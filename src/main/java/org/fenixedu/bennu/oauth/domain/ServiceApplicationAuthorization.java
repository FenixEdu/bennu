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
