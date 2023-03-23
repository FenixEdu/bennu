package org.fenixedu.bennu.portal.domain;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.portal.model.Application;
import org.fenixedu.bennu.portal.model.ApplicationRegistry;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;

/**
 * A {@link MenuContainer} represents an inner node in the functionality tree. It may hold {@link MenuFunctionality}s or other
 * containers, forming a tree-like structure.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public class MenuContainer extends MenuContainer_Base {

    /**
     * Used to create the {@link MenuContainer} that will represent the root of the
     * functionality tree.
     * 
     * @param configuration
     *            The {@link PortalConfiguration} in which the functionality tree will be installed.
     */
    MenuContainer(PortalConfiguration configuration) {
        super();
        if (configuration.getMenu() != null && configuration.getMenu() != this) {
            throw new RuntimeException("There can be only one root menu!");
        }
        setConfiguration(configuration);
        init(null, false, "anyone", new LocalizedString(), new LocalizedString(), "ROOT_PATH");
    }

    /**
     * Creates a new {@link MenuContainer} based on the given {@link Application}, and inserts it under the given parent.
     * 
     * @param parent
     *            The parent container for the new item
     * @param application
     *            The {@link Application} that will provide the require information for this container
     * 
     * @throws IllegalArgumentException
     *             If {@code parent} is null.
     */
    public MenuContainer(MenuContainer parent, Application application) {
        super();
        if (parent == null) {
            throw new IllegalArgumentException("MenuContainer requires a parent Container!");
        }
        init(parent, true, application.getAccessGroup(), application.getTitle(), application.getDescription(),
                application.getPath());
        for (Functionality functionality : application.getFunctionalities()) {
            new MenuFunctionality(this, functionality);
        }
    }

    /**
     * Creates a new {@link MenuContainer} based on the provider parameters, and inserts it under the given parent.
     * 
     * @param parent
     *            The parent container for the new item
     * @param visible
     *            Whether this container is to be visible when rendering the menu
     * @param accessGroup
     *            The expression for this container's access group
     * @param description
     *            The textual description for this container
     * @param title
     *            The title for this container
     * @param path
     *            The semantic-url path for this container
     * 
     * @throws IllegalArgumentException
     *             If {@code parent} is null.
     */
    public MenuContainer(MenuContainer parent, boolean visible, String accessGroup, LocalizedString description,
            LocalizedString title, String path) {
        super();
        if (parent == null) {
            throw new IllegalArgumentException("MenuFunctionality cannot be created without a parent!");
        }
        init(parent, visible, accessGroup, title, description, path);
    }

    /*
     * Creates a new copy of this container. It does NOT replicate the container's children.
     */
    private MenuContainer(MenuContainer parent, MenuContainer original) {
        super();
        if (parent == null) {
            throw new IllegalArgumentException("MenuFunctionality cannot be created without a parent!");
        }
        init(parent, original);
    }

    /**
     * Adds a given {@link MenuItem} as the last child of this container.
     * 
     * @param child
     *            The child element to add to this container
     * 
     * @throws BennuPortalDomainException
     *             If another child with the same path already exists
     */
    @Override
    public void addChild(MenuItem child) throws BennuPortalDomainException {
        addChild(child, getNextOrder());
    }

    private Integer getNextOrder() {
        return getChildSet().size() + 1;
    }

    public void updateFullPath() {
        super.updateFullPath();
        for (MenuItem item : getChildSet()) {
            item.updateFullPath();
        }
    }

    /**
     * Adds a given {@link MenuItem} as a child of this container, in the given position.
     * 
     * @param child
     *            The child element to add to this container
     * @param order
     *            The position this item will be inserted at
     * 
     * @throws BennuPortalDomainException
     *             If another child with the same path already exists
     */
    public void addChild(MenuItem child, Integer order) throws BennuPortalDomainException {
        child.setOrd(order);
        for (MenuItem existing : getChildSet()) {
            if (existing.getPath().equals(child.getPath())) {
                throw BennuPortalDomainException.childWithPathAlreadyExists(child.getPath());
            }
        }
        super.addChild(child);
    }

    /**
     * Returns the children of this container, sorted by their order
     * 
     * @return
     *         The ordered children of this container
     */
    public Set<MenuItem> getOrderedChild() {
        return Collections.unmodifiableSet(new TreeSet<>(getChildSet()));
    }

    /**
     * Returns the User Menu as a lazy {@link Stream}. This method is preferred
     * to the alternatives (returning {@link Set}), as it allows further optimizations.
     * 
     * @return
     *         The User's Menu as a Stream
     */
    public Stream<MenuItem> getUserMenuStream() {
        return getChildSet().stream().filter((item) -> item.isVisible() && item.isItemAvailableForCurrentUser()).sorted();
    }

    /**
     * Deletes this container, as well as all its children.
     */
    @Atomic
    @Override
    public void delete() {
        if (getConfiguration() != null) {
            throw BennuPortalDomainException.cannotDeleteRootContainer();
        }

        setConfigurationFromSubRoot(null);
        for (MenuItem child : getChildSet()) {
            child.delete();
        }
        super.delete();
    }

    /**
     * Returns whether this container is the root of the functionality tree.
     * 
     * @return
     *         {@code true} if this is the root container.
     */
    public boolean isRoot() {
        return getConfiguration() != null;
    }

    public String resolveLayout() {
        if (getConfiguration() != null) {
            // Portal root might have specific layout
            return !StringUtils.isBlank(getLayout()) ? getLayout() : "default";
        }
        if (!StringUtils.isBlank(getLayout())) {
            return getLayout();
        }
        return getParent().resolveLayout();
    }

    @Override
    public boolean isAvailable(User user) {
        return isRoot() ? true : super.isAvailable(user);
    }

    /**
     * Traverses the functionality tree looking for the branch that matches the given path.
     * Lookup starts with the children of the current Container.
     * 
     * Note that this method is aware of the availability of tree branches, meaning that if
     * the current user doesn't have access to the selected functionality, this method will return null.
     * 
     * @param parts
     *            The path to be looked up
     * @return
     *         The {@link MenuFunctionality} matching the given path, or null if no functionality matches or the user has no
     *         access.
     */
    public final MenuFunctionality findFunctionalityWithPath(String[] parts) {
        return findFunctionalityWithPath(parts, 0, true);
    }

    public final MenuFunctionality findFunctionalityWithPathWithoutAccessControl(String[] parts) {
        return findFunctionalityWithPath(parts, 0, false);
    }

    /**
     * Helper method that receives the array index to look up, thus avoiding creation of helper objects (such as lists).
     * 
     * The functionality of this method is as follows:
     * 
     * <ol>
     * <li>
     * If there are no more elements in the path, find the initial content ({@link #findInitialContent()}) of this
     * {@link MenuContainer}</li>
     * 
     * <li>
     * For each child of this container:
     * <ol>
     * <li>It checks if the child is available</li>
     * <li>It checks if the child matches the given part of the path</li>
     * <li>If both of the above are true:
     * 
     * <ul>
     * <li>If the selected item is a functionality, return it. Note that it is possible that this element is not the last in the
     * path, allowing custom processing.</li>
     * <li>
     * If the selected item if a container, continue searching, looking for the next element in the path</li>
     * </ul>
     * </li>
     * </ol>
     * </li>
     * <li>
     * If no item was found, return {@code null}.</li>
     * </ol>
     */
    private final MenuFunctionality findFunctionalityWithPath(String[] parts, int startIndex,
            Boolean checkUserAccessToFunctionality) {
        // 1)
        if (parts.length == startIndex) {
            return findInitialContent();
        }

        // 2)
        for (MenuItem child : getChildSet()) {
            if (child.getPath().equals(parts[startIndex])
                    && checkUserAccessToFunctionality(child, checkUserAccessToFunctionality)) {
                if (child.isMenuFunctionality()) {
                    return child.getAsMenuFunctionality();
                } else {
                    return child.getAsMenuContainer().findFunctionalityWithPath(parts, startIndex + 1,
                            checkUserAccessToFunctionality);
                }
            }
        }

        // 3)
        return null;
    }

    private boolean checkUserAccessToFunctionality(MenuItem menuItem, Boolean checkAccess) {
        return checkAccess ? menuItem.isItemAvailableForCurrentUser() : true;
    }

    /**
     * Returns the initial content of this container, i.e., the first {@link MenuFunctionality}.
     * 
     * @return
     *         The initial content of this container
     */
    public MenuFunctionality findInitialContent() {
        for (MenuItem item : getOrderedChild()) {
            if (item.isItemAvailableForCurrentUser()) {
                if (item.isMenuFunctionality()) {
                    return item.getAsMenuFunctionality();
                } else {
                    MenuFunctionality functionality = item.getAsMenuContainer().findInitialContent();
                    if (functionality != null) {
                        return functionality;
                    }
                }
            }
        }
        return null;
    }

    /**
     * "Moves" this container to the selected point in the menu.
     * 
     * As {@link MenuItem}s are immutable by default, this instance is deleted, and
     * a new copy of it is created under the specified container. This process is
     * recursive, meaning that all the current children will be replicated as well.
     * 
     * @param newParent
     *            The new parent in which to insert the copy of this container
     * @return
     *         The newly created container
     */
    @Override
    public MenuContainer moveTo(MenuContainer newParent) {
        MenuContainer copy = new MenuContainer(newParent, this);
        for (MenuItem item : getChildSet()) {
            item.moveTo(copy);
        }
        super.delete();
        return copy;
    }

    public boolean isSubRoot() {
        return getConfigurationFromSubRoot() != null;
    }

    @Override
    public boolean isVisible() {
        return !isSubRoot() && super.isVisible();
    }

    public static MenuContainer createSubRoot(String key, LocalizedString title, LocalizedString description) {
        MenuContainer container = new MenuContainer(PortalConfiguration.getInstance().getMenu(), false,
                Group.anyone().getExpression(), description, title, key);
        PortalConfiguration.getInstance().addSubRoot(container);
        return container;
    }

    @Override
    public void setPath(String path) {
        if (!path.equals(getPath())) {
            super.setPath(path);
            updateFullPath();
        }
    }

    public void updateAccessGroup() {
        String groupExpression =
                getChildSet().stream().map(item -> item.getAccessGroup().getExpression()).collect(Collectors.joining(" | "));
        setAccessGroup(Group.parse(groupExpression));
    }

    public Set<Application> getApplications() {
        String availableApplicationNames = getAvailableApplicationNames();
        return availableApplicationNames == null ? Collections.emptySet() : Stream.of(availableApplicationNames.split(","))
                .map(key -> ApplicationRegistry.getAppByKey(key))
                .filter(app -> app != null && app.getFunctionalities().size() > 0).collect(Collectors.toSet());
    }

    public void setApplications(Set<Application> applications) {
        if (applications != null && !applications.isEmpty()) {
            setAvailableApplicationNames(applications.stream().map(app -> app.getKey()).collect(Collectors.joining(",")));
        } else {
            setAvailableApplicationNames(null);
        }
    }
}
