package org.fenixedu.bennu.scheduler.future;

public enum PersistentFutureState {
    WAITING, PROCESSING, SUCCESS, FAILURE, CANCELLED;

    public boolean isDone() {
        return this.compareTo(PersistentFutureState.SUCCESS) >= 0;
    }
}
