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
package pt.ist.bennu.core.domain.exceptions;

import pt.ist.bennu.core.util.BundleUtil;

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
    private final String key;

    private final String[] args;

    private final String bundle;

    public DomainException(String bundle, String key, String... args) {
        super(key);
        this.bundle = bundle;
        this.key = key;
        this.args = args;
    }

    public DomainException(Throwable cause, String bundle, String key, String... args) {
        super(cause);
        this.bundle = bundle;
        this.key = key;
        this.args = args;
    }

    @Override
    public String getLocalizedMessage() {
        return BundleUtil.getString(bundle, key, args);
    }
}
