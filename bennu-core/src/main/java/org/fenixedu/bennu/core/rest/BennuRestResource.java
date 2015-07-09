package org.fenixedu.bennu.core.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.core.util.CoreConfiguration.CasConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

public abstract class BennuRestResource extends JsonAwareResource {

    private static final Logger LOG = LoggerFactory.getLogger(BennuRestResource.class);

    protected CasConfigContext getCasConfigContext() {
        return new CasConfigContext(CoreConfiguration.casConfig());
    }

    public static class CasConfigContext {

        private final CasConfig casConfig;

        public CasConfigContext(CasConfig casConfig) {
            this.casConfig = casConfig;
        }

        public CasConfig getCasConfig() {
            return casConfig;
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
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    /**
     * @deprecated Use {@link BennuRestResource#accessControl(Group) } instead.
     * @param accessExpression group expression to evaluate
     * @return The user logged in or exception
     */
    @Deprecated
    protected User accessControl(String accessExpression) {
        try {
            return accessControl(Group.parse(accessExpression));
        } catch (DomainException e) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
    }

    protected User verifyAndGetRequestAuthor() {
        return accessControl(Group.logged());
    }

    protected Response ok() {
        return Response.ok().build();
    }

}
