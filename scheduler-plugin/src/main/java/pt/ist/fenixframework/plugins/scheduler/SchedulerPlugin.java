package pt.ist.fenixframework.plugins.scheduler;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.bennu.service.Service;
import pt.ist.fenixframework.FenixFrameworkPlugin;
import pt.ist.fenixframework.plugins.scheduler.domain.SchedulerSystem;

@WebListener
public class SchedulerPlugin implements FenixFrameworkPlugin, ServletContextListener {
    private static boolean initialized = false;

    @Override
    public List<URL> getDomainModel() {
        return Collections.singletonList(getClass().getResource("/scheduler-plugin.dml"));
    }

    @Override
    @Service
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
