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
        return getWhitelist().contains(ipAddress);
    }

}
