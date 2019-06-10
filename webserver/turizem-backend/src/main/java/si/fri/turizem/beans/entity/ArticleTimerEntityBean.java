package si.fri.turizem.beans.entity;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.json.JSONArray;
import si.fri.turizem.models.Article;
import si.fri.turizem.models.ArticleQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.ws.rs.InternalServerErrorException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static si.fri.turizem.util.ScopusClientUtil.*;

@ApplicationScoped
public class ArticleTimerEntityBean {
    private static final Logger LOG = LogManager.getLogger(ArticleTimerEntityBean.class.getName());

    @PersistenceUnit(unitName = "turizem-jpa")
    protected EntityManagerFactory emf;

    public void getScopusArticles() {
        EntityManager em = emf.createEntityManager();

        List<si.fri.turizem.models.Query> queries;

        try {
            queries = em.createNamedQuery("Query.findAllQueries", si.fri.turizem.models.Query.class).getResultList();
        } catch (NoResultException e) {
            throw new InternalServerErrorException("No queries available from data base");
        }

        queries.forEach(query -> {
            JSONArray articles = getArticlesList(query.getQuery().replaceAll(" ", ""));

            for (int i = 0; i < articles.length(); i++) {

                Article article = new Article();

                try {
                    em.getTransaction().begin();
                    article = em.createNamedQuery("Article.findArticle", Article.class)
                            .setParameter("aid", articles.getJSONObject(i).getString("dc:identifier").substring(10))
                            .getSingleResult();
                } catch (NoResultException e) {
                    String dataXml = getArticle(articles.getJSONObject(i).getString("prism:url"));
                    article.setXml(dataXml.getBytes(StandardCharsets.UTF_8));
                    article.setJson(convertXmlToJson(dataXml, articles.getJSONObject(i).getString("dc:identifier").substring(10)).getBytes(StandardCharsets.UTF_8));
                    article.setAid(articles.getJSONObject(i).getString("dc:identifier").substring(10));

                    em.persist(article);
                    em.getTransaction().commit();
                }

                try {
                    em.getTransaction().begin();
                    article = em.createNamedQuery("Article.findArticle", Article.class)
                            .setParameter("aid", article.getAid())
                            .getSingleResult();
                } catch (NoResultException e) {
                    //
                    LOG.info("ARTICLE EXCEPTION pre aq");
                }

                ArticleQuery articleQuery = new ArticleQuery();

                try {
                    articleQuery = em.createNamedQuery("ArticleQuery.findArticleQuery", ArticleQuery.class)
                            .setParameter("query", query)
                            .setParameter("article", article)
                            .getSingleResult();
                } catch (NoResultException e) {
                    articleQuery.setArticle(article);
                    articleQuery.setQuery(query);
                    articleQuery.setIdArticle(article.getId());
                    articleQuery.setIdQuery(query.getId());

                    try {
                        em.persist(articleQuery);
                        em.getTransaction().commit();
                    } catch (PersistenceException pe) {
                        LOG.warn(e);
                        throw new PersistenceException(pe);
                    }
                }
            }
        });
    }
}
