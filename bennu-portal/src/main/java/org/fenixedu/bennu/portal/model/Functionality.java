package org.fenixedu.bennu.portal.model;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.servlet.PortalBackend;
import org.fenixedu.bennu.portal.servlet.PortalBackendRegistry;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonObject;

/**
 * An {@link Functionality} represents a concrete functionality, provided by a {@link PortalBackend}.
 * 
 * This class acts as a model descriptor, which is used to create {@link MenuFunctionality}.
 * 
 * <p>
 * Note that the key of a {@link Functionality} MUST be unique per provider, meaning that it is invalid for provider 'X' to
 * declare two functionalities with key 'Y', but it is acceptable for two different providers to declare functionalities with the
 * same key.
 * </p>
 * 
 * @see Application
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public final class Functionality implements Comparable<Functionality> {

    private final String provider;

    private final String key;

    private final String path;

    private final String accessGroup;

    private final LocalizedString title;

    private final LocalizedString description;

    private final boolean visible;

    public Functionality(String provider, String key, String path, String accessGroup, LocalizedString title,
            LocalizedString description) {
        this(provider, key, path, accessGroup, title, description, true);
    }

    public Functionality(String provider, String key, String path, String accessGroup, LocalizedString title,
            LocalizedString description, boolean visible) {
        this.provider = provider;
        this.key = key;
        this.path = path;
        this.accessGroup = accessGroup;
        this.title = title;
        this.description = description;
        this.visible = visible;
    }

    /**
     * Returns the {@link PortalBackend} that is responsible for handling this functionality. This name must match
     * the key of a provider registered at {@link PortalBackendRegistry}.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * A functionality's key is a logical descriptor, used to uniquely identify a functionality externally.
     * 
     * @return This functionality's key
     */
    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }

    public String getAccessGroup() {
        return accessGroup;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public boolean isVisible() {
        return visible;
    }

    public JsonObject json() {
        JsonObject obj = new JsonObject();
        obj.addProperty("path", path);
        obj.addProperty("accessGroup", accessGroup);
        obj.addProperty("key", key);
        obj.add("title", title.json());
        obj.add("description", description.json());
        obj.addProperty("provider", provider);
        return obj;
    }

    @Override
    public String toString() {
        return json().toString();
    }

    @Override
    public int compareTo(Functionality other) {
        if (!this.visible && other.visible) {
            return -1;
        }
        if (this.visible && !other.visible) {
            return 1;
        }
        return this.title.compareTo(other.title);
    }

}
