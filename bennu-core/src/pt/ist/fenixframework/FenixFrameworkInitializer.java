package pt.ist.fenixframework;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myorg._development.PropertiesManager;
import pt.ist.fenixWebFramework.FenixWebFramework;

public class FenixFrameworkInitializer {

    public static final String[] CONFIG_PATHS;

    private static File getApplicationDir() {
	final URL url = FenixFrameworkInitializer.class.getResource("/configuration.properties");
	try {
	    return new File(url.toURI()).getParentFile();
	} catch (final URISyntaxException e) {
	    throw new Error(e);
	}
    }

    static {
	System.out.println("Initializing bennu.");

	final String preInitClassnames = PropertiesManager.getProperty("pre.init.classnames");
	if (preInitClassnames != null) {
	    final String[] classnames = preInitClassnames.split(",");
	    for (final String classname : classnames) {
		if (classname != null && !classname.isEmpty()) {
		    try {
			Class.forName(classname.trim());
		    } catch (final ClassNotFoundException e) {
			throw new Error(e);
		    }
		}
	    }
	}

	final File dir = getApplicationDir();
	final List<String> urls = new ArrayList<String>();
	for (final File file : dir.listFiles()) {
	    if (file.isFile() && file.getName().endsWith(".dml")) {
		try {
		    urls.add(file.getCanonicalPath());
		} catch (IOException e) {
		    throw new Error(e);
		}
	    }
	}
	
	Collections.sort(urls);
	final String[] paths = new String[urls.size()];
	for (int i = 0; i < urls.size(); i++) {
	    paths[i] = urls.get(i);
	}
	try {
	    CONFIG_PATHS = paths;
	    FenixWebFramework.bootStrap(PropertiesManager.getFenixFrameworkConfig(paths));
	} catch (Throwable t) {
	    t.printStackTrace();
	    throw new Error(t);
	}

    }

}
