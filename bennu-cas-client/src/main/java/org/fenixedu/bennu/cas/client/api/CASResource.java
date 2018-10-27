
package org.fenixedu.bennu.cas.client.api;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
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
import org.fenixedu.bennu.cas.client.strategy.DefaultTicketValidationStrategy;
import org.fenixedu.bennu.cas.client.strategy.TicketValidationStrategy;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.portal.servlet.PortalLoginServlet;
import org.jasig.cas.client.validation.TicketValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

@Path("/cas-client/login")
public class CASResource {

    private static final Logger logger = LoggerFactory.getLogger(CASResource.class);

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static TicketValidationStrategy VALIDATION_STRATEGY = null;

    private TicketValidationStrategy getTicketValidator() {
        if (VALIDATION_STRATEGY == null) {
            try {
                VALIDATION_STRATEGY = (TicketValidationStrategy) Class
                        .forName(CASClientConfiguration.getConfiguration().getCasLoginStrategy()).newInstance();
            } catch (Throwable t) {
                logger.error("Problem instantiating ticket validation strategy, falling back to default strategy", t);
                VALIDATION_STRATEGY = new DefaultTicketValidationStrategy();
            }
        }
        return VALIDATION_STRATEGY;
    }

    @GET
    @Path("/{callback}")
    public Response returnFromCAS(@QueryParam("ticket") String ticket, @PathParam("callback") String callback,
            @Context HttpServletRequest request, @Context HttpServletResponse response)
            throws UnsupportedEncodingException, URISyntaxException {

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

            // Validate the ticket
            String requestURL = URLDecoder.decode(request.getRequestURL().toString(), CHARSET.name());

            getTicketValidator().validateTicket(ticket, requestURL, request, response);

        } catch (TicketValidationException | AuthorizationException e) {
            logger.debug(e.getMessage(), e);
            // Append the login_failed parameter to the callback
            actualCallback = actualCallback + (actualCallback.contains("?") ? "&" : "?") + "login_failed=true";
        }
        return Response.status(Status.FOUND).location(new URI(actualCallback)).build();
    }

    private static Optional<String> decode(String base64Callback) {
        try {
            return Optional.of(new String(Base64.getUrlDecoder().decode(base64Callback), CHARSET));
        } catch (IllegalArgumentException e) {
            // Invalid Base64, return an empty Optional
            return Optional.empty();
        }
    }
}
