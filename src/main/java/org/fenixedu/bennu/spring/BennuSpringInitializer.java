package org.fenixedu.bennu.spring;

import javax.servlet.ServletRegistration.Dynamic;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.servlet.PortalBackendRegistry;
import org.fenixedu.bennu.spring.portal.SpringPortalBackend;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class BennuSpringInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] {};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { BennuSpringConfiguration.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected void customizeRegistration(Dynamic registration) {
        super.customizeRegistration(registration);
        registration.setMultipartConfig(CoreConfiguration.getMultipartConfigElement());
        PortalBackendRegistry.registerPortalBackend(new SpringPortalBackend());
    }

}
