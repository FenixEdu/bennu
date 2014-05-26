/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of Bennu Spring.
 *
 * Bennu Spring is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Spring is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Spring.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.spring.portal;

import org.fenixedu.bennu.portal.model.Functionality;
import org.springframework.web.method.HandlerMethod;

/*
 * HandlerMethod extension to store a Functionality
 */
final class PortalHandlerMethod extends HandlerMethod {

    private final Functionality functionality;

    PortalHandlerMethod(HandlerMethod method, Functionality functionality) {
        super(method.createWithResolvedBean());
        this.functionality = functionality;
    }

    public Functionality getFunctionality() {
        return functionality;
    }

    @Override
    public HandlerMethod createWithResolvedBean() {
        return this;
    }

}
