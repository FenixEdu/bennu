/*
 * BennuCoreJsonException.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-core.
 *
 * bennu-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.rest.json;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;

public class BennuCoreJsonException extends BennuCoreDomainException {
    private static final long serialVersionUID = 895004129903164510L;

    public static BennuCoreJsonException valueIsNotAnInteger(String value) {
        return new BennuCoreJsonException("error.bennu.core.valueIsNotAnInteger", value);
    }

    protected BennuCoreJsonException(String key, String... args) {
        super(key, args);
    }

    protected BennuCoreJsonException(Status status, String key, String... args) {
        super(status, key, args);
    }

    protected BennuCoreJsonException(Throwable cause, String key, String... args) {
        super(cause, key, args);
    }

    protected BennuCoreJsonException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, key, args);
    }
}
