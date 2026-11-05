package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.servlet.Filter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Reproduces the deployment topology of every Bennu Spring application: the application's own
 * dispatcher context component-scans {@code org.fenixedu.bennu} (bennu-spring's
 * BennuSpringConfiguration does exactly this), which must <b>not</b> pull the SAML engine in.
 *
 * The SAML engine class carries {@code @EnableWebSecurity}, whose meta-annotation chain
 * ({@code @EnableGlobalAuthentication} &rarr; {@code @Configuration} &rarr; {@code @Component})
 * would otherwise make it a scan candidate and a full configuration class in <em>any</em>
 * context — failing the whole webapp when the SAML configuration is absent or broken. The
 * {@code @Conditional(SamlModuleOwnedContextCondition.class)} gate keeps the engine exclusive
 * to the module-owned context that {@code SAMLClientInitializer} publishes.
 */
public class SamlScanExclusionTest {

    private static AnnotationConfigWebApplicationContext context;

    @Configuration
    @ComponentScan("org.fenixedu.bennu.saml.client")
    static class ApplicationStyleConfiguration {
    }

    @BeforeClass
    public static void bootApplicationStyleContext() {
        // saml.enabled = true and the configuration is complete here (default test configuration):
        // even so, the SAML engine must not be registered by an application component scan
        context = new AnnotationConfigWebApplicationContext();
        context.register(ApplicationStyleConfiguration.class);
        context.refresh();
    }

    @AfterClass
    public static void shutdown() {
        context.close();
    }

    @Test
    public void componentScanDoesNotRegisterTheSamlEngine() {
        assertEquals("Expected no SAML engine beans in an application-scanned context", 0,
                context.getBeanNamesForType(SecurityFilterChain.class).length);
        assertEquals("Expected no springSecurityFilterChain bean in an application-scanned context", 0,
                context.getBeanNamesForType(Filter.class).length);
    }

    @Test
    public void componentScanDoesNotProcessTheSamlConfigurationClasses() {
        assertTrue("Expected the SAML engine configuration class to be skipped by the scan",
                context.getBeanNamesForType(SAML2SecurityConfiguration.class).length == 0);
        assertTrue("Expected the disabled configuration class to be skipped by the scan",
                context.getBeanNamesForType(SAMLDisabledConfiguration.class).length == 0);
    }
}
