/*
 * Created on 2005/05/13
 */
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.ist.bennu.core.util.Language;

/**
 * 
 * @author Luis Cruz
 */
public class I18NFilter implements Filter {
	public static final String LOCALE_KEY = I18NFilter.class.getName() + "_LOCAL_KEY";

	ServletContext servletContext;

	FilterConfig filterConfig;

	@Override
	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
		this.servletContext = filterConfig.getServletContext();
	}

	@Override
	public void destroy() {
		this.servletContext = null;
		this.filterConfig = null;
	}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;

		HttpSession session = request.getSession(true);
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
		filterChain.doFilter(request, response);
	}
}
