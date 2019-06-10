package si.fri.turizem.beans.logical;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import si.fri.turizem.beans.entity.ArticleEntityBean;
import si.fri.turizem.models.Article;
import si.fri.turizem.models.Query;
import si.fri.turizem.util.ScopusClientUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

import static si.fri.turizem.util.ScopusClientUtil.getArticle;
import static si.fri.turizem.util.ScopusClientUtil.getArticleFullText;

@RequestScoped
public class ArticleLogicalBean {
    private static final Logger LOG = LogManager.getLogger(ArticleLogicalBean.class.getName());

    @Inject
    private ArticleEntityBean articleEntityBean;

    /**
     * Prepare entity for persisting in DB
     *
     * @param q
     */
    public void persistArticles(String q) {

        JSONArray articles = ScopusClientUtil.getArticlesList(q);

        for (int i = 0; i < articles.length(); i++) {
            Article article = new Article();
            String dataXml = getArticle(articles.getJSONObject(i).getString("prism:url"));
            article.setXml(dataXml.getBytes(StandardCharsets.UTF_8));
            article.setJson(ScopusClientUtil.convertXmlToJson(dataXml, articles.getJSONObject(i).getString("dc:identifier").substring(10)).getBytes(StandardCharsets.UTF_8));
            article.setAid(articles.getJSONObject(i).getString("dc:identifier").substring(10));

            Query query = new Query();
            query.setQuery(q);

            articleEntityBean.persistArticle(article, query);
        }
    }

    public String getArticleFull(String aid) {
        Article article = articleEntityBean.getArticle(aid);
        String dataString = new String(article.getJson(), StandardCharsets.UTF_8);
        JSONObject fullText = new JSONObject(dataString);

        if(fullText.getJSONArray("content").length() != 0)
            return fullText.getJSONArray("content").toString();
        else
            return null;
    }
}
