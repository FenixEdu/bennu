package org.fenixedu.bennu.saml.client.api;

import com.onelogin.saml2.Auth;
import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.core.domain.Singleton;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.security.SkipCSRF;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.saml.client.SAMLClientConfiguration;
import org.fenixedu.bennu.saml.client.SAMLClientSDK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Path("/saml-client")
public class SAMLResource {

    private static final Logger logger = LoggerFactory.getLogger(SAMLResource.class);

    @POST
    @SkipCSRF
    @Path("/returnFromSAML")
    public Response returnFromSAML(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws URISyntaxException {

        // Fail fast if SAML is not enabled
        if (!SAMLClientConfiguration.getConfiguration().samlEnabled()) {
            return Response.status(Status.NOT_FOUND).build();
        }


        Auth auth = null;
        try {
            auth = SAMLClientSDK.getAuth(request, response);
            auth.processResponse();
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage(), e);
            return Response.status(Status.BAD_REQUEST).location(new URI("")).build(); //TODO FIX
        }

        if (!auth.isAuthenticated()) {
            System.out.println("Not authenticated");
            logger.debug("Not authenticated");
            return Response.status(Status.BAD_REQUEST).location(new URI("")).build(); //TODO FIX
        }

        String username = null;
        String relayState = null;
        List<String> errors = auth.getErrors();
        if (!errors.isEmpty()) {
            logger.debug(StringUtils.join(errors, ", "));
            if (auth.isDebugActive()) {
                String errorReason = auth.getLastErrorReason();
                if (errorReason != null && !errorReason.isEmpty()) {
                    logger.debug(auth.getLastErrorReason());
                }
            }
        } else {
            Map<String, List<String>> attributes = auth.getAttributes();
//            String nameId = auth.getNameId();
//            String nameIdFormat = auth.getNameIdFormat();
//            String sessionIndex = auth.getSessionIndex();
//            String nameidNameQualifier = auth.getNameIdNameQualifier();
//            String nameidSPNameQualifier = auth.getNameIdSPNameQualifier();
            List<String> tempIstId = attributes.get("uid");
            if (tempIstId != null) {
                username = tempIstId.get(0);
            } else {
                //todo something like redirecting to failed page
            }
        }


        String resultLocation = CoreConfiguration.getConfiguration().applicationUrl() + request.getContextPath();
        try {

            final User user = getUser(username);
            Authenticate.login(request, response, user, "SAML Authentication");

            logger.trace("Logged in user {}, redirecting to {}", username, resultLocation);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            resultLocation = resultLocation + "/login.do?login_failed=true";
        }
        return Response.status(Status.FOUND).location(new URI(resultLocation)).build();
    }

    private User getUser(final String username) {
        return Singleton.getInstance(() -> User.findByUsername(username), () -> {
            logger.info("Created new user for {}", username);
            return new User(username, new UserProfile("Unknown", "User", null, null, null));
        });
    }

    @GET
    @Path("/metadata")
    public Response returnMetadata() {
        File file = new File(SAMLClientConfiguration.getConfiguration().serviceProviderFinalMetadataPath());
        if (file.exists()) {
            Response.ResponseBuilder response = Response.ok(file);
            response.header("Content-Type", "text/xml");
            return response.build();
        } else {
            logger.debug("SAML SP metadata file not found, path: " + SAMLClientConfiguration.getConfiguration()
                    .serviceProviderFinalMetadataPath());
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
