package org.fenixedu.bennu.toolkit;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.fenixedu.bennu.toolkit.components.Component;
import org.fenixedu.bennu.toolkit.components.ToolkitComponent;

@HandlesTypes({ ToolkitComponent.class })
public class ToolkitInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        if (c != null) {
            for (Class<?> type : c) {
                if (type.isAnnotationPresent(ToolkitComponent.class)) {
                    Component.register(type);
                }
            }
        }
    }
}
