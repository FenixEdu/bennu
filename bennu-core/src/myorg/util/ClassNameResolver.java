package myorg.util;

import java.util.HashMap;
import java.util.Map;

public class ClassNameResolver {

    private static Map<Class<?>, Resolver> nameMap = new HashMap<Class<?>, Resolver>();

    public static void registerType(Class<?> objectType, String bundle, String key) {
	nameMap.put(objectType, new Resolver(bundle, key));
    }

    public static String getNameFor(Class<?> objectType) {
	Resolver resolver = nameMap.get(objectType);
	return resolver == null ? objectType.getName() : BundleUtil.getStringFromResourceBundle(resolver.getBundle(), resolver
		.getKey());
    }

    private static class Resolver {
	private String bundle;
	private String key;

	public Resolver(String bundle, String key) {
	    this.bundle = bundle;
	    this.key = key;
	}

	public String getBundle() {
	    return this.bundle;
	}

	public String getKey() {
	    return this.key;
	}
    }
}
