package org.fenixedu.bennu.oauth.domain;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

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

}
