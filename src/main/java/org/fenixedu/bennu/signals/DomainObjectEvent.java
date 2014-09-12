package org.fenixedu.bennu.signals;

import pt.ist.fenixframework.DomainObject;

public class DomainObjectEvent<T extends DomainObject> {

    private final T object;

    public DomainObjectEvent(T object) {
        this.object = object;
    }

    public T getInstance() {
        return object;
    }
}
