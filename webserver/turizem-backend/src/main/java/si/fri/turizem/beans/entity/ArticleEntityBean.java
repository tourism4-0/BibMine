package si.fri.turizem.beans.entity;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import si.fri.turizem.models.Article;
import si.fri.turizem.models.ArticleQuery;
import si.fri.turizem.models.Query;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.List;

@RequestScoped
public class ArticleEntityBean {
    private static final Logger LOG = LogManager.getLogger(ArticleEntityBean.class.getName());

    @Inject
    private QueryEntityBean queryEntityBean;

    @Inject
    private ArticleQueryEntityBean articleQueryEntityBean;

    @PersistenceContext
    private EntityManager em;

    /**
     * Get specific article from DB
     *
     * @param aid
     * @return article
     */
    public Article getArticle(String aid) {
        try {
            return em.createNamedQuery("Article.findArticle", Article.class)
                    .setParameter("aid", aid)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOG.warn("Article with aid {} not found in Database. It will be now persisted.", aid);
            return null;
        }
    }

    /**
     * Get all articles from database
     *
     * @return articles
     */
    public List<Article> getAllArticles() {
        List<Article> articles = em.createNamedQuery("Article.findAllArticles", Article.class)
                .getResultList();

        return articles;
    }

    /**
     * Persist article in DB
     *
     * @param article
     * @param query
     */
    @Transactional
    public void persistArticle(Article article, Query query) {
        if (article != null && query != null) {
            try {
                if (getArticle(article.getAid()) == null)
                    em.persist(article);
                if (queryEntityBean.getQuery(query.getQuery()) == null)
                    em.persist(query);

                Article article1 = getArticle(article.getAid());
                Query query1 = queryEntityBean.getQuery(query.getQuery());

                ArticleQuery articleQuery = new ArticleQuery();

                if(articleQueryEntityBean.getArticleQueries(article1, query1) == null){
                    articleQuery.setArticle(article1);
                    articleQuery.setQuery(query1);

                    articleQuery.setIdArticle(article1.getId());
                    articleQuery.setIdQuery(query1.getId());

                    em.persist(articleQuery);
                }
            } catch (PersistenceException e) {
                LOG.warn(e);
                throw new PersistenceException(e);
            }
        } else {
            LOG.warn("Article {} or Query {} is null", article, query);
        }
    }

    /**
     * Delete article from DB
     *
     * @param aid
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteArticle(String aid) {
        Article article = getArticle(aid);
        if (article != null)
            em.remove(article);
    }
}
