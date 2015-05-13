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
package org.fenixedu.bennu.oauth.domain;

import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class ApplicationUserAuthorization extends ApplicationUserAuthorization_Base {

    public ApplicationUserAuthorization(User user, ExternalApplication application) {
        super();
        setUser(user);
        setApplication(application);
    }

    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        Set<ApplicationUserSession> sessions = new HashSet<ApplicationUserSession>(getSessionSet());
        for (ApplicationUserSession session : sessions) {
            session.delete();
        }
        setUser(null);
        setApplication(null);
        deleteDomainObject();
    }

}