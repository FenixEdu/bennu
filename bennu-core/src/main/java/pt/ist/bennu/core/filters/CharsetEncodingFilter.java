package pt.ist.bennu.core.filters;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharsetEncodingFilter implements Filter {

	private static String defaultCharset = Charset.defaultCharset().name();

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		final String defaultCharset = filterConfig.getInitParameter("defaultCharset");
		if (defaultCharset != null && !defaultCharset.isEmpty() && Charset.forName(defaultCharset) != null) {
			CharsetEncodingFilter.defaultCharset = defaultCharset;
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
			ServletException {
		if (request.getCharacterEncoding() == null) {
			request.setCharacterEncoding(defaultCharset);
		}
		if (response.getCharacterEncoding() == null) {
			response.setCharacterEncoding(defaultCharset);
		}
		filterChain.doFilter(request, response);
	}

}
