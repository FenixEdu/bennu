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

import org.fenixedu.bennu.oauth.OAuthProperties;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Strings;

public class ApplicationUserSession extends ApplicationUserSession_Base {

    public ApplicationUserSession() {
        super();
    }

    @Override
    public void setCode(String code) {
        super.setCode(code);
        setCreationDate(new DateTime());
    }

    public boolean matchesCode(String code) {
        if (Strings.isNullOrEmpty(code) || Strings.isNullOrEmpty(getCode())) {
            return false;
        }
        return code.equals(getCode());
    }

    public boolean isCodeValid() {
        return !Strings.isNullOrEmpty(getCode())
                && getCreationDate().plusSeconds(OAuthProperties.getConfiguration().getCodeExpirationSeconds()).isAfterNow();
    }

    public boolean matchesAccessToken(String accessToken) {
        if (Strings.isNullOrEmpty(getAccessToken()) || Strings.isNullOrEmpty(accessToken)) {
            return false;
        }
        return getAccessToken().equals(accessToken);
    }

    public boolean isAccessTokenValid() {
        return getCreationDate() != null
                && getCreationDate().plusSeconds(OAuthProperties.getConfiguration().getAccessTokenExpirationSeconds())
                        .isAfterNow();
    }

    public boolean isRefreshTokenValid() {
        return !Strings.isNullOrEmpty(getRefreshToken());
    }

    public boolean matchesRefreshToken(String refreshToken) {
        if (Strings.isNullOrEmpty(getRefreshToken()) || Strings.isNullOrEmpty(refreshToken)) {
            return false;
        }
        return getRefreshToken().equals(refreshToken);
    }

    @Atomic
    public void setTokens(String accessToken, String refreshToken) {
        setAccessToken(accessToken);
        setRefreshToken(refreshToken);
        setCode(null);
        setCreationDate(new DateTime());
    }

    @Atomic
    public void setNewAccessToken(String accessToken) {
        setAccessToken(accessToken);
        setCode(null);
        setCreationDate(new DateTime());
    }

    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        setCode(null);
        setAccessToken(null);
        setCreationDate(null);
        setDeviceId(null);
        setApplicationUserAuthorization(null);
        setRefreshToken(null);
        if (getUserPKCEInfoAuthorizationSession() != null) {
            getUserPKCEInfoAuthorizationSession().delete();
        }
        deleteDomainObject();
    }

    public boolean isActive() {
        return getCreationDate() != null;
    }
}