package org.fenixedu.bennu.saml.client.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.security.SkipCSRF;
import org.fenixedu.bennu.saml.client.SAMLClientConfiguration;
import org.fenixedu.bennu.saml.client.SAML2SecurityConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guard for the SAML endpoints when the SAML client is disabled.
 *
 * When the SAML client is enabled, these endpoints are served by the Spring Security SAML engine
 * (registered by {@link org.fenixedu.bennu.saml.client.SAML2SecurityConfiguration}) before the
 * request ever reaches this resource:
 * <ul>
 * <li>{@code POST /api/saml-client/returnFromSAML} &mdash; assertion consumer service</li>
 * <li>{@code GET /api/saml-client/metadata} &mdash; generated SP metadata</li>
 * <li>{@code POST /api/saml-client/logout} &mdash; SP-initiated Single Logout</li>
 * </ul>
 */
@Path("/saml-client")
public class SAMLResource {

    private static final Logger logger = LoggerFactory.getLogger(SAMLResource.class);

    @POST
    @SkipCSRF
    @Path("/returnFromSAML")
    public Response returnFromSAML(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        return unavailable("returnFromSAML");
    }

    @GET
    @Path("/metadata")
    public Response returnMetadata() {
        return unavailable("metadata");
    }

    private Response unavailable(String endpoint) {
        if (SAMLClientConfiguration.getConfiguration().samlEnabled()) {
            logger.warn("SAML endpoint {} was reached but was not handled by the SAML engine", endpoint);
        }
        return Response.status(Status.NOT_FOUND).build();
    }
}
