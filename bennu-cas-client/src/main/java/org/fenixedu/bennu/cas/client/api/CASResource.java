
package org.fenixedu.bennu.cas.client.api;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.cas.client.CASClientConfiguration;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.servlet.PortalLoginServlet;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Strings;

@Path("/cas-client/login")
public class CASResource {

    private static final Logger logger = LoggerFactory.getLogger(CASResource.class);

    private final TicketValidator validator = new Cas20ServiceTicketValidator(CASClientConfiguration.getConfiguration()
            .casServerUrl());

    @GET
    @Path("/{callback}")
    public Response returnFromCAS(@QueryParam("ticket") String ticket, @PathParam("callback") String callback,
            @Context HttpServletRequest request, @Context HttpServletResponse response) throws UnsupportedEncodingException,
            URISyntaxException {

        // Fail fast if CAS is not enabled
        if (!CASClientConfiguration.getConfiguration().casEnabled()) {
            return Response.status(Status.NOT_FOUND).build();
        }

        // We should always have a ticket here, so fail fast if not
        if (Strings.isNullOrEmpty(ticket)) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        // Check the callback is valid
        Optional<String> cb = decode(callback).filter(PortalLoginServlet::validateCallback);
        if (!cb.isPresent()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        String actualCallback = cb.get();

        try {
            // Begin by logging out
            Authenticate.logout(request, response);

            // Validate the ticket
            String requestURL = URLDecoder.decode(request.getRequestURL().toString(), "UTF-8");

            String username = validator.validate(ticket, requestURL).getPrincipal().getName();
            User user = getUser(username);
            Authenticate.login(request, response, user, "CAS Authentication");
            logger.trace("Logged in user {}, redirecting to {}", username, actualCallback);
        } catch (TicketValidationException | AuthorizationException e) {
            logger.debug(e.getMessage(), e);
            // Append the login_failed parameter to the callback
            actualCallback = actualCallback + (actualCallback.contains("?") ? "&" : "?") + "login_failed=true";
        }
        return Response.status(Status.FOUND).location(new URI(actualCallback)).build();
    }

    private static Optional<String> decode(String base64Callback) {
        try {
            return Optional.of(new String(Base64.getUrlDecoder().decode(base64Callback), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            // Invalid Base64, return an empty Optional
            return Optional.empty();
        }
    }

    private User getUser(String username) {
        User user = User.findByUsername(username);
        if (user == null) {
            user = attemptBootstrapUser(username);
        }
        return user;
    }

    @Atomic(mode = TxMode.WRITE)
    private static User attemptBootstrapUser(String username) {
        User user = User.findByUsername(username);
        if (user != null) {
            return user;
        }
        logger.info("Created new user for {}", username);
        return new User(username, new UserProfile("Unknown", "User", null, null, null));
    }

}
