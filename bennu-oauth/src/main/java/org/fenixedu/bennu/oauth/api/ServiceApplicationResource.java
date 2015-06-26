/**
 * Copyright © 2015 Instituto Superior Técnico
 *
 * This file is part of Bennu OAuth.
 *
 * Bennu OAuth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu OAuth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu OAuth.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.oauth.api;

import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;

import com.google.gson.JsonElement;

@Path("/bennu-oauth/service-applications")
public class ServiceApplicationResource extends ExternalApplicationResource {

    @Override
    protected User verifyAndGetRequestAuthor() {
        return accessControl(Group.managers());
    }

    @Override
    protected User verifyAndGetRequestAuthor(ExternalApplication application) {
        return verifyAndGetRequestAuthor();
    }

    @Override
    public JsonElement myApplications() {
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @Override
    protected Set<? extends ExternalApplication> getAllApplications() {
        return Bennu.getInstance().getServiceApplicationSet();
    }

    @Override
    protected ExternalApplication create(JsonElement json) {
        return create(json, ServiceApplication.class);
    }

    @Override
    protected JsonElement update(ExternalApplication application, JsonElement json, User currentUser) {
        return view(update(json, application));
    }

}
