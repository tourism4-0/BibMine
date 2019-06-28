package si.fri.bibmine.beans.worker;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class TimerBean {
    private static final Logger LOG = LogManager.getLogger(TimerBean.class.getName());

    private static final String TIMER_PERIOD_ENV_NAME = "timerPeriod";
    private static Integer timerPeriod = 86400000; // once per day interval

    @Inject
    private TaskBean taskBean;

    @PersistenceUnit(unitName = "bibmine-jpa")
    protected EntityManagerFactory emf;

    /**
     * Post construct initialization method.
     */
    private void initialize(@Observes @Initialized(ApplicationScoped.class) Object init) {

        LOG.trace("TimerBean initialized.");

        // get period in seconds
        if (System.getenv().containsKey(TIMER_PERIOD_ENV_NAME)) {
            timerPeriod = Integer.parseInt(System.getenv().get(TIMER_PERIOD_ENV_NAME)) * 1000;
        }

        LOG.trace("TimerBean variables initialized: timerPeriod=" + timerPeriod  + ".");

        // start task checker
        taskCheckerInit();

    }

    /**
     * Pre destroy cleanup method.
     */
    private void cleanup(@Observes @Destroyed(ApplicationScoped.class) Object init) {
        LOG.trace("Bean {} cleanup.", TimerBean.class.getName());
    }

    /**
     * Method for initializing task checker.
     */
    private void taskCheckerInit() {
        TimerTask taskCheckProducer = new TimerTask() {

            @Override
            public void run() {
                taskBean.taskCheck();
            }

        };

        Timer timer = new Timer();
        timer.schedule(taskCheckProducer, timerPeriod, timerPeriod);
    }

}
