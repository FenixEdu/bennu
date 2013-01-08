package pt.ist.bennu.core.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import pt.ist.fenixframework.pstm.RequestInfo;
import pt.ist.fenixframework.pstm.Transaction;

public class CloseTransactionFilter implements Filter {

	@Override
	public void init(FilterConfig config) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {
			RequestInfo.setRequestURI(((HttpServletRequest) request).getRequestURI());
		}

		try {
			Transaction.begin(true);
			Transaction.currentFenixTransaction().setReadOnly();
			chain.doFilter(request, response);
		} finally {
			Transaction.forceFinish();
			RequestInfo.clear();
		}
	}

}
