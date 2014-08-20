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
package org.fenixedu.bennu.spring.portal;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.model.Application;
import org.fenixedu.bennu.portal.model.ApplicationRegistry;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class PortalHandlerMapping extends RequestMappingHandlerMapping {

    private static final Logger logger = LoggerFactory.getLogger(PortalHandlerMapping.class);

    private static final String[] EMPTY_ARRAY = new String[0];
    public static final String DELEGATE = "$DELEGATE_TO_PARENT$";

    @Autowired
    private MessageSource messageSource;

    private final Map<Class<?>, Functionality> functionalities = new HashMap<>();
    private final Map<Class<?>, Application> applicationClasses = new HashMap<>();

    @Override
    protected void initHandlerMethods() {
        registerApplications(getApplicationContext().getBeansWithAnnotation(SpringApplication.class).values());
        registerFunctionalities(getApplicationContext().getBeansWithAnnotation(SpringFunctionality.class).values());
        registerLonelyControllers(getApplicationContext().getBeansWithAnnotation(BennuSpringController.class).values());
        super.initHandlerMethods();
    }

    private void registerLonelyControllers(Collection<Object> values) {
        for (Object bean : values) {
            Class<?> type = bean.getClass();
            Class<?> functionalityType = AnnotationUtils.findAnnotation(type, BennuSpringController.class).value();
            Functionality functionality = functionalities.get(functionalityType);
            if (functionality == null) {
                throw new Error("Controller " + type.getName() + " declares " + functionalityType.getName()
                        + " as a functionality, but it is not one...");
            }
            functionalities.put(type, functionality);
        }
    }

    private void registerApplications(Collection<Object> values) {
        for (Object bean : values) {
            Class<?> type = bean.getClass();
            SpringApplication app = AnnotationUtils.findAnnotation(type, SpringApplication.class);
            LocalizedString title = getLocalized(app.title());
            Application application =
                    new Application(type.getName(), app.path(), app.group(), title,
                            app.description().equals(DELEGATE) ? title : getLocalized(app.description()), app.hint());
            applicationClasses.put(type, application);
            ApplicationRegistry.registerApplication(application);
            logger.debug("Registered application for type {}", type);
        }
    }

    private void registerFunctionalities(Collection<Object> values) {
        for (Object bean : values) {
            Class<?> type = bean.getClass();
            SpringFunctionality model = AnnotationUtils.findAnnotation(type, SpringFunctionality.class);
            RequestMapping mapping = AnnotationUtils.findAnnotation(type, RequestMapping.class);
            if (mapping == null) {
                throw new Error("Functionality type " + type.getName() + " does not declare a @RequestMapping!");
            }
            Application app = applicationClasses.get(model.app());
            String path = extractPath(mapping, type);
            LocalizedString title = getLocalized(model.title());
            Functionality functionality =
                    new Functionality(SpringPortalBackend.BACKEND_KEY, "/" + path, path.replace('/', '-'), model.accessGroup()
                            .equals(DELEGATE) ? app.getAccessGroup() : model.accessGroup(), title, model.description().equals(
                            DELEGATE) ? title : getLocalized(model.description()));
            app.addFunctionality(functionality);
            functionalities.put(type, functionality);
        }
    }

    private String extractPath(RequestMapping mapping, Class<?> type) {
        if (ObjectUtils.isEmpty(mapping.value())) {
            throw new Error("Functionality type " + type.getName() + " does not declare any @RequestMapping mappings!");
        }
        String path = mapping.value()[0];
        return path.startsWith("/") ? path.substring(1) : path;
    }

    @Override
    protected HandlerMethod createHandlerMethod(Object handler, Method method) {
        HandlerMethod handlerMethod = super.createHandlerMethod(handler, method).createWithResolvedBean();
        Functionality functionality = functionalities.get(handlerMethod.getBeanType());
        return new PortalHandlerMethod(handlerMethod, functionality);
    }

    private LocalizedString getLocalized(String key) {
        LocalizedString.Builder builder = new LocalizedString.Builder();
        for (Locale locale : CoreConfiguration.supportedLocales()) {
            builder.with(locale, messageSource.getMessage(key, EMPTY_ARRAY, "!!" + key + "!!", locale));
        }
        return builder.build();
    }

}
