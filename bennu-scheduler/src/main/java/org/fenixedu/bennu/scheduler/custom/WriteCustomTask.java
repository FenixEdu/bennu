package org.fenixedu.bennu.scheduler.custom;

import pt.ist.fenixframework.Atomic;

public abstract class WriteCustomTask extends CustomTask {
    @Override
    public Atomic.TxMode getTxMode() {
        return Atomic.TxMode.WRITE;
    }
}
