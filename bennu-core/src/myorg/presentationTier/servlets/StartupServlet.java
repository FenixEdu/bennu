package myorg.presentationTier.servlets;

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
	String domainModelPath = getServletContext().getRealPath(getInitParameter("domainmodel"));
	FenixWebFramework.initialize(PropertiesManager.getFenixFrameworkConfig(domainModelPath));
	MyOrg.initialize(FenixWebFramework.getConfig());

	final String managerUsernames = PropertiesManager.getProperty("manager.usernames");
	Authenticate.initRole(RoleType.MANAGER, managerUsernames);
    }

}
