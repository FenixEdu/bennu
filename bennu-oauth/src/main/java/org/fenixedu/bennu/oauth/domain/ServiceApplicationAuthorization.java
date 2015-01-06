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

import org.joda.time.DateTime;

import com.google.common.base.Strings;

public class ServiceApplicationAuthorization extends ServiceApplicationAuthorization_Base {

    public ServiceApplicationAuthorization(String accessToken) {
        super();
        setAccessToken(accessToken);
        setCreationDate(new DateTime());
    }

    public boolean matchesAccessToken(String accessToken) {
        if (Strings.isNullOrEmpty(getAccessToken()) || Strings.isNullOrEmpty(accessToken)) {
            return false;
        }
        return getAccessToken().equals(accessToken);
    }
}
