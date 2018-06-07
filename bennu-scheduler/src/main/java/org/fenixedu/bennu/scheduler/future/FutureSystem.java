package org.fenixedu.bennu.scheduler.future;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class FutureSystem extends FutureSystem_Base {

    /*
    {
        // restart incomplete PersistentFuture
        System.out.println("Restarting incomplete PersistentFuture");
        s.getPersistentFutureSet().stream().filter((pf) -> !pf.isDone()).forEach((pf) -> {
            System.out.println("Restarting PersistentFuture " + pf.getExternalId());
            pf.restart();
        });
    
        System.out.println("Deleting incomplete PersistentFuture");
        FutureSystem.getInstance().getPersistentFutureSet().stream().forEach((pf) -> {
            System.out.println("Deleting PersistentFuture " + pf.getExternalId());
            pf.delete();
        });
    }
         */

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

}
