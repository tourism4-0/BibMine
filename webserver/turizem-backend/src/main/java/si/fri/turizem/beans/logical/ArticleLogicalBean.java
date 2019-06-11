package si.fri.turizem.beans.logical;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import si.fri.turizem.beans.entity.ArticleEntityBean;
import si.fri.turizem.beans.entity.QueryEntityBean;
import si.fri.turizem.models.Article;
import si.fri.turizem.models.Query;
import si.fri.turizem.util.ScopusClientUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static si.fri.turizem.util.ScopusClientUtil.getArticle;
import static si.fri.turizem.util.ScopusClientUtil.getArticleFullText;

@RequestScoped
public class ArticleLogicalBean {
    private static final Logger LOG = LogManager.getLogger(ArticleLogicalBean.class.getName());

    @Inject
    private ArticleEntityBean articleEntityBean;

    @Inject
    private QueryEntityBean queryEntityBean;

    /**
     * Prepare entity for persisting in DB
     *
     * @param q
     */
    public void persistArticles(String q) {

        JSONArray scopusArticles = ScopusClientUtil.getArticlesList(q);
        List<Article> dbArticles = articleEntityBean.getAllArticles();

        if(queryEntityBean.getQuery(q) != null)
            scopusArticles = filterScopusArticles(scopusArticles, dbArticles);

        LOG.info("TOTAL AMOUNT OF NEW ARTICLES ON THIS ITERATION: {}/200", scopusArticles.length());

        for (int i = 0; i < scopusArticles.length(); i++) {
            Article article = new Article();
            String dataXml = getArticle(scopusArticles.getJSONObject(i).getString("prism:url"));
            article.setXml(dataXml.getBytes(StandardCharsets.UTF_8));
            article.setJson(ScopusClientUtil.convertXmlToJson(dataXml, scopusArticles.getJSONObject(i).getString("dc:identifier").substring(10)).getBytes(StandardCharsets.UTF_8));
            article.setAid(scopusArticles.getJSONObject(i).getString("dc:identifier").substring(10));

            Query query = new Query();
            query.setQuery(q);

            articleEntityBean.persistArticle(article, query);
        }
    }

    /**
     * Get full text of article
     *
     * @param aid
     * @return
     */
    public String getArticleFull(String aid) {
        Article article = articleEntityBean.getArticle(aid);
        String dataString = new String(article.getJson(), StandardCharsets.UTF_8);
        JSONObject fullText = new JSONObject(dataString);

        if (fullText.getJSONArray("content").length() != 0)
            return fullText.getJSONArray("content").toString();
        else
            return null;
    }

    /**
     * Filter out already persisted articles in local database
     *
     * @param scopusArticles
     * @param dbArticles
     * @return
     */
    public JSONArray filterScopusArticles(JSONArray scopusArticles, List<Article> dbArticles) {

        for (int i = 0; i < scopusArticles.length(); i++) {
            for (int j = 0; j < dbArticles.size(); j++) {
                if (scopusArticles.getJSONObject(i).getString("dc:identifier").substring(10).equals(dbArticles.get(j).getAid().trim())) {
                    scopusArticles.remove(i);
                    break;
                }
            }
        }
        return scopusArticles;
    }
}
