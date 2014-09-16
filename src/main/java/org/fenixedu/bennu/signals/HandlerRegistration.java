package org.fenixedu.bennu.signals;

/**
 * Registration objects returned when an event handler is bound (e.g. via {@link Signal#register(String, Object)}), used to
 * deregister.
 * 
 * @author Artur Ventura
 * 
 */
public final class HandlerRegistration {
    private final Object handler;
    private final String key;

    HandlerRegistration(String key, Object handler) {
        this.handler = handler;
        this.key = key;
    }

    /**
     * Returns the key for this handler registration.
     * 
     * @return the key that this handler was registred on.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the handler that was registered.
     * 
     * @return the handler that was registered.
     */
    public Object getHandler() {
        return handler;
    }

    /**
     * Unregisteres the handler from the Signaling system.
     * 
     */
    public void unregister() {
        Signal.unregister(key, handler);
    }
}