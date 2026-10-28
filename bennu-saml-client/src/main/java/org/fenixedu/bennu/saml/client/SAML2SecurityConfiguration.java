package org.fenixedu.bennu.saml.client;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.saml2.provider.service.authentication.OpenSamlAuthenticationProvider;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.Saml2WebSsoAuthenticationRequestFilter;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml3AuthenticationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
        // The portal callback travels in the session (see SAMLLoginProvider and
        // BennuAuthenticationSuccessHandler#resolveCallback); RelayState stays at Spring's stock
        // default, a random opaque value. The resolver is Spring's public OpenSAML 3 flavour —
        // with OpenSAML 3 on the classpath the whole engine runs on the 3.x paths, matching the
        // generation the SAP integration (wss4j) uses.
        OpenSaml3AuthenticationRequestResolver authenticationRequestResolver =
                new OpenSaml3AuthenticationRequestResolver(new DefaultRelyingPartyRegistrationResolver(repository));
        authenticationRequestResolver.setAuthnRequestCustomizer(context -> {
            // the portal callback travels in the session: ?callback= on the authentication-request
            // endpoint is kept there for the success handler (also covers callers that skip
            // SAMLLoginProvider); a request without it clears a stale callback from an earlier
            // attempt, so every new SP-initiated login starts clean
            HttpServletRequest request = context.getRequest();
            String callback = request.getParameter("callback");
            if (StringUtils.hasText(callback)) {
                request.getSession(true).setAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE,
                        callback);
            } else {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.removeAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE);
                }
            }
        });

        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
        http.sessionManagement(session -> session.sessionFixation().none());
        http.securityContext(securityContext -> securityContext.securityContextRepository(new BennuSecurityContextRepository()));

        AtomicBoolean providerSwappedIn = new AtomicBoolean();
        http.saml2Login(login -> {
            // The Bennu provider is swapped in for the stock one the DSL registers (it restores
            // the SessionIndex extraction, the assertion Issuer check and the InResponseTo
            // binding that the OpenSAML 3 defaults lack — see
            // BennuOpenSamlAuthenticationProvider). The authentication manager, its parent and
            // the AuthenticationEventPublisher are built by the framework's
            // HttpSecurityConfiguration, exactly as for the stock provider: an app's own
            // publisher bean wins. ProviderManager still tries its parent after a rejection —
            // with the DSL default that parent is the global authentication manager, as in stock
            // Spring; this module registers no second SAML provider, so the Bennu wrapper is the
            // only SAML provider in the chain and its InResponseTo binding always applies.
            login.addObjectPostProcessor(new ObjectPostProcessor<Object>() {
                @Override
                public <O> O postProcess(O object) {
                    if (object instanceof OpenSamlAuthenticationProvider) {
                        providerSwappedIn.set(true);
                        return (O) new BennuOpenSamlAuthenticationProvider();
                    }
                    return object;
                }
            });
            login.relyingPartyRegistrationRepository(repository);
            login.loginProcessingUrl(ASSERTION_CONSUMER_SERVICE_URL);
            login.authenticationConverter(new BennuSaml2AuthenticationConverter(repository));
            login.authenticationRequestResolver(authenticationRequestResolver);
            login.successHandler(new BennuAuthenticationSuccessHandler());
            login.failureHandler(new BennuAuthenticationFailureHandler());
        });

        http.addFilterBefore(metadataFilter(repository), Saml2WebSsoAuthenticationRequestFilter.class);

        if (SAMLClientConfiguration.getConfiguration().sloEnabled()) {
            configureLogout(http, repository);
        }

        SecurityFilterChain chain = http.build();
        // fail fast if the hook never fired (e.g. the classpath switched to OpenSAML 4 and the
        // DSL creates the 4-based provider this post-processor does not match): the Bennu
        // customizations would otherwise be silently missing
        Assert.state(providerSwappedIn.get(),
                "The SAML login DSL did not register an OpenSamlAuthenticationProvider; the Bennu"
                        + " customizations (SessionIndex, Issuer check, InResponseTo binding) would be missing");
        return chain;
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
