package org.fenixedu.bennu.saml.client;

import javax.servlet.Filter;

import org.fenixedu.bennu.saml.client.SAML2SecurityConfiguration;
import org.fenixedu.bennu.saml.client.servlet.SAMLClientInitializer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Boots the module-owned Spring context the same way SAMLClientInitializer does, and provides
 * helpers to drive the resulting Spring Security filter chain with spring-test mocks.
 */
final class SamlContextSupport {

    private SamlContextSupport() {
    }

    static AnnotationConfigWebApplicationContext boot(Class<?>... configurationClasses) {
        MockServletContext servletContext = new MockServletContext();
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(servletContext);
        context.register(configurationClasses);
        servletContext.setAttribute(SAMLClientInitializer.WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
        context.refresh();
        return context;
    }

    static Filter filterChain(AnnotationConfigWebApplicationContext context) {
        return context.getBean("springSecurityFilterChain", Filter.class);
    }

    static MockHttpServletRequest request(String method, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(method);
        request.setScheme("https");
        request.setServerName("saml-client.test.example");
        request.setServerPort(443);
        request.setRequestURI(uri);
        request.setServletPath(uri);
        request.setContextPath("");
        return request;
    }

    /**
     * A request bound to the booted context's servlet context, so that lookups through
     * {@code request.getServletContext()} (e.g. the module-owned WebApplicationContext attribute)
     * resolve like they do in the deployed webapp.
     */
    static MockHttpServletRequest request(AnnotationConfigWebApplicationContext context, String method, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest(context.getServletContext(), method, uri);
        request.setScheme("https");
        request.setServerName("saml-client.test.example");
        request.setServerPort(443);
        request.setRequestURI(uri);
        request.setServletPath(uri);
        request.setContextPath("");
        return request;
    }

    static RelyingPartyRegistration registration(RelyingPartyRegistrationRepository repository) {
        return repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);
    }
}
