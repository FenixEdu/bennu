/*
 * DomainException.java
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

import org.fenixedu.bennu.core.i18n.BundleUtil;

import com.google.gson.JsonObject;

/**
 * <p>
 * {@code DomainException}s indicate expected error conditions that can presented.
 * </p>
 * 
 * <p>
 * The presentation of these exceptions is ensured by {@link BundleUtil#getString(String, String, String...)} based on resource
 * bundle parameters collected at construction time.
 * </p>
 */
public class DomainException extends RuntimeException {
    private static final long serialVersionUID = 3292374361672788603L;

    private final String key;

    private final String[] args;

    private final String bundle;

    private final Status status;

    protected DomainException(String bundle, String key, String... args) {
        this(Status.PRECONDITION_FAILED, bundle, key, args);
    }

    protected DomainException(Status status, String bundle, String key, String... args) {
        super(key);
        this.status = status;
        this.bundle = bundle;
        this.key = key;
        this.args = args;
    }

    protected DomainException(Throwable cause, String bundle, String key, String... args) {
        this(cause, Status.INTERNAL_SERVER_ERROR, bundle, key, args);
    }

    protected DomainException(Throwable cause, Status status, String bundle, String key, String... args) {
        super(key, cause);
        this.status = status;
        this.bundle = bundle;
        this.key = key;
        this.args = args;
    }

    @Override
    public String getLocalizedMessage() {
        return BundleUtil.getString(bundle, key, args);
    }

    public Status getResponseStatus() {
        return status;
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();
        json.addProperty("message", getLocalizedMessage());
        return json;
    }

    public String getKey() {
        return key;
    }

    public String[] getArgs() {
        return args;
    }
}
