package myorg.presentationTier.servlets;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;

public class ActionServletWrapper extends ActionServlet {

    private static class ServletConfigWrapper implements ServletConfig {

	private final ServletConfig servletConfig;

	public static final Map<String, String> parameterMap = new HashMap<String, String>();
	static {
	    parameterMap.put("config", "/WEB-INF/conf/struts-default.xml");
	    parameterMap.put("application", "resources.MyorgResources");

	    parameterMap.put("debug", "3");
	    parameterMap.put("detail", "3");
	    parameterMap.put("validating", "true");
	}

	public ServletConfigWrapper(final ServletConfig servletConfig) {
	    this.servletConfig = servletConfig;
	}

	public String getInitParameter(final String name) {
	    final String parameter = parameterMap.get(name);
	    return parameter == null ? servletConfig == null ? null : servletConfig.getInitParameter(name) : parameter;
	}

	public Enumeration getInitParameterNames() {
	    return new Enumeration() {

		private final Enumeration enumeration = servletConfig == null ? null : servletConfig.getInitParameterNames();

		private final Iterator iterator = parameterMap.keySet().iterator();

		public boolean hasMoreElements() {
		    return iterator.hasNext() || (enumeration != null && enumeration.hasMoreElements());
		}

		public Object nextElement() {
		    return enumeration != null && enumeration.hasMoreElements() ? enumeration.nextElement() : iterator.next();
		}

	    };
	}

	public ServletContext getServletContext() {
	    return servletConfig == null ? null : servletConfig.getServletContext();
	}

	public String getServletName() {
	    return servletConfig == null ? null : servletConfig.getServletName();
	}

    }

    public static ServletConfig servletConfig = null;

    @Override
    public void init(final ServletConfig config) throws ServletException {
	final ServletConfigWrapper servletConfigWrapper = new ServletConfigWrapper(config);
	super.init(servletConfigWrapper);
	this.servletConfig = config;
    }

}
