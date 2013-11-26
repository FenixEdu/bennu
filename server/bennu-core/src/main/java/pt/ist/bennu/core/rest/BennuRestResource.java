package pt.ist.bennu.core.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.AuthorizationException;
import pt.ist.bennu.core.domain.exceptions.BennuCoreDomainException;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.domain.groups.Group;
import pt.ist.bennu.core.rest.json.JsonAwareResource;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.core.security.UserSession;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.core.util.ConfigurationManager.CasConfig;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

public abstract class BennuRestResource extends JsonAwareResource {

    private static final Logger LOG = LoggerFactory.getLogger(BennuRestResource.class);

    @Context
    HttpServletRequest request;

    protected UserSession login(String username, String password, boolean checkPassword) {
        return Authenticate.login(request.getSession(true), username, password, checkPassword);
    }

    protected CasConfigContext getCasConfigContext() {
        return new CasConfigContext(ConfigurationManager.getCasConfig());
    }

    public class CasConfigContext {

        private final CasConfig casConfig;

        public CasConfigContext(CasConfig casConfig) {
            this.casConfig = casConfig;
        }

        public CasConfig getCasConfig() {
            return casConfig;
        }

        public HttpServletRequest getRequest() {
            return request;
        }
    }

    protected <T extends DomainObject> T readDomainObject(final String externalId) {
        boolean error = false;
        try {
            if (externalId == null || "null".equals(externalId)) {
                return null;
            }
            T obj = FenixFramework.getDomainObject(externalId);
            if (obj == null) {
                error = true;
            } else {
                return obj;
            }
        } catch (Throwable t) {
            error = true;
        } finally {
            if (error) {
                throw BennuCoreDomainException.resourceNotFound(externalId);
            }
        }
        LOG.error("Unreachable code");
        return null;
    }

    protected User accessControl(Group group) {
        final User user = Authenticate.getUser();
        if (group.isMember(user)) {
            return user;
        }
        throw AuthorizationException.unauthorized();
    }

    protected User accessControl(String accessExpression) {
        try {
            final Group group = Group.parse(accessExpression);
            return accessControl(group);
        } catch (DomainException e) {
            throw AuthorizationException.unauthorized();
        }
    }

    protected User verifyAndGetRequestAuthor() {
        return accessControl("logged");
    }

    protected <T extends DomainObject> URI getURIFor(T domainObject, String relativePath) {
        try {
            return new URI("http", getHost(), relativePath + "/" + domainObject.getExternalId(), null);
        } catch (URISyntaxException e) {
            throw BennuCoreDomainException.cannotCreateEntity();
        }
    }

    protected String getHost() {
        return request.getServerName();
    }

}
