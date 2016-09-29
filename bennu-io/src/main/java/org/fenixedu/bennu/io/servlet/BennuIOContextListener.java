package org.fenixedu.bennu.io.servlet;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.io.domain.FileDeleterThread;
import org.fenixedu.bennu.io.domain.FileSupport;

import pt.ist.fenixframework.Atomic;

@WebListener
public class BennuIOContextListener implements ServletContextListener {
    private static boolean initialized = false;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Atomic
    public void initialize() {
        if (!initialized) {
            FileSupport.getInstance();
            initialized = true;
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        initialize();
        FileDeleterThread thread = new FileDeleterThread();
        thread.run();
        executor.scheduleAtFixedRate(thread, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //oh well
        }
    }
}
