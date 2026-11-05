package org.fenixedu.bennu.saml.client;

import org.fenixedu.bennu.saml.client.servlet.SAMLClientInitializer;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Only allows the SAML Spring configurations inside the module-owned Spring context.
 *
 * The SAML engine class carries {@code @EnableWebSecurity}, which makes <em>any</em> class
 * annotated with it scan-magnetic and a <em>full</em> configuration class in Spring's eyes:
 * {@code @EnableWebSecurity} is meta-annotated (via {@code @EnableGlobalAuthentication}) with
 * {@code @Configuration}, which is itself meta-annotated with {@code @Component}. As a result,
 * every Bennu Spring application's own component scan (bennu-spring scans
 * {@code org.fenixedu.bennu}) would register the SAML engine into the application context and
 * build it there — failing the whole webapp when the SAML configuration is absent or broken.
 *
 * This condition matches only contexts that carry the module-owned active profile
 * (see {@link SAMLClientInitializer#MODULE_OWNED_CONTEXT_PROFILE}), which the module's initializer
 * sets exclusively on the Spring context it owns — so the SAML configurations are processed
 * exclusively by the module's own initializer.
 */
public class SamlModuleOwnedContextCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment() != null && context.getEnvironment()
                .acceptsProfiles(Profiles.of(SAMLClientInitializer.MODULE_OWNED_CONTEXT_PROFILE));
    }
}
