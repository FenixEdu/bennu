package org.fenixedu.bennu.portal.domain;

import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.commons.i18n.LocalizedString;

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
        setItemKey(functionality.getKey());
        setProvider(functionality.getProvider());
    }

    /**
     * Creates a new {@link MenuFunctionality} under the given container, based on the provider parameters.
     * 
     * @throws IllegalArgumentException
     *             If {@code parent} is null.
     */
    public MenuFunctionality(MenuContainer parent, boolean visible, String key, String provider, String accessGroup,
            LocalizedString description, LocalizedString title, String path) {
        super();
        if (parent == null) {
            throw new IllegalArgumentException("MenuFunctionality cannot be created without a parent!");
        }
        init(parent, visible, accessGroup, title, description, path);
        setItemKey(key);
        setProvider(provider);
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
        return findFunctionality(PortalConfiguration.getInstance().getMenu(), provider, key);
    }

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
