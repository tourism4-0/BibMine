package si.fri.turizem.beans.worker;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import si.fri.turizem.beans.entity.ArticleTimerEntityBean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@ApplicationScoped
public class TaskBean {

    private static final Logger LOG = LogManager.getLogger(TaskBean.class.getName());

    @Inject
    private ArticleTimerEntityBean articleTimerEntityBean;

    @PersistenceUnit(unitName = "turizem-jpa")
    EntityManagerFactory emf;


    /**
     * Post construct initialization method.
     */
    @PostConstruct
    private void initialize() {
        LOG.trace("TaskBean initialized.");
    }

    /**
     * Task checker method.
     */
    public void taskCheck() {
        LOG.trace("Task check started.");
        // periodically get articles
        getArticles();
    }

    private void getArticles() {
        LOG.trace("getArticles task check started.");
        articleTimerEntityBean.getScopusArticles();
    }
}
