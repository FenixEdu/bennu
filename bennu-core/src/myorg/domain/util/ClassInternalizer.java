package myorg.domain.util;

public class ClassInternalizer {
    public static Class<?> internalize(String classname) {
	try {
	    return Class.forName(classname);
	} catch (ClassNotFoundException e) {
	    throw new RuntimeException(e);
	}
    }
}
