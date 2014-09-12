package org.fenixedu.bennu.signals;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.TransactionManager;

import com.google.common.eventbus.Subscribe;

@RunWith(JUnit4.class)
public class SignalsTest {

    @BeforeClass
    public static void initFF() {
        System.setProperty("fenixframework.appName", "fenix-framework-core-api");
        new FenixFrameworkListenerAttacher().contextInitialized(null);
    }

    @Test
    public void testInTransactionNotification() throws Exception {
        AtomicBoolean bool = new AtomicBoolean(false);
        Signal.registerWithTransaction("x", new Listener(bool));
        TransactionManager manager = FenixFramework.getTransactionManager();
        manager.begin();
        Signal.emit("x", new DomainObjectEvent<>(FenixFramework.getDomainRoot()));
        Assert.assertEquals(false, bool.get());
        manager.commit();
        Assert.assertEquals(true, bool.get());
    }

    @Test
    public void testOutsideTransactionNotification() throws Exception {
        AtomicBoolean bool = new AtomicBoolean(false);
        Signal.register("x", new Listener(bool));
        TransactionManager manager = FenixFramework.getTransactionManager();
        manager.begin();
        Signal.emit("x", new DomainObjectEvent<>(FenixFramework.getDomainRoot()));
        Assert.assertEquals(false, bool.get());
        manager.commit();
        Assert.assertEquals(true, bool.get());
    }

    public static class Listener {

        private final AtomicBoolean bool;

        public Listener(AtomicBoolean bool) {
            this.bool = bool;
        }

        @Subscribe
        public void handleEvent(DomainObjectEvent<DomainRoot> event) {
            Assert.assertTrue("Event is not for domain root!", event.getInstance() instanceof DomainRoot);
            Assert.assertTrue("Instances do not match!", event.getInstance() == FenixFramework.getDomainRoot());
            bool.set(true);
        }

    }

}
