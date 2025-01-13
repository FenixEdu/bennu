package org.fenixedu.bennu.portal.domain;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.qubit.terra.framework.services.ServiceProvider;
import com.qubit.terra.framework.services.context.ApplicationUser;
import com.qubit.terra.portal.domain.layouts.Layout;
import com.qubit.terra.portal.domain.menus.MenuVisibility;
import com.qubit.terra.portal.services.layouts.LayoutProviderRegistry;

import pt.ist.fenixframework.Atomic;

/**
 * Base class for items that are presented in an application's menu.
 * 
 * {@code MenuItem}s are either a {@link MenuContainer}, aggregating other items or a {@link MenuFunctionality}, representing a
 * concrete entry in the menu.
 * 
 * Note that a {@link MenuItem} is immutable, meaning that once it is installed in the menu, it is not possible to modify any of
 * its properties. If you desire to do so, {@code delete} the item and create a new one with the new values.
 * 
 * {@code MenuItem}s are {@link Comparable} according to their order in the menu.
 * 
 * @see MenuContainer
 * @see MenuFunctionality
 * 
 */
public abstract class MenuItem extends MenuItem_Base implements com.qubit.terra.portal.domain.menus.MenuItem {

    private static final Supplier<LayoutProviderRegistry> layoutProviderRegistrySupplier =
            Suppliers.memoize(() -> ServiceProvider.getService(LayoutProviderRegistry.class));

    protected MenuItem() {
        super();
        setOrd(1);
    }

    protected final void init(MenuContainer parent, boolean visible, String accessGroup, LocalizedString title,
            LocalizedString description, String path) {
        setVisible(visible);
        setVisibility(visible ? MenuVisibility.ALL.name() : MenuVisibility.INVISIBLE.name());
        setAccessGroup(Group.parse(accessGroup));
        setDescription(description);
        setTitle(title);
        setPath(path);
        if (parent != null) {
            parent.addChild(this);
        }
        setFullPath(computeFullPath());
    }

    protected final void init(MenuContainer parent, MenuItem original) {
        setVisible(original.getVisible());
        setVisibility(original.getVisibility());
        setAccessGroup(original.getAccessGroup());
        setDescription(original.getDescription());
        setTitle(original.getTitle());
        setPath(original.getPath());
        setLayout(original.getLayout());
        if (parent != null) {
            parent.addChild(this);
        }
        setFullPath(computeFullPath());
    }

    public Group getAccessGroup() {
        return getGroup().toGroup();
    }

    public void setAccessGroup(Group group) {
        setGroup(group.toPersistentGroup());
    }

    @Override
    protected void setGroup(PersistentGroup group) {
        super.setGroup(group);
        if (getParent() != null) {
            getParent().updateAccessGroup();
        }
    }

    @Override
    public boolean isMenuContainer() {
        return this instanceof MenuContainer;
    }

    @Override
    public boolean isMenuFunctionality() {
        return this instanceof MenuFunctionality;
    }

    /**
     * Deletes this item, removing it from the menu.
     */
    @Atomic
    @Override
    public void delete() {
        setParent(null);
        setGroup(null);
        deleteDomainObject();
    }

    /**
     * Determines whether this {@link MenuItem} and all its parents are available for the given {@link User}.
     * 
     * @param user
     *            The user to verify
     * @return
     *         Whether the given user can access this item
     */
    public boolean isAvailable(User user) {
        return getGroup().isMember(user) && getParent().isAvailable(user);
    }

    /*
     * Returns whether this item is available for the current user.
     * Implementation Node: This method ONLY checks the current node, not the full chain!
     */
    protected boolean isItemAvailableForCurrentUser() {
        return getGroup().isMember(Authenticate.getUser());
    }

    @Deprecated
    public boolean isVisible() {
        return getVisible();
    }

    @Deprecated
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    @Override
    public MenuVisibility getItemVisibility() {
        return getVisibility() == null ? null : MenuVisibility.valueOf(getVisibility());
    }

    @Override
    public void setItemVisibility(MenuVisibility visibility) {
        if (visibility != null) {
            setVisibility(visibility.name());
        }
    }

    @Override
    public void setItemVisible(boolean visible) {
        setVisible(visible);
    }

    @Override
    public MenuContainer getParent() {
        //FIXME: remove when the framework enables read-only slots
        return super.getParent();
    }

    @Override
    public String getPath() {
        //FIXME: remove when the framework enables read-only slots
        return super.getPath();
    }

    @Override
    public String getFullPath() {
        //FIXME: remove when the framework enables read-only slots
        return super.getFullPath();
    }

    public MenuContainer getAsMenuContainer() {
        if (isMenuContainer()) {
            return (MenuContainer) this;
        }
        throw new IllegalStateException("Not a MenuContainer");
    }

    public MenuFunctionality getAsMenuFunctionality() {
        if (isMenuFunctionality()) {
            return (MenuFunctionality) this;
        }
        throw new IllegalStateException("Not a MenuFunctionality");
    }

