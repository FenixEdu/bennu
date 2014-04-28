package org.fenixedu.bennu.portal.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Central place for {@link PortalBackends} to register their specific {@link Applications}.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public final class ApplicationRegistry {

    private static final Map<String, Application> applications = new HashMap<String, Application>();

    /**
     * Registers a new {@link Application}
     * 
     * @param application
     *            The application to register
     * @throws IllegalArgumentException
     *             If another application with the same key already exists
     */
    public static void registerApplication(Application application) {
        if (applications.containsKey(application.getKey())) {
            throw new IllegalArgumentException("Application " + application.getKey() + " is already registered");
        }
        applications.put(application.getKey(), application);
    }

    /**
     * Returns all registered {@link Application}s
     */
    public static Collection<Application> availableApplications() {
        return Collections.unmodifiableCollection(applications.values());
    }

    /**
     * Returns a registered {@link Application} by its key.
     * 
     * @param key
     *            The key of the desired application
     * @throws IllegalStateException
     *             If no application with the given key is registered
     */
    public static Application getAppByKey(String key) {
        if (!applications.containsKey(key)) {
            throw new IllegalArgumentException("No application registered under key " + key);
        }
        return applications.get(key);
    }

}
