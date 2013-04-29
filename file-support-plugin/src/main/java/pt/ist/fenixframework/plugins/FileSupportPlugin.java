package pt.ist.fenixframework.plugins;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.plugins.fileSupport.FileDeleterThread;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport;

@WebListener
public class FileSupportPlugin implements ServletContextListener {
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
