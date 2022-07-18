package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;

import com.google.common.base.Strings;

/**
 * Specialized servlet that logs the current user out.
 * 
 * If CAS is enabled, the user is redirected to the CAS logout page, after
 * being logged out locally.
 * 
 * If CAS is not enabled, the user is redirected to the application's root.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@WebServlet({ "/logout", "/logout/" })
public class PortalLogoutServlet extends HttpServlet {

    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Logout locally
        Authenticate.logout(req, resp);

        if (Strings.isNullOrEmpty(BennuPortalConfiguration.getConfiguration().logoutURL())) {
            resp.sendRedirect(req.getContextPath() + "/");
        } else {
            resp.sendRedirect(BennuPortalConfiguration.getConfiguration().logoutURL());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
