package pt.ist.bennu.scheduler.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.bennu.scheduler.domain.SchedulerSystem;

@WebListener
public class SchedulerContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        SchedulerSystem.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        SchedulerSystem.destroy();
    }
}
