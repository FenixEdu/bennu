package myorg.presentationTier.servlets.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import myorg.presentationTier.actions.AuthenticationAction;
import pt.ist.fenixWebFramework.FenixWebFramework;
import edu.yale.its.tp.cas.client.CASAuthenticationException;
import edu.yale.its.tp.cas.client.CASReceipt;
import edu.yale.its.tp.cas.client.ProxyTicketValidator;

public class CasAuthenticationFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
	    throws IOException, ServletException {
	final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
	final String ticket = httpServletRequest.getParameter("ticket");
	if (ticket != null) {
	    AuthenticationAction.logout(httpServletRequest);

	    final String requestURL = httpServletRequest.getRequestURL().toString();
	    try {
		final CASReceipt receipt = getCASReceipt(ticket, requestURL);
		final String username = receipt.getUserName();
		AuthenticationAction.login(httpServletRequest, username, null);
	    } catch (CASAuthenticationException e) {
		e.printStackTrace();
		// do nothing ... the user just won't have a session
	    }
	}
	filterChain.doFilter(servletRequest, servletResponse);
    }

    public static CASReceipt getCASReceipt(final String casTicket, final String requestURL) throws UnsupportedEncodingException,
	    CASAuthenticationException {
	final String casValidateUrl = FenixWebFramework.getConfig().getCasValidateUrl();
	final String casServiceUrl = URLEncoder.encode(requestURL, "UTF-8");

	ProxyTicketValidator pv = new ProxyTicketValidator();
	pv.setCasValidateUrl(casValidateUrl);
	pv.setServiceTicket(casTicket);
	pv.setService(casServiceUrl);
	pv.setRenew(false);

	return CASReceipt.getReceipt(pv);
    }

}
