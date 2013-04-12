package pt.ist.bennu.scheduler.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.bennu.scheduler.domain.SchedulerSystem;
import pt.ist.bennu.service.Service;

@WebListener
public class SchedulerContextListener implements ServletContextListener {
    private static boolean initialized = false;

    @Service
    public void initialize() {
        if (!initialized) {
            SchedulerSystem.getInstance().initTasks();
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
