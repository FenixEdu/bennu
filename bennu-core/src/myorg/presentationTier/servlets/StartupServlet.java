package myorg.presentationTier.servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import myorg._development.PropertiesManager;
import myorg.applicationTier.Authenticate;
import myorg.domain.MyOrg;
import myorg.domain.RoleType;
import pt.ist.fenixWebFramework.FenixWebFramework;

public class StartupServlet extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	final String domainmodelPath = getServletContext().getRealPath(getInitParameter("domainmodelPath"));
	final File dir = new File(domainmodelPath);
	final List<String> urls = new ArrayList<String>();
	for (final File file : dir.listFiles()) {
	    if (file.isFile() && file.getName().endsWith(".dml")) {
		try {
		    urls.add(file.getCanonicalPath());
		} catch (IOException e) {
		    e.printStackTrace();
		    throw new ServletException(e);
		}
	    }
	}
	final String[] paths = new String[urls.size()];
	for (int i = 0; i < urls.size(); i++) {
	    paths[i] = urls.get(i);
	}
	Collections.sort(urls);
	try {
	    FenixWebFramework.initialize(PropertiesManager.getFenixFrameworkConfig(paths));
	} catch (Throwable t) {
	    t.printStackTrace();
	    throw new Error(t);
	}
	MyOrg.initialize(FenixWebFramework.getConfig());

	final String managerUsernames = PropertiesManager.getProperty("manager.usernames");
	Authenticate.initRole(RoleType.MANAGER, managerUsernames);
    }

}
