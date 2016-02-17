/*
 * Bennu.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.domain;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.fenixedu.bennu.core.domain.groups.PersistentGroup;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Root class of the domain.
 */
public final class Bennu extends Bennu_Base {
    private Bennu() {
        super();
        setRoot(FenixFramework.getDomainRoot());
    }

    public static Bennu getInstance() {
        if (FenixFramework.getDomainRoot().getBennu() == null) {
            return initialize();
        }
        return FenixFramework.getDomainRoot().getBennu();
    }

    @Atomic(mode = TxMode.WRITE)
    private static Bennu initialize() {
        if (FenixFramework.getDomainRoot().getBennu() == null) {
            return new Bennu();
        }
        return FenixFramework.getDomainRoot().getBennu();
    }

    @Override
    public Set<User> getUserSet() {
        //FIXME: remove when the framework enables read-only slots
        return super.getUserSet();
    }

    @Override
    public Set<PersistentGroup> getGroupSet() {
        //FIXME: remove when the framework enables read-only slots
        return super.getGroupSet();
    }

    /**
     * Retrieves the stored property with the given name.
     *
     * @param name
     *         The name of the property to retrieve
     * @return The value of the property. Empty if the property is not defined
     * @throws NullPointerException
     *         If <code>name</code> is <code>null</code>
     */
    public Optional<String> getProperty(String name) {
        return primitiveProperty(name).map(JsonPrimitive::getAsString);
    }

    /**
     * Retrieves the stored numeric property with the given name.
     *
     * @param name
     *         The name of the property to retrieve
     * @return The value of the property. Empty if the property is not defined
     * @throws NullPointerException
     *         If <code>name</code> is <code>null</code>
     */
    public Optional<Number> getNumericProperty(String name) {
        return primitiveProperty(name).map(JsonPrimitive::getAsNumber);
    }

    /**
     * Retrieves the stored boolean property with the given name.
     *
     * @param name
     *         The name of the property to retrieve
     * @return The value of the property. Empty if the property is not defined
     * @throws NullPointerException
     *         If <code>name</code> is <code>null</code>
     */
    public Optional<Boolean> getBooleanProperty(String name) {
        return primitiveProperty(name).map(JsonPrimitive::getAsBoolean);
    }

    /**
     * Stores the given string property, associated with the given property name.
     *
     * @param name
     *         The name of the property to store
     * @param value
     *         The value to store
     * @throws NullPointerException
     *         If either <code>name</code> or <code>value</code> is <code>null</code>
     */
    public void setProperty(String name, String value) {
        setProperty(name, new JsonPrimitive(Objects.requireNonNull(value)));
    }

    /**
     * Stores the given numeric property, associated with the given property name.
     *
     * @param name
     *         The name of the property to store
     * @param value
     *         The value to store
     * @throws NullPointerException
     *         If either <code>name</code> or <code>value</code> is <code>null</code>
     */
    public void setProperty(String name, Number value) {
        setProperty(name, new JsonPrimitive(Objects.requireNonNull(value)));
    }

    /**
     * Stores the given boolean property, associated with the given property name.
     *
     * @param name
     *         The name of the property to store
     * @param value
     *         The value to store
     * @throws NullPointerException
     *         If <code>name</code> is <code>null</code>
     */
    public void setProperty(String name, boolean value) {
        setProperty(name, new JsonPrimitive(value));
    }

    /**
     * Removes the stored value of the property with the given name. Does nothing if the property is not defined.
     *
     * @param name
     *         The name of the property to remove
     * @throws NullPointerException
     *         If <code>name</code> is <code>null</code>
     */
    public void removeProperty(String name) {
        Objects.requireNonNull(name);
        if (getPropertyData() != null) {
            JsonObject json = new JsonObject();
            // Forcing the cast to JsonPrimitive, to ensure we only copy immutable JSON
            // If full JSON support is required in the future, this method should be extended
            getPropertyData().getAsJsonObject().entrySet().stream().filter(entry -> !entry.getKey().equals(name))
                    .forEach(entry -> json.add(entry.getKey(), entry.getValue().getAsJsonPrimitive()));
            setPropertyData(json);
        }
    }

    // Implementation

    private Optional<JsonPrimitive> primitiveProperty(String name) {
        Objects.requireNonNull(name);
        if (getPropertyData() == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(getPropertyData().getAsJsonObject().get(name)).filter(JsonElement::isJsonPrimitive)
                    .map(JsonElement::getAsJsonPrimitive);
        }
    }

    private void setProperty(String name, JsonPrimitive primitive) {
        Objects.requireNonNull(name);
        JsonObject json = new JsonObject();
        if (getPropertyData() != null) {
            getPropertyData().getAsJsonObject().entrySet().stream()
                    .forEach(entry -> json.add(entry.getKey(), entry.getValue().getAsJsonPrimitive()));
        }
        json.add(name, primitive);
        setPropertyData(json);
    }

}
