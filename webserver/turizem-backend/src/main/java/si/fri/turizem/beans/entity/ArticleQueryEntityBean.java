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
public class ArticleQueryEntityBean {
    private static final Logger LOG = LogManager.getLogger(ArticleQueryEntityBean.class.getName());

    @Inject
    private QueryEntityBean queryEntityBean;

    @PersistenceContext
    private EntityManager em;

    /**
     * Get specific article from DB
     *
     * @param aid
     * @return article
     */
    public ArticleQuery getArticleQueries(Article article, Query query) {
        try {
            return em.createNamedQuery("ArticleQuery.findArticleQuery", ArticleQuery.class)
                    .setParameter("articleId", article.getId())
                    .setParameter("queryId", query.getId())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
