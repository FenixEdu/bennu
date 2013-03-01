package pt.ist.bennu.portal.domain;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import pt.ist.bennu.service.Service;

public class MenuItem extends MenuItem_Base implements Comparable<MenuItem> {

    public MenuItem() {
        super();
        setOrd(0);
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

    @Override
    public int compareTo(MenuItem o) {
        return getOrd().compareTo(o.getOrd());
    }

    @Service
    public void delete() {
        if (hasHost()) {
            removeHost();
        }
        for (MenuItem child : getChild()) {
            child.delete();
        }
        deleteDomainObject();
    }
}
