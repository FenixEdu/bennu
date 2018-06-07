package org.fenixedu.bennu.scheduler.future;

import java.io.Serializable;
import java.util.concurrent.Callable;

@FunctionalInterface
public interface PersistentFutureTask extends Callable<Serializable>, Serializable {

}
