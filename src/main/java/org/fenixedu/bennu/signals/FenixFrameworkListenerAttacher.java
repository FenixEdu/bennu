package org.fenixedu.bennu.signals;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FenixFrameworkListenerAttacher implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Signal.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Signal.shutdown();
    }
}
