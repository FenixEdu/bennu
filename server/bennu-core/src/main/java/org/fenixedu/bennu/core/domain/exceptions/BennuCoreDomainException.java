/*
 * BennuCoreDomainException.java
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
package org.fenixedu.bennu.core.domain.exceptions;

import javax.ws.rs.core.Response.Status;

/**
 * Factory class for bennu-core expected errors.
 */
public class BennuCoreDomainException extends DomainException {
    private static final long serialVersionUID = 2072331768593389420L;

    protected static final String BUNDLE = "resources.BennuResources";

    protected BennuCoreDomainException(String key, String... args) {
        super(BUNDLE, key, args);
    }

    protected BennuCoreDomainException(Status status, String key, String... args) {
        super(status, BUNDLE, key, args);
    }

    protected BennuCoreDomainException(Throwable cause, String key, String... args) {
        super(cause, BUNDLE, key, args);
    }

    protected BennuCoreDomainException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, BUNDLE, key, args);
    }

    public static BennuCoreDomainException badOldPassword() {
        return new BennuCoreDomainException("error.bennu.core.bad.old.password");
    }

    public static BennuCoreDomainException resourceNotFound(String id) {
        return new BennuCoreDomainException(Status.NOT_FOUND, "error.bennu.core.resourcenotfound", id);
    }

    public static BennuCoreDomainException cannotCreateEntity() {
        return new BennuCoreDomainException("error.bennu.core.cannotcreateentity");
    }

    public static BennuCoreDomainException parseError() {
        return new BennuCoreDomainException(Status.BAD_REQUEST, "error.bennu.core.parseerror");
    }

    public static BennuCoreDomainException groupParsingError(Throwable cause) {
        return new BennuCoreDomainException(cause, "error.bennu.core.groups.parse");
    }

    public static BennuCoreDomainException errorOnDeleteDomainObject() {
        return new BennuCoreDomainException("error.bennu.core.cant.delete.domainObject");
    }

    public static BennuCoreDomainException dynamicGroupNotFound(String name) {
        return new BennuCoreDomainException("error.bennu.core.dynamic.group.not.found", name);
    }
}
