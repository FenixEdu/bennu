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

    public String getUsername() {
        return getApplicationUserAuthorization().getUser().getUsername();
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
        deleteDomainObject();
    }

    public boolean isActive() {
        return getCreationDate() != null;
    }
}