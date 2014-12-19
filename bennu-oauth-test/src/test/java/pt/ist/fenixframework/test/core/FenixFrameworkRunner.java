package pt.ist.fenixframework.test.core;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class FenixFrameworkRunner extends BlockJUnit4ClassRunner {

    private static final Logger logger = LoggerFactory.getLogger(FenixFrameworkRunner.class);

    public FenixFrameworkRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    @Atomic(mode = TxMode.WRITE)
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        super.runChild(method, notifier);
    }

}
