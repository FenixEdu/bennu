/*
 * CasAuthenticationFilter.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(CasAuthenticationFilter.class);

    @Override
    public void init(final FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        final String serverName = request.getServerName();
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final String ticket = httpServletRequest.getParameter("ticket");
        if (ticket != null) {
            Authenticate.logout(httpServletRequest.getSession());
            final String requestURL = "https://" + request.getServerName();
            try {
                final CASReceipt receipt = getCASReceipt(serverName, ticket, requestURL);
                final String username = receipt.getUserName();
                Authenticate.login(httpServletRequest.getSession(), username, null, false);
            } catch (CASAuthenticationException e) {
                e.printStackTrace();
            }
        }
        chain.doFilter(request, response);
    }

    public static CASReceipt getCASReceipt(final String serverName, final String casTicket, final String requestURL)
            throws UnsupportedEncodingException, CASAuthenticationException {
        String casValidateUrl = ConfigurationManager.getCasConfig().getCasValidateUrl();
        String casServiceUrl = URLEncoder.encode(requestURL.replace("http://", "https://").replace(":8080", ""), "UTF-8");

        ProxyTicketValidator pv = new ProxyTicketValidator();
        pv.setCasValidateUrl(casValidateUrl);
        pv.setServiceTicket(casTicket);
        pv.setService(casServiceUrl);
        pv.setRenew(false);
        logger.debug(String.format("casValidateUrl : %s casTicket : %s casServiceUrl : %s", casValidateUrl, casTicket,
                casServiceUrl));
        return CASReceipt.getReceipt(pv);
    }
}
