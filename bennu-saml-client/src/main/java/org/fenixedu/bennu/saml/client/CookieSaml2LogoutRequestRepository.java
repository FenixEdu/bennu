package org.fenixedu.bennu.saml.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml2.provider.service.authentication.logout.Saml2LogoutRequest;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2LogoutRequestRepository;

import com.google.gson.Gson;

/**
 * Stores the pending SAML 2.0 Logout Request in a short-lived {@link Cookie} instead of the HTTP
 * session.
 *
 * The portal's /logout endpoint (through {@link SAMLLoginProvider#logout}) terminates the local
 * session before the Identity Provider's LogoutResponse arrives, so a session-based store would
 * lose the pending request and the response would fail to correlate. The cookie survives the
 * local logout, letting the response be validated and the user land on
 * {@code saml.logoutSuccessUrl}.
 *
 * The request is stored as a JSON field map (never as a serialized Java object: cookie content
 * is client-controlled and must not be deserialized natively). If the payload does not fit a
 * cookie, the save is skipped with a warning — SAML logout remains best effort.
 */
public class CookieSaml2LogoutRequestRepository implements Saml2LogoutRequestRepository {

    private static final Logger logger = LoggerFactory.getLogger(CookieSaml2LogoutRequestRepository.class);

    public static final String COOKIE_NAME = "SAML_CLIENT_LOGOUT_REQUEST";

    private static final int MAX_AGE_SECONDS = 300;
    private static final int MAX_COOKIE_VALUE_LENGTH = 3500;

    private final Gson gson = new Gson();
    private final RelyingPartyRegistrationRepository repository;

    public CookieSaml2LogoutRequestRepository(RelyingPartyRegistrationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Saml2LogoutRequest loadLogoutRequest(HttpServletRequest request) {
        String payload = cookieValue(request);
        if (payload == null) {
            return null;
        }
        try {
            return deserialize(payload);
        } catch (Exception e) {
            logger.warn("Ignoring malformed SAML logout request cookie", e);
            return null;
        }
    }

    @Override
    public void saveLogoutRequest(Saml2LogoutRequest logoutRequest, HttpServletRequest request,
            HttpServletResponse response) {
        if (logoutRequest == null) {
            removeLogoutRequest(request, response);
            return;
        }
        String payload = serialize(logoutRequest);
        if (payload == null) {
            return;
        }
        Cookie cookie = new Cookie(COOKIE_NAME, payload);
        configure(cookie, request);
        response.addCookie(cookie);
    }

    @Override
    public Saml2LogoutRequest removeLogoutRequest(HttpServletRequest request, HttpServletResponse response) {
        Saml2LogoutRequest logoutRequest = loadLogoutRequest(request);
        Cookie expired = new Cookie(COOKIE_NAME, "");
        configure(expired, request);
        expired.setMaxAge(0);
        response.addCookie(expired);
        return logoutRequest;
    }

    private void configure(Cookie cookie, HttpServletRequest request) {
        cookie.setPath(request.getContextPath() + "/");
        cookie.setMaxAge(MAX_AGE_SECONDS);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
    }

    private String serialize(Saml2LogoutRequest logoutRequest) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("id", logoutRequest.getId());
        fields.put("samlRequest", logoutRequest.getSamlRequest());
        fields.put("relayState", logoutRequest.getRelayState());
        fields.put("binding", logoutRequest.getBinding() == null ? null : logoutRequest.getBinding().name());
        fields.put("location", logoutRequest.getLocation());
        fields.put("registrationId", logoutRequest.getRelyingPartyRegistrationId());
        String json = this.gson.toJson(fields);
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        if (payload.length() > MAX_COOKIE_VALUE_LENGTH) {
            logger.warn("Pending SAML logout request does not fit the logout cookie ({} bytes); "
                    + "the IdP LogoutResponse will not correlate", payload.length());
            return null;
        }
        return payload;
    }

    private Saml2LogoutRequest deserialize(String payload) {
        byte[] jsonBytes = Base64.getUrlDecoder().decode(payload);
        String json = new String(jsonBytes, StandardCharsets.UTF_8);
        Map<String, String> fields = this.gson.fromJson(json, Map.class);

        String registrationId = fields.get("registrationId");
        RelyingPartyRegistration registration = registrationId == null ? null
                : this.repository.findByRegistrationId(registrationId);
        if (registration == null) {
            logger.warn("Ignoring pending SAML logout request for unknown registration {}", registrationId);
            return null;
        }

        Saml2LogoutRequest.Builder builder = Saml2LogoutRequest.withRelyingPartyRegistration(registration)
                .id(fields.get("id"))
                .samlRequest(fields.get("samlRequest"))
                .location(fields.get("location"));
        String relayState = fields.get("relayState");
        if (relayState != null) {
            builder.relayState(relayState);
        }
        String binding = fields.get("binding");
        if (binding != null) {
            builder.binding(org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding
                    .valueOf(binding));
        }
        return builder.build();
    }

    private String cookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
