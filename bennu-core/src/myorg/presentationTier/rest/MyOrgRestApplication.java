/**
 * 
 */
package myorg.presentationTier.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

import pt.ist.fenixWebFramework.servlets.rest.RestAnnotationProcessor;
import pt.utl.ist.fenix.tools.util.FileUtils;

/**
 * @author Shezad Anavarali Date: Sep 8, 2009
 * 
 */
public class MyOrgRestApplication extends Application {

    @Override
    public Restlet createRoot() {
	Router router = new Router(getContext());

	for (final Entry<String, Class<? extends ServerResource>> entry : loadActionsFromFile().entrySet()) {
	    router.attach(entry.getKey(), entry.getValue());
	}

	return router;
    }

    private Map<String, Class<? extends ServerResource>> loadActionsFromFile() {
	final Map<String, Class<? extends ServerResource>> resourcesClasses = new HashMap<String, Class<? extends ServerResource>>();
	final InputStream inputStream = getClass().getResourceAsStream("/.restAnnotationLog");
	if (inputStream != null) {
	    try {
		final String contents = FileUtils.readFile(inputStream);
		for (final String classname : contents.split(RestAnnotationProcessor.ENTRY_SEPERATOR)) {
		    try {
			String[] values = classname.split(RestAnnotationProcessor.LINE_SEPERATOR);
			if (values.length == 2) {
			    ClassLoader loader = Thread.currentThread().getContextClassLoader();
			    Class<?> type = loader.loadClass(values[0]);
			    resourcesClasses.put(values[1], (Class<? extends ServerResource>) type);
			}

		    } catch (final ClassNotFoundException e) {
			e.printStackTrace();
		    }
		}
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	}
	return resourcesClasses;
    }

}
