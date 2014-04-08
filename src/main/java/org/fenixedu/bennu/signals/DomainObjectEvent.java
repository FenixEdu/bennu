package org.fenixedu.bennu.signals;

import pt.ist.fenixframework.DomainObject;

public class DomainObjectEvent<T extends DomainObject> {
    private T o;
    public DomainObjectEvent(T o) {
        this.o = o;
    }
    public T getInstance() {
        return o;
    }
}
