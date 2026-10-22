package org.fenixedu.bennu.saml.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.Saml2WebSsoAuthenticationRequestFilter;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Owns the Spring Security {@link SecurityFilterChain} that acts as the SAML 2.0 protocol engine
 * for this module.
 *
 * Spring Security is deliberately NOT used as an authorization gate: Bennu remains the security
 * authority (sessions, CSRF, user provisioning), and this chain only serves the SAML endpoints.
 * Every request that reaches it is permitted, and nothing from Spring Security is ever written to
 * the HTTP session (see {@link BennuSecurityContextRepository}).
 */
@Configuration
@EnableWebSecurity
public class SAML2SecurityConfiguration {

    public static final String REGISTRATION_ID = "bennu";

    public static final String AUTHENTICATION_REQUEST_URL = "/saml2/authenticate/" + REGISTRATION_ID;
    public static final String ASSERTION_CONSUMER_SERVICE_URL = "/api/saml-client/returnFromSAML";
    public static final String METADATA_URL = "/api/saml-client/metadata";
    public static final String LOGOUT_URL = "/api/saml-client/logout";

    @Bean
    public BennuRelyingPartyRegistrationRepository samlRelyingPartyRegistrationRepository() {
        return new BennuRelyingPartyRegistrationRepository();
    }

    @Bean
    public SecurityFilterChain samlFilterChain(HttpSecurity http, BennuRelyingPartyRegistrationRepository repository)
            throws Exception {
        // Spring Security 5.8 defaults to a random RelayState. Bennu uses it to carry the
        // PortalLoginServlet callback through the IdP round trip.
        OpenSaml4AuthenticationRequestResolver authenticationRequestResolver = new OpenSaml4AuthenticationRequestResolver(
                new DefaultRelyingPartyRegistrationResolver(repository));
        authenticationRequestResolver.setRelayStateResolver(request -> request.getParameter("callback"));

        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
        http.sessionManagement(session -> session.sessionFixation().none());
        http.securityContext(securityContext -> securityContext.securityContextRepository(new BennuSecurityContextRepository()));

        http.saml2Login(login -> login
                .relyingPartyRegistrationRepository(repository)
                .loginProcessingUrl(ASSERTION_CONSUMER_SERVICE_URL)
                .authenticationConverter(new BennuSaml2AuthenticationConverter(repository))
                .authenticationRequestResolver(authenticationRequestResolver)
                .successHandler(new BennuAuthenticationSuccessHandler())
                .failureHandler(new BennuAuthenticationFailureHandler()));

        http.addFilterBefore(metadataFilter(repository), Saml2WebSsoAuthenticationRequestFilter.class);

        if (SAMLClientConfiguration.getConfiguration().sloEnabled()) {
            configureLogout(http, repository);
        }

        return http.build();
    }

    private void configureLogout(HttpSecurity http, BennuRelyingPartyRegistrationRepository repository) throws Exception {
        // The logout() DSL is configured so that Saml2LogoutConfigurer picks up our handlers and
        // success handler. Session invalidation is delegated to BennuLogoutHandler, which fires the
        // Bennu logout listeners before Spring redirects to the IdP. The success handler defines
        // where the user lands when the IdP's LogoutResponse comes back.
        http.logout(logout -> logout
                .invalidateHttpSession(false)
                .logoutSuccessUrl(SAMLClientConfiguration.getConfiguration().logoutSuccessUrl())
                .addLogoutHandler(new BennuLogoutHandler()));

        http.saml2Logout(logout -> logout
                .relyingPartyRegistrationRepository(repository)
                .logoutUrl(LOGOUT_URL)
                .logoutRequest(request -> request
                        .logoutUrl(LOGOUT_URL)
                        .logoutRequestRepository(new CookieSaml2LogoutRequestRepository(repository))
                        .logoutRequestValidator(logoutValidator()))
                .logoutResponse(response -> response
                        .logoutUrl(LOGOUT_URL)
                        .logoutResponseValidator(logoutValidator())));
    }

    private BennuSaml2LogoutMessageValidator logoutValidator() {
        return new BennuSaml2LogoutMessageValidator(
                Boolean.TRUE.equals(SAMLClientConfiguration.getConfiguration().sloRequireSignedMessages()));
    }

    private Saml2MetadataFilter metadataFilter(BennuRelyingPartyRegistrationRepository repository) {
        // the fixed metadata URL has no {registrationId} path variable, so resolve the single
        // static registration regardless of the requested id
        RelyingPartyRegistrationResolver registrationResolver = (request, registrationId) -> repository
                .findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);
        Saml2MetadataFilter metadataFilter = new Saml2MetadataFilter(registrationResolver, metadataResolver());
        metadataFilter.setRequestMatcher(new AntPathRequestMatcher(METADATA_URL));
        return metadataFilter;
    }

    private OpenSamlMetadataResolver metadataResolver() {
        OpenSamlMetadataResolver metadataResolver = new OpenSamlMetadataResolver();
        metadataResolver.setEntityDescriptorCustomizer(new BennuSaml2MetadataPostProcessor());
        return metadataResolver;
    }
}
