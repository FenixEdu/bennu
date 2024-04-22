package org.fenixedu.bennu.scheduler.future;

import org.fenixedu.bennu.core.domain.Bennu;

import org.fenixedu.bennu.core.domain.Singleton;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class FutureSystem extends FutureSystem_Base {

    private FutureSystem() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static FutureSystem getInstance() {
        return Singleton.getInstance(() -> Bennu.getInstance().getFutureSystem(), () -> new FutureSystem());
    }

    public static PersistentFuture getPersistentFuture(String id) {
        return FenixFramework.getDomainObject(id);
    }

}
