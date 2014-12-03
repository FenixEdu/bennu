package org.fenixedu.bennu.oauth.api;

import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;

@Path("/bennu-oauth/service-applications")
public class ServiceApplicationResource extends ExternalApplicationResource {

    @Override
    protected User verifyAndGetRequestAuthor() {
        return accessControl("#managers");
    }

    @Override
    protected User verifyAndGetRequestAuthor(ExternalApplication application) {
        return verifyAndGetRequestAuthor();
    }

    @Override
    public String myApplications() {
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @Override
    protected Set<? extends ExternalApplication> getAllApplications() {
        return Bennu.getInstance().getServiceApplicationSet();
    }

    @Override
    protected ExternalApplication create(String json) {
        return create(json, ServiceApplication.class);
    }

}
