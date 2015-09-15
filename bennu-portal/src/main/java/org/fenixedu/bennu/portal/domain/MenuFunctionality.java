package org.fenixedu.bennu.portal.domain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.FenixFramework;

/**
 * {@link MenuFunctionality}s are the leafs of the functionality tree. They represent a concrete functionality installed in the
 * menu.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public final class MenuFunctionality extends MenuFunctionality_Base {

    /**
     * Creates a new {@link MenuFunctionality} under the given container, based on the given {@link Functionality}.
     * 
     * @param parent
     *            The parent container for the new item
     * @param functionality
     *            The {@link Functionality} that will provide the require information for this functionality
     * 
     * @throws IllegalArgumentException
     *             If {@code parent} is null.
     */
    public MenuFunctionality(MenuContainer parent, Functionality functionality) {
        super();
        if (parent == null) {
            throw new IllegalArgumentException("MenuFunctionality cannot be created without a parent!");
        }
        init(parent, functionality.isVisible(), functionality.getAccessGroup(), functionality.getTitle(),
                functionality.getDescription(), functionality.getPath());
        setDocumentationUrl("{base}");
        setItemKey(functionality.getKey());
        setProvider(functionality.getProvider());
    }

    /**
     * Creates a new {@link MenuFunctionality} under the given container, based on the provider parameters.
     * 
     * @param parent
     *            The parent container for the new item
     * @param visible
     *            Whether this functionality is to be visible when rendering the menu
     * @param key
     *            The unique key that represents this functionality
     * @param provider
     *            The provider backend that will be responsible for rendering this functionality
     * @param accessGroup
     *            The expression for this functionality's access group
     * @param description
     *            The textual description for this functionality
     * @param title
     *            The title for this functionality
     * @param path
     *            The semantic-url path for this functionality
     * 
     * @throws IllegalArgumentException
     *             If {@code parent} is null.
     */
    public MenuFunctionality(MenuContainer parent, boolean visible, String key, String provider, String accessGroup,
            LocalizedString description, LocalizedString title, String path) {
        this(parent, visible, key, provider, accessGroup, description, title, path, "");
    }

    /**
     * Creates a new {@link MenuFunctionality} under the given container, based on the provider parameters.
     *
     * @param parent
     *            The parent container for the new item
     * @param visible
     *            Whether this functionality is to be visible when rendering the menu
     * @param key
     *            The unique key that represents this functionality
     * @param provider
     *            The provider backend that will be responsible for rendering this functionality
     * @param accessGroup
     *            The expression for this functionality's access group
     * @param description
     *            The textual description for this functionality
     * @param title
     *            The title for this functionality
     * @param path
     *            The semantic-url path for this functionality
     * @param documentationUrl
     *            The URL for the documentation of this functionality
     * @throws IllegalArgumentException
     *             If {@code parent} is null.
     */
    public MenuFunctionality(MenuContainer parent, boolean visible, String key, String provider, String accessGroup,
            LocalizedString description, LocalizedString title, String path, String documentationUrl) {
        super();
        if (parent == null) {
            throw new IllegalArgumentException("MenuFunctionality cannot be created without a parent!");
        }
        init(parent, visible, accessGroup, title, description, path);
        setDocumentationUrl(documentationUrl);
        setItemKey(key);
        setProvider(provider);
    }

    /*
     * Creates a new copy of this functionality under the specified parent.
     */
    private MenuFunctionality(MenuContainer newParent, MenuFunctionality original) {
        super();
        if (newParent == null) {
            throw new IllegalArgumentException("MenuFunctionality cannot be created without a parent!");
        }
        init(newParent, original);
        setDocumentationUrl(original.getDocumentationUrl());
        setItemKey(original.getItemKey());
        setProvider(original.getProvider());
    }

    /**
     * Looks up in the functionality tree, the functionality with the given provider and key.
     * 
     * @param provider
     *            The provider of the functionality
     * @param key
     *            The key of the functionality
     * @return
     *         The {@link MenuFunctionality} with the given provider and key. {@code null} if no such functionality is installed.
     */
    public static MenuFunctionality findFunctionality(String provider, String key) {
        String functionality = "$$bennuPortal$$provider:" + provider + "$$bennu;Portal$$key:" + key;
        MenuFunctionality target =
                cache.computeIfAbsent(functionality,
                        (funct) -> findFunctionality(PortalConfiguration.getInstance().getMenu(), provider, key));
        if (target == null) {
            // null is only returned if it wasn't present in the map, and functionality isn't really installed.
            return null;
        }
        // Check if the functionality is still valid. As the key and provider are immutable, we must only
        // ensure that the Domain Object still exists.
        if (!FenixFramework.isDomainObjectValid(target)) {
            cache.remove(functionality, target);
            return findFunctionality(provider, key);
        }
        return target;
    }

    private static final ConcurrentMap<String, MenuFunctionality> cache = new ConcurrentHashMap<>();

    public String resolveLayout() {
        if (getLayout() != null) {
            return getLayout();
        }
        return getParent().resolveLayout();
    }

    @Override
    public String getProvider() {
        //FIXME: remove when the framework enables read-only slots
        return super.getProvider();
    }

    @Override
    public String getItemKey() {
        //FIXME: remove when the framework enables read-only slots
        return super.getItemKey();
    }

    public String getParsedDocumentationUrl() {
        if (this.getDocumentationUrl() != null) {
            return this.getDocumentationUrl().replaceAll("\\{base\\}",
                    PortalConfiguration.getInstance().getDocumentationBaseUrl());
        }
        return "";
    }

    /**
     * "Moves" this functionality to the selected point in the menu.
     * 
     * As {@link MenuItem}s are immutable by default, this instance is deleted, and
     * a new copy of it is created under the specified container.
     * 
     * @param newParent
     *            The new parent in which to insert the copy of this functionality
     * @return
     *         The newly created functionality
     */
    @Override
    public MenuFunctionality moveTo(MenuContainer newParent) {
        MenuFunctionality copy = new MenuFunctionality(newParent, this);
        delete();
        return copy;
    }

    // Private methods

    private static MenuFunctionality findFunctionality(MenuContainer container, String provider, String key) {
        for (MenuItem item : container.getChildSet()) {
            if (item.isMenuFunctionality()) {
                MenuFunctionality functionality = item.getAsMenuFunctionality();
                if (functionality.getProvider().equals(provider) && functionality.getItemKey().equals(key)) {
                    return functionality;
                }
            } else {
                MenuFunctionality functionality = findFunctionality(item.getAsMenuContainer(), provider, key);
                if (functionality != null) {
                    return functionality;
                }
            }
        }
        return null;
    }

}
