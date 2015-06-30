package org.fenixedu.bennu.core.util;

import java.util.HashMap;
import java.util.Map;

import org.fenixedu.bennu.core.annotation.BennuCoreAnnotationInitializer;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.Project;

public class ModuleMapper {
    private static Map<String, String> resourceModuleMap = new HashMap<>();

    static {
        for (Project artifact : FenixFramework.getProject().getProjects()) {
            String projectResource = "/" + artifact.getName() + "/project.properties";
            String url = BennuCoreAnnotationInitializer.class.getResource(projectResource).toExternalForm();
            if (url.startsWith("jar")) {
                resourceModuleMap.put(url.substring("jar:".length(), url.length() - projectResource.length() - 1),
                        artifact.getName());
            } else {
                resourceModuleMap.put(url.replace(projectResource, ""), artifact.getName());
            }
        }
    }

    /**
     * Finds the module that contains the requested class type. Will signal an {@link Error} when the class is not found in any
     * module, like when the class is from an external library and not a framework module.
     * 
     * @param type The {@link Class} object to find.
     * @return The name of the module that contains it.
     */
    public static String getModuleOf(Class<?> type) {
        String typeLocation = type.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
        if (resourceModuleMap.containsKey(typeLocation)) {
            return resourceModuleMap.get(typeLocation);
        }
        for (String path : resourceModuleMap.keySet()) {
            if (typeLocation.startsWith(path)) {
                return resourceModuleMap.get(path);
            }
        }
        throw new Error("Type: " + type.getName() + " not found on any module");
    }
}
