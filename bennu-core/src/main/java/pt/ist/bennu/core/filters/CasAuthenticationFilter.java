/*
 * @(#)CasAuthenticationFilter.java
 * 
 * Copyright 2009 Instituto Superior Tecnico Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 * https://fenix-ashes.ist.utl.pt/
 * 
 * This file is part of the Bennu Web Application Infrastructure.
 * 
 * The Bennu Web Application Infrastructure is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Bennu is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Bennu. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.filters;

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

import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.core.util.ConfigurationManager;
import edu.yale.its.tp.cas.client.CASAuthenticationException;
import edu.yale.its.tp.cas.client.CASReceipt;
import edu.yale.its.tp.cas.client.ProxyTicketValidator;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class CasAuthenticationFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
			ServletException {
		final String serverName = request.getServerName();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		final String ticket = httpServletRequest.getParameter("ticket");
		if (ticket != null) {
			Authenticate.logout(httpServletRequest.getSession());

			final String requestURL = httpServletRequest.getRequestURL().toString();
			try {
				final CASReceipt receipt = getCASReceipt(serverName, ticket, requestURL);
				final String username = receipt.getUserName();
				Authenticate.login(httpServletRequest.getSession(), username, null, false);
			} catch (CASAuthenticationException e) {
				e.printStackTrace();
				// do nothing ... the user just won't have a session
			}
		}
		filterChain.doFilter(request, response);
	}

	public static CASReceipt getCASReceipt(final String serverName, final String casTicket, final String requestURL)
			throws UnsupportedEncodingException, CASAuthenticationException {
		String casValidateUrl = ConfigurationManager.getCasConfig(serverName).getCasValidateUrl();
		String casServiceUrl = URLEncoder.encode(requestURL.replace("http://", "https://").replace(":8080", ""), "UTF-8");

		ProxyTicketValidator pv = new ProxyTicketValidator();
		pv.setCasValidateUrl(casValidateUrl);
		pv.setServiceTicket(casTicket);
		pv.setService(casServiceUrl);
		pv.setRenew(false);

		return CASReceipt.getReceipt(pv);
	}

}
