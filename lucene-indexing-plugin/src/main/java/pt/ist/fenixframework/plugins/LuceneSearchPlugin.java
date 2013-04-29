package pt.ist.fenixframework.plugins;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.plugins.luceneIndexing.IndexListener;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.LuceneSearchPluginRoot;

@WebListener
public class LuceneSearchPlugin implements ServletContextListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(LuceneSearchPlugin.class.getName());
    private static boolean initialized = false;

    @Atomic
    public void initialize() {
        if (!initialized) {
            LuceneSearchPluginRoot.getInstance();
            FenixFramework.getTransactionManager().addCommitListener(new IndexListener());
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
