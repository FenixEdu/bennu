package org.fenixedu.bennu.core.util;

/**
 * 
 * @author Pedro Santos
 * 
 */
public class ClassInternalizer {
    public static Class<?> internalize(String classname) {
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
