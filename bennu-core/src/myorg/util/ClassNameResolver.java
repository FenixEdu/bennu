package myorg.util;

import java.util.HashMap;
import java.util.Map;

import pt.ist.fenixframework.DomainObject;

public class ClassNameResolver {

    private static Map<Class<? extends DomainObject>, Resolver> nameMap = new HashMap<Class<? extends DomainObject>, Resolver>();

    public static void registerType(Class<? extends DomainObject> domainType, String bundle, String key) {
	nameMap.put(domainType, new Resolver(bundle, key));
    }

    public static String getNameFor(Class<? extends DomainObject> domainType) {
	Resolver resolver = nameMap.get(domainType);
	return resolver == null ? domainType.getName() : BundleUtil.getStringFromResourceBundle(resolver.getBundle(), resolver
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
