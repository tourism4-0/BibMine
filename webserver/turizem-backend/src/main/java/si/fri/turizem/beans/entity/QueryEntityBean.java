package si.fri.turizem.beans.entity;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import si.fri.turizem.models.Query;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@RequestScoped
public class QueryEntityBean {
    private static final Logger LOG = LogManager.getLogger(QueryEntityBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    /**
     * Get specific article from DB
     *
     * @param query
     * @return article
     */
    public Query getQuery(String query) {
        try {
            return em.createNamedQuery("Query.findQuery", Query.class)
                    .setParameter("query", query)
                    .getSingleResult();
        } catch (Exception e) {
            LOG.warn("Query {} not found.",query);
            return null;
        }
    }

    /**
     * Get all queries from database
     *
     * @return queries
     */
    public List<Query> getAllQueries() {
        List<Query> queries = em.createNamedQuery("Query.findAllQueries", Query.class)
                .getResultList();

        return queries;
    }


    /**
     * Delete query from DB
     *
     * @param q
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteQuery(String q) {
        Query query = getQuery(q);
        if (query != null)
            em.remove(query);
    }
}
