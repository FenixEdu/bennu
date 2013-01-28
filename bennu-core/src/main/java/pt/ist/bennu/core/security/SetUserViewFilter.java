package pt.ist.bennu.core.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetUserViewFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(SetUserViewFilter.class);

	public static final String USER_SESSION_ATTRIBUTE = "USER_SESSION_ATTRIBUTE";

	@Override
	public void init(FilterConfig config) {
	}

	@Override
	public void destroy() {
	}

	protected HttpSession getHttpSession(final ServletRequest servletRequest) {
		if (servletRequest instanceof HttpServletRequest) {
			final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
			return httpServletRequest.getSession(false);
		}
		return null;
	}

	protected SessionUserWrapper getUserView(final ServletRequest servletRequest) {
		final HttpSession httpSession = getHttpSession(servletRequest);
		return (SessionUserWrapper) (httpSession == null ? null : httpSession.getAttribute(USER_SESSION_ATTRIBUTE));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		SessionUserWrapper wrapper = getUserView(request);

		if (wrapper != null) {
			final DateTime lastLogoutDateTime = wrapper.getLastLogoutDateTime();
			if (lastLogoutDateTime == null || wrapper.getUserCreationDateTime().isAfter(lastLogoutDateTime)) {
				logger.trace("wrapper is setted");
				UserView.setSessionUserWrapper(wrapper);
			} else {
				logger.trace("loginDateTime invalid");
				UserView.setSessionUserWrapper(null);
			}
		} else {
			logger.trace("wrapper is null");
			UserView.setSessionUserWrapper(null);
		}

		try {
			chain.doFilter(request, response);
		} finally {
			logger.trace("finally : wrapper is null");
			UserView.setSessionUserWrapper(null);
		}
	}
}
