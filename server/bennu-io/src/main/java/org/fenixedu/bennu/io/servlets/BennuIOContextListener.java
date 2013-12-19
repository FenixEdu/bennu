package org.fenixedu.bennu.io.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.io.FileDeleterThread;
import org.fenixedu.bennu.io.domain.FileSupport;

import pt.ist.fenixframework.Atomic;

@WebListener
public class BennuIOContextListener implements ServletContextListener {
    private static boolean initialized = false;

    @Atomic
    public void initialize() {
        if (!initialized) {
            FileSupport.getInstance();
            Thread thread = new Thread(new FileDeleterThread());
            thread.start();
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
