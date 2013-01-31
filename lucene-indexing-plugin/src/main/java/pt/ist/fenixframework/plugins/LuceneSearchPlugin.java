package pt.ist.fenixframework.plugins;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.FenixFrameworkPlugin;
import pt.ist.fenixframework.plugins.luceneIndexing.IndexListener;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.LuceneSearchPluginRoot;
import pt.ist.fenixframework.pstm.TopLevelTransaction;

@WebListener
public class LuceneSearchPlugin implements FenixFrameworkPlugin, ServletContextListener {
	public static final Logger LOGGER = Logger.getLogger(LuceneSearchPlugin.class.getName());
	private static boolean initialized = false;

	@Override
	public List<URL> getDomainModel() {
		URL resource = getClass().getResource("/luceneSearch-plugin.dml");
		return Collections.singletonList(resource);
	}

	@Override
	@Service
	public void initialize() {
		if (!initialized) {
			LuceneSearchPluginRoot.getInstance();
			TopLevelTransaction.addCommitListener(new IndexListener());
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