    public abstract MenuItem moveTo(MenuContainer container);

    // This method needs to be overridden so that this class's
    // metaholder contains a PATH_FROM_ROOT() method, which is
    // currently being used by other modules, most notably,
    // fenixedu-fcul-api.
    //
    // 19 October 2024 - Francisco Esteves
    public List<com.qubit.terra.portal.domain.menus.MenuItem> getPathFromRoot() {
        return com.qubit.terra.portal.domain.menus.MenuItem.super.getPathFromRoot();
    }

    @Override
    public boolean isItemRestricted() {
        if (getParent() == null) {
            return true;
        }
        return getRestricted() != null ? getRestricted() : getParent().isItemRestricted();
    }

    public String getRecursiveProviderImplementation() {
        if (!StringUtils.isEmpty(getProviderImplementation())) {
            return getProviderImplementation();
        }
        return getParent() != null ? getParent().getRecursiveProviderImplementation() : null;
    }

    @Override
    public Integer getPosition() {
        return super.getOrd();
    }

    @Override
    public void setItemName(com.qubit.terra.framework.tools.primitives.LocalizedString name) {
        setTitle(BundleUtil.convertToBennuLocalizedString(name));
    }

    @Override
    public void setPosition(Integer position) {
        super.setOrd(position);
    }

    @Override
    public com.qubit.terra.framework.tools.primitives.LocalizedString getItemName() {
        return BundleUtil.convertToPlatformLocalizedString(super.getTitle());
    }

    @Override
    public String getItemPath() {
        return getPath();
    }

    @Override
    public String getFullItemPath() {
        return getFullPath();
    }

    @Override
    public void setItemPath(String path) {
        setPath(path);
    }

    @Override
    public String getItemIcon() {
        return super.getIcon();
    }

    @Override
    public void setItemIcon(String icon) {
        setIcon(icon);
    }

    @Override
    public String getMenuLayout() {
        return super.getLayout();
    }

    @Override
    public void setMenuLayout(String layout) {
        setLayout(layout);
    }

    @Override
    public void setMenuItemFullPath(String path) {
        setFullPath(path);
    }

    @Override
    public com.qubit.terra.framework.tools.primitives.LocalizedString getItemDescription() {
        return BundleUtil.convertToPlatformLocalizedString(super.getDescription());
    }

    /**
     * Determines whether this {@link MenuItem} and all its parents are available for a given user.
     * This method is a shorthand for <code>isAvailable(User user)</code>.
     * 
     * @return
     *         Whether the user can access this item
     */
    @Override
    public boolean isAvailableForUser(ApplicationUser appUser) {
        return isAvailable(getBennuUser(appUser));
    }

    @Override
    public com.qubit.terra.portal.domain.menus.MenuContainer getParentContainer() {
        return super.getParent();
    }

    @Override
    public String getItemProviderImplementation() {
        return getProviderImplementation();
    }

    @Override
    public void setItemProviderImplementation(String providerImplementation) {
        this.setProviderImplementation(providerImplementation);
    }

    @Override
    public MenuContainer asMenuContainer() {
        return getAsMenuContainer();
    }

    @Override
    public MenuFunctionality asMenuFunctionality() {
        return getAsMenuFunctionality();
    }

    @Override
    public String getAccessControlExpression() {
        return this.getAccessGroup() == null ? null : this.getAccessGroup().getExpression();
    }

    @Override
    public void setAccessControlExpression(String expression) {
        setAccessGroup(Group.parse(expression));
    }

    @Override
    public void setItemDescription(com.qubit.terra.framework.tools.primitives.LocalizedString description) {
        this.setDescription(BundleUtil.convertToBennuLocalizedString(description));
    }

    @Override
    public void setItemRestricted(Boolean restricted) {
        setRestricted(restricted);
    }

    protected User getBennuUser(ApplicationUser appUser) {
        return appUser != null ? User.findByUsername(appUser.getUsername()) : null;
    }

    @Override
    public Layout getLayoutObject() {
        // Since there is currently only one implementation
        // of the LayoutProvider interface in Fenix, we can
        // assume that the map returned by LayoutProviderRegistry's
        // getLayoutProvider method will contain one and only
        // one LayoutProvider object
        //
        // 20.12.2024 - Francisco Esteves
        return layoutProviderRegistrySupplier.get().getLayoutProviders().values().iterator().next().getLayout(getLayout())
                .orElse(null);
    }

    @Override
    public void setLayoutObject(Layout layout) {
        setLayout(layout.getKey());
    }

    @Override
    public Layout resolveLayoutObject() {
        return Optional.ofNullable(com.qubit.terra.portal.domain.menus.MenuItem.super.resolveLayoutObject())
                .orElse(layoutProviderRegistrySupplier.get().getLayoutProviders().values().iterator().next().getLayout("default")
                        .orElse(null));
    }

}
