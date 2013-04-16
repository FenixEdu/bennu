package pt.ist.bennu.portal.domain;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.BennuGroup;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.core.util.MultiLanguageString;
import pt.ist.bennu.dispatch.AppServer;
import pt.ist.bennu.service.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class MenuItem extends MenuItem_Base implements Comparable<MenuItem> {

    public MenuItem() {
        super();
        setOrd(1);
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
        return getChild().size() + 1;
    }

    public Set<MenuItem> getOrderedChild() {
        return Collections.unmodifiableSet(new TreeSet<>(getChild()));
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

    @Service
    public void delete() {
        setParent(null);
        if (hasHost()) {
            removeHost();
        }
        for (MenuItem child : getChild()) {
            child.delete();
        }
        deleteDomainObject();
    }

    @Override
    public MultiLanguageString getDescription() {
        final MultiLanguageString description = super.getDescription();
        if (description == null) {
            return AppServer.getDescription(getPath());
        }
        return description;
    }

    @Override
    public MultiLanguageString getTitle() {
        final MultiLanguageString title = super.getTitle();
        if (title == null) {
            return AppServer.getTitle(getPath());
        }
        return title;
    }

    @Override
    public void setTitle(MultiLanguageString title) {
        if (!isFunctionalityLink()) {
            super.setTitle(title);
        }
    }

    @Override
    public void setDescription(MultiLanguageString description) {
        if (!isFunctionalityLink()) {
            super.setDescription(description);
        }
    }

    public Boolean isFunctionalityLink() {
        return AppServer.hasFunctionality(getPath());
    }

    public Boolean isAvailable(User user) {
        return BennuGroup.parse(getAccessExpression()).isMember(user);
    }
}
