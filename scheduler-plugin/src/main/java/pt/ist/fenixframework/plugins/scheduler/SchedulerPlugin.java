package pt.ist.fenixframework.plugins.scheduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.plugins.scheduler.domain.SchedulerSystem;

@WebListener
public class SchedulerPlugin implements ServletContextListener {
    private static boolean initialized = false;

    @Atomic
    public void initialize() {
        if (!initialized) {
            SchedulerSystem.getInstance();
            initialized = true;
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
