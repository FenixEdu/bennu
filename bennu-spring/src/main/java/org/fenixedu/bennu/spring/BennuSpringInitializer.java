/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of Bennu Spring.
 *
 * Bennu Spring is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Spring is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Spring.  If not, see <http://www.gnu.org/licenses/>.
 */
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
