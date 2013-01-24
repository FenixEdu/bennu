package pt.ist.bennu.core.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.Duration;
import org.joda.time.format.ISOPeriodFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilingFilter implements Filter {
	private final Logger logger = LoggerFactory.getLogger(ProfilingFilter.class);

	@Override
	public void init(final FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		if (logger.isInfoEnabled()) {
			final long start = System.currentTimeMillis();
			try {
				chain.doFilter(request, response);
			} finally {
				log(((HttpServletRequest) request).getRequestURI(), new Duration(start, System.currentTimeMillis()));
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	private void log(String uri, Duration duration) {
		logger.info(String.format("[%s] - %s", ISOPeriodFormat.standard().print(duration.toPeriod()), uri));
	}
}
