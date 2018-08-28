package org.fenixedu.bennu.scheduler.custom;

import pt.ist.fenixframework.Atomic;

public abstract class ReadCustomTask extends CustomTask {
    @Override
    public Atomic.TxMode getTxMode() {
        return Atomic.TxMode.READ;
    }
}
