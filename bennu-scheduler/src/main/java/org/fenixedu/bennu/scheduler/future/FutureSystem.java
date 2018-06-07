package org.fenixedu.bennu.scheduler.future;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class FutureSystem extends FutureSystem_Base {

    private FutureSystem() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static FutureSystem getInstance() {
        if (Bennu.getInstance().getFutureSystem() == null) {
            return initialize();
        }
        return Bennu.getInstance().getFutureSystem();
    }

    @Atomic(mode = TxMode.WRITE)
    private static FutureSystem initialize() {
        if (Bennu.getInstance().getFutureSystem() == null) {
            return new FutureSystem();
        }
        return Bennu.getInstance().getFutureSystem();
    }

    public static PersistentFuture getPersistentFuture(String id) {
        return FenixFramework.getDomainObject(id);
    }

}
