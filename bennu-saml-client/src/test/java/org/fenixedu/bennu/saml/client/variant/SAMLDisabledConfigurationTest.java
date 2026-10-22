package org.fenixedu.bennu.saml.client.variant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.ServletContextEvent;
import javax.servlet.Filter;

import org.fenixedu.bennu.saml.client.SAMLClientConfiguration;
import org.fenixedu.bennu.saml.client.SAMLDisabledConfiguration;
import org.fenixedu.bennu.saml.client.servlet.SAMLClientInitializer;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Runs against configuration-variant.properties (saml.enabled=false): the initializer publishes
 * the pass-through context, so requests to the SAML endpoints continue down the filter chain
 * (the 404 guard itself lives in SAMLResource, served by the application's JAX-RS runtime).
 */
public class SAMLDisabledConfigurationTest {

    @Test
    public void passThroughFilterContinuesTheChain() throws Exception {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(SAMLDisabledConfiguration.class);
        context.refresh();

        Filter filter = context.getBean("springSecurityFilterChain", Filter.class);
        assertNotNull(filter);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/saml-client/returnFromSAML");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, response, chain);
        assertNotNull("Expected the request to pass through to the rest of the chain", chain.getRequest());
        context.close();
    }

    @Test
    public void initializerRegistersThePassThroughContextWhenDisabled() {
        MockServletContext servletContext = new MockServletContext();
        SAMLClientInitializer initializer = new SAMLClientInitializer();
        initializer.contextInitialized(new ServletContextEvent(servletContext));

        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext,
                SAMLClientInitializer.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        assertNotNull("Expected the module-owned context to be published", context);
        assertNotNull(context.getBean("springSecurityFilterChain", Filter.class));
        assertTrue("Expected SAML to be disabled in this configuration",
                !SAMLClientConfiguration.getConfiguration().samlEnabled());
        initializer.contextDestroyed(new ServletContextEvent(servletContext));
        assertNull(WebApplicationContextUtils.getWebApplicationContext(servletContext,
                SAMLClientInitializer.WEB_APPLICATION_CONTEXT_ATTRIBUTE));
    }
}
