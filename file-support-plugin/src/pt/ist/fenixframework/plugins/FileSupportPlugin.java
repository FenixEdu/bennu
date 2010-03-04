package pt.ist.fenixframework.plugins;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import pt.ist.fenixframework.FenixFrameworkPlugin;
import pt.ist.fenixframework.plugins.fileSupport.FileDeleterThread;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport;

public class FileSupportPlugin implements FenixFrameworkPlugin {

    @Override
    public List<URL> getDomainModel() {
	URL resource = getClass().getResource("/file-plugin.dml");
	return Collections.singletonList(resource);
    }

    @Override
    public void initialize() {
	FileSupport.getInstance();
	Thread thread = new Thread(new FileDeleterThread());
	thread.start();
    }
}
