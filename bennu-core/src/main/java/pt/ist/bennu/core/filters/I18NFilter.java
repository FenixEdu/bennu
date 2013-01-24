package pt.ist.bennu.core.filters;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import pt.ist.bennu.core.util.Language;

/**
 * 
 * @author Luis Cruz
 */
public class I18NFilter implements Filter {
	public static final String LOCALE_KEY = I18NFilter.class.getName() + "_LOCAL_KEY";

	FilterConfig config;

	ServletContext servletContext;

	@Override
	public void init(final FilterConfig config) throws ServletException {
		this.config = config;
		this.servletContext = config.getServletContext();
	}

	@Override
	public void destroy() {
		this.servletContext = null;
		this.config = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession(true);
		String requestLocale = request.getParameter("locale");
		Locale locale;
		if (requestLocale != null) {
			String[] localeParts = requestLocale.split("_");
			locale = localeParts.length > 1 ? new Locale(localeParts[0], localeParts[1]) : new Locale(requestLocale);
			session.setAttribute(LOCALE_KEY, locale);
		} else {
			locale = (Locale) session.getAttribute(LOCALE_KEY);
			if (locale == null) {
				locale = Language.getDefaultLocale();
			}
		}
		Language.setLocale(locale);
		chain.doFilter(request, response);
	}
}
