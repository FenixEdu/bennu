/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of Bennu Signals.
 *
 * Bennu Signals is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Signals is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Signals.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.signals;


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