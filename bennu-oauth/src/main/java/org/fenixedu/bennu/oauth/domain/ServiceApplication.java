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
import java.util.Objects;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

import com.google.gson.JsonElement;

public class ServiceApplication extends ServiceApplication_Base {

    public ServiceApplication() {
        super();
        init();
        setRedirectUrl("");
        setBennuServiceApplication(Bennu.getInstance());
        setBennu(null);
    }

    @Atomic
    public void createServiceAuthorization(String accessToken) {
        addServiceAuthorization(new ServiceApplicationAuthorization(accessToken));
    }

    public boolean hasServiceAuthorization(final String accessToken) {
        return getServiceAuthorizationSet().stream().anyMatch(auth -> auth.matchesAccessToken(accessToken));
    }

    @Override
    public boolean matches(String redirectUrl, String secret) {
        return matchesSecret(secret);
    }

    public Set<String> getWhitelist() {
        final Set<String> whitelist = new HashSet<>();

        JsonElement addresses = getIpAddresses();

        if (addresses == null || !addresses.isJsonArray()) {
            return whitelist;
        }

        for (JsonElement el : addresses.getAsJsonArray()) {
            whitelist.add(el.getAsString().trim());
        }

        return whitelist;
    }

    public boolean matchesIpAddress(String ipAddress) {
        Objects.requireNonNull(ipAddress);
        return getWhitelist().isEmpty() || getWhitelist().contains(ipAddress);
    }

}
