package si.fri.bibmine.beans.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.json.JSONArray;
import si.fri.bibmine.models.Article;
import si.fri.bibmine.models.ArticleQuery;
import si.fri.bibmine.models.Query;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.ws.rs.InternalServerErrorException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static si.fri.bibmine.util.ScopusClientUtil.*;

@ApplicationScoped
public class ArticleTimerEntityBean {
    private static final Logger LOG = LogManager.getLogger(ArticleTimerEntityBean.class.getName());

    @PersistenceUnit(unitName = "bibmine-jpa")
    protected EntityManagerFactory emf;

    public void getScopusArticles() {
        EntityManager em = emf.createEntityManager();

        List<si.fri.bibmine.models.Query> queries = getAllQueries();

        queries.forEach(query -> {
            JSONArray articles = getArticlesList(query.getQuery().replaceAll(" ", ""));
            JSONArray filteredArticles = filterScopusArticles(articles);

            LOG.info("TOTAL AMOUNT OF NEW ARTICLES ON THIS ITERATION: {}/200", filteredArticles.length());

            for (int i = 0; i < filteredArticles.length(); i++) {

                Article article = new Article();

                try {
                    article = em.createNamedQuery("Article.findArticle", Article.class)
                            .setParameter("aid", filteredArticles.getJSONObject(i).getString("dc:identifier").substring(10))
                            .getSingleResult();
                } catch (NoResultException e) {
                    String dataXml = getArticle(filteredArticles.getJSONObject(i).getString("prism:url"));
                    article.setXml(dataXml.getBytes(StandardCharsets.UTF_8));
                    article.setAid(filteredArticles.getJSONObject(i).getString("dc:identifier").substring(10));

                    si.fri.bibmine.models.parse.Article articleParse = convertXmlToJson(dataXml, filteredArticles.getJSONObject(i).getString("dc:identifier").substring(10));

                    ObjectMapper mapper = new ObjectMapper();
                    String json = "";
                    try {
                        json = mapper.writeValueAsString(articleParse);
                    } catch (JsonProcessingException ejp) {
                        ejp.printStackTrace();
                    }

                    article.setJson(json.getBytes(StandardCharsets.UTF_8));

                    em.getTransaction().begin();
                    em.persist(article);
                    em.getTransaction().commit();
                }

                try {
                    article = em.createNamedQuery("Article.findArticle", Article.class)
                            .setParameter("aid", article.getAid())
                            .getSingleResult();
                } catch (NoResultException e) {
                    //
                }

                ArticleQuery articleQuery = new ArticleQuery();

                try {
                    articleQuery = em.createNamedQuery("ArticleQuery.findArticleQuery", ArticleQuery.class)
                            .setParameter("queryId", query.getId())
                            .setParameter("articleId", article.getId())
                            .getSingleResult();
                } catch (NoResultException e) {
                    articleQuery.setArticle(article);
                    articleQuery.setQuery(query);
                    articleQuery.setIdArticle(article.getId());
                    articleQuery.setIdQuery(query.getId());
                    try {
                        em.getTransaction().begin();
                        em.persist(articleQuery);
                        em.getTransaction().commit();
                        LOG.info("Database update SUCCESSFULL");
                    } catch (PersistenceException pe) {
                        LOG.warn(e);
                        throw new PersistenceException(pe);
                    }
                }
            }
        });
    }

    /**
     * Filter out already persisted articles in local database
     *
     * @param scopusArticles
     * @return
     */
    public JSONArray filterScopusArticles(JSONArray scopusArticles) {
        EntityManager em = emf.createEntityManager();

        List<Article> dbArticles = new ArrayList<>();

        try {
            em.getTransaction().begin();
            dbArticles = em.createNamedQuery("Article.findAllArticles", Article.class)
                    .getResultList();
            em.getTransaction().commit();
        } catch (NoResultException e) {
            //
        }
        em.close();

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

    public List<Query> getAllQueries() {
        EntityManager em = emf.createEntityManager();
        List<si.fri.bibmine.models.Query> queries;

        try {
            queries = em.createNamedQuery("Query.findAllQueries", si.fri.bibmine.models.Query.class).getResultList();
        } catch (NoResultException e) {
            throw new InternalServerErrorException("No queries available from data base");
        }

        em.close();
        return queries;
    }
}