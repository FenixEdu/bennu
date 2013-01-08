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

public class SetUserViewFilter implements Filter {
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
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException {
		SessionUserWrapper wrapper = getUserView(servletRequest);
		if (wrapper != null) {
			final DateTime lastLogoutDateTime = wrapper.getLastLogoutDateTime();
			if (lastLogoutDateTime == null || wrapper.getUserCreationDateTime().isAfter(lastLogoutDateTime)) {
				UserView.setSessionUserWrapper(wrapper);
			} else {
				UserView.setSessionUserWrapper(null);
			}
		} else {
			UserView.setSessionUserWrapper(null);
		}
		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			UserView.setSessionUserWrapper(null);
		}
	}
}
