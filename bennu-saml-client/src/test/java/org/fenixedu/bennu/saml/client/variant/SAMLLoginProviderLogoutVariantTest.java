package org.fenixedu.bennu.saml.client.variant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.fenixedu.bennu.saml.client.BennuSecurityContextRepository;
import org.fenixedu.bennu.saml.client.SAMLDisabledConfiguration;
import org.fenixedu.bennu.saml.client.SAMLLoginProvider;
import org.fenixedu.bennu.saml.client.servlet.SAMLClientInitializer;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 * Runs against configuration-variant.properties (saml.enabled=false, saml.slo.enabled=false):
 * the SAML provider's /logout hook must decline, leaving the plain local logout to the portal.
 */
public class SAMLLoginProviderLogoutVariantTest {

    @Test
    public void declinesWhenSloIsDisabled() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(servletContext);
        context.register(SAMLDisabledConfiguration.class);
        servletContext.setAttribute(SAMLClientInitializer.WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
        context.refresh();

        MockHttpServletRequest request = new MockHttpServletRequest(servletContext, "GET", "/logout");
        request.setSession(new MockHttpSession());
        request.getSession()
                .setAttribute(BennuSecurityContextRepository.SAML_IDENTITY_SESSION_ATTRIBUTE,
                        new BennuSecurityContextRepository.StoredSamlIdentity("idp-user-123",
                                Collections.singletonList("session-1")));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean handled = new SAMLLoginProvider().logout(request, response);

        assertFalse("Expected the hook to decline with SLO disabled", handled);
        assertNull(response.getRedirectedUrl());

        WebApplicationContext published = WebApplicationContextUtils.getWebApplicationContext(servletContext,
                SAMLClientInitializer.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (published instanceof AnnotationConfigWebApplicationContext) {
            ((AnnotationConfigWebApplicationContext) published).close();
        }
    }
}
