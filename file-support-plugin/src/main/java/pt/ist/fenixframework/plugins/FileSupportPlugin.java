package pt.ist.fenixframework.plugins;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.FenixFrameworkPlugin;
import pt.ist.fenixframework.plugins.fileSupport.FileDeleterThread;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport;

@WebListener
public class FileSupportPlugin implements FenixFrameworkPlugin, ServletContextListener {
	private static boolean initialized = false;

	@Override
	public List<URL> getDomainModel() {
		URL resource = getClass().getResource("/file-plugin.dml");
		return Collections.singletonList(resource);
	}

	@Override
	@Service
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
