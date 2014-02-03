package org.fenixedu.bennu.portal.servlet;

import java.util.HashMap;
import java.util.Map;

/**
 * Central place for {@link PortalBackends} to register themselves.
 * 
 * Upon application startup, backends that wish to be discovered must register here.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public final class PortalBackendRegistry {

    /*
     * In reality this should be a ConcurrentHashMap, but we are assuming it is
     * only mutated during the single-threaded startup of the application
     */
    private static final Map<String, PortalBackend> backends = new HashMap<>();

    /**
     * Registers a new {@link PortalBackend}.
     * 
     * @param backend
     *            The backend to register
     * @throws IllegalArgumentException
     *             If another backend with the same key is already registered.
     */
    public static void registerPortalBackend(PortalBackend backend) {
        if (backends.containsKey(backend.getBackendKey())) {
            throw new IllegalArgumentException("Another backend with key " + backend.getBackendKey() + " is already registered");
        }
        backends.put(backend.getBackendKey(), backend);
    }

    /**
     * Returns the {@link PortalBackend} for the given key.
     * 
     * @param key
     *            The backend key
     * @return
     *         The {@link PortalBackend} for the given key
     * @throws UnsupportedOperationException
     *             If no backend is found for the given key
     */
    public static PortalBackend getPortalBackend(String key) {
        PortalBackend backend = backends.get(key);
        if (backend == null) {
            throw new IllegalArgumentException("No backend for key " + key);
        }
        return backend;
    }

}
