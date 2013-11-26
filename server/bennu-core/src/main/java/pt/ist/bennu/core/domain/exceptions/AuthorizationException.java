/*
 * AuthorizationException.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.domain.exceptions;

import javax.ws.rs.core.Response.Status;

/**
 * Group access authorization exception.
 */
public class AuthorizationException extends BennuCoreDomainException {
    private static final long serialVersionUID = -5511692317966073944L;

    private AuthorizationException(String key, String... args) {
        super(Status.UNAUTHORIZED, key, args);
    }

    private AuthorizationException(Throwable cause, String key, String... args) {
        super(cause, Status.UNAUTHORIZED, key, args);
    }

    public static AuthorizationException authenticationFailed() {
        return new AuthorizationException("error.bennu.core.authentication.failed");
    }

    public static AuthorizationException unauthorized() {
        return new AuthorizationException("error.bennu.core.unauthorized");
    }

    public static AuthorizationException badAccessGroupConfiguration() {
        return new AuthorizationException("error.bennu.core.badaccessgroupconfiguration");
    }
}
