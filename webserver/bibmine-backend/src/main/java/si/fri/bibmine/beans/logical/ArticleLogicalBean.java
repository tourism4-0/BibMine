package si.fri.bibmine.beans.logical;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.json.JSONArray;
import si.fri.bibmine.beans.entity.ArticleEntityBean;
import si.fri.bibmine.beans.entity.QueryEntityBean;
import si.fri.bibmine.models.Article;
import si.fri.bibmine.models.Query;
import si.fri.bibmine.util.ScopusClientUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static si.fri.bibmine.util.ScopusClientUtil.getArticle;

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
            article.setAid(scopusArticles.getJSONObject(i).getString("dc:identifier").substring(10));

            si.fri.bibmine.models.parse.Article articleParse = ScopusClientUtil.convertXmlToJson(dataXml, scopusArticles.getJSONObject(i).getString("dc:identifier").substring(10));

            ObjectMapper mapper = new ObjectMapper();
            String json = "";
            try {
                json = mapper.writeValueAsString(articleParse);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            article.setJson(json.getBytes(StandardCharsets.UTF_8));

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
        String dataString = "";
        if(article != null){
            dataString = new String(article.getJson(), StandardCharsets.UTF_8);
        }
        else {
            dataString = "Article " + aid + " not found.";
        }
    return dataString;
    }

    /**
     * Get raw article data in XML
     *
     * @param aid
     * @return
     */
    public String getArticleXML(String aid) {
        Article article = articleEntityBean.getArticle(aid);
        String dataString = "";
        if(article != null){
            dataString = new String(article.getXml(), StandardCharsets.UTF_8);
        }
        else {
            dataString = "Article " + aid + " not found.";
        }
        return dataString;
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
