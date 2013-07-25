package pt.ist.bennu.portal.domain;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.Group;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.portal.AppServer;
import pt.ist.dsi.commons.i18n.LocalizedString;
import pt.ist.fenixframework.Atomic;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class MenuItem extends MenuItem_Base implements Comparable<MenuItem> {

    public MenuItem() {
        super();
        setOrd(1);
        setDescription(new LocalizedString());
    }

    @Override
    public void addChild(MenuItem child) {
        addChild(child, getNextOrder());
    }

    public void addChild(MenuItem child, Integer order) {
        child.setOrd(order);
        super.addChild(child);
    }

    private Integer getNextOrder() {
        return getChildSet().size() + 1;
    }

    public Set<MenuItem> getOrderedChild() {
        return Collections.unmodifiableSet(new TreeSet<>(getChildSet()));
    }

    public Set<MenuItem> getUserMenu() {
        return FluentIterable.from(getOrderedChild()).filter(new Predicate<MenuItem>() {

            @Override
            public boolean apply(MenuItem menu) {
                return menu.isAvailable(Authenticate.getUser());
            }
        }).toSet();
    }

    @Override
    public int compareTo(MenuItem o) {
        return getOrd().compareTo(o.getOrd());
    }

    @Atomic
    public void delete() {
        setParent(null);
        setConfiguration(null);
        for (MenuItem child : getChildSet()) {
            child.delete();
        }
        deleteDomainObject();
    }

    @Override
    public LocalizedString getDescription() {
        final LocalizedString description = super.getDescription();
        if (description == null) {
            return AppServer.getDescription(getPath());
        }
        return description;
    }

    @Override
    public LocalizedString getTitle() {
        final LocalizedString title = super.getTitle();
        if (title == null) {
            return AppServer.getTitle(getPath());
        }
        return title;
    }

    @Override
    public void setTitle(LocalizedString title) {
        if (!isFunctionalityLink()) {
            super.setTitle(title);
        }
    }

    @Override
    public void setDescription(LocalizedString description) {
        if (!isFunctionalityLink()) {
            super.setDescription(description);
        }
    }

    public Boolean isFunctionalityLink() {
        return AppServer.hasFunctionality(getPath());
    }

    public Boolean isAvailable(User user) {
        return Group.parse(getAccessExpression()).isMember(user);
    }
}
