/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of Bennu Signals.
 *
 * Bennu Signals is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Signals is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Signals.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.signals;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
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
    public static void init() {
        System.setProperty("fenixframework.appName", "fenix-framework-core-api");
        Signal.init();
    }

    @Before
    public void cleanup() {
        Signal.clear("x");
    }

    @Test
    public void testInTransactionNotification() throws Exception {
        AtomicBoolean bool = new AtomicBoolean(false);
        Signal.register("x", new Listener(bool));
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
        Signal.registerWithoutTransaction("x", new Listener(bool));
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

    @Test
    public void testLambdaSignals() throws Exception {
        AtomicBoolean bool = new AtomicBoolean(false);
        Signal.register("x", (DomainObjectEvent<DomainRoot> event) -> {
            Assert.assertTrue("Event is not for domain root!", event.getInstance() instanceof DomainRoot);
            Assert.assertTrue("Instances do not match!", event.getInstance() == FenixFramework.getDomainRoot());
            bool.set(true);
        });
        TransactionManager manager = FenixFramework.getTransactionManager();
        manager.begin();
        Assert.assertEquals(false, bool.get());
        Signal.emit("x", new DomainObjectEvent<>(FenixFramework.getDomainRoot()));
        Assert.assertEquals(false, bool.get());
        manager.commit();
        Assert.assertEquals(true, bool.get());
    }

    @Test
    public void testExceptionHandling() {
        AtomicBoolean bool = new AtomicBoolean(false);
        Signal.register("x", (event) -> {
            throw new NullPointerException();
        });

        boolean caught = false;
        try {
            TransactionManager manager = FenixFramework.getTransactionManager();
            manager.begin();
            Assert.assertEquals(false, bool.get());
            Signal.emit("x", new DomainObjectEvent<>(FenixFramework.getDomainRoot()));
            Assert.assertEquals(false, bool.get());
            manager.commit();
        } catch (Throwable e) {
            caught = true;
            Assert.assertTrue("Wrong Exception", e.getCause() instanceof RuntimeException);
            Assert.assertTrue("Wrong Exception", e.getMessage().equals("Exceptions while emiting signals"));
            Assert.assertTrue("Incorrect Exception emited", e.getCause().getCause() instanceof NullPointerException);
        }
        Assert.assertTrue("Exception not thrown", caught);
    }

    @Test
    public void testMultipleExceptionHandling() {
        AtomicBoolean bool = new AtomicBoolean(false);
        Signal.register("x", (event) -> {
            throw new NullPointerException();
        });
        Signal.register("x", (event) -> {
            throw new UnsupportedOperationException();
        });

        boolean caught = false;
        try {
            TransactionManager manager = FenixFramework.getTransactionManager();
            manager.begin();
            Assert.assertEquals(false, bool.get());
            Signal.emit("x", new DomainObjectEvent<>(FenixFramework.getDomainRoot()));
            Assert.assertEquals(false, bool.get());
            manager.commit();
        } catch (Throwable e) {
            caught = true;
            Assert.assertTrue("Wrong Exception", e.getCause() instanceof RuntimeException);
            Assert.assertTrue("Wrong Exception", e.getMessage().equals("Exceptions while emiting signals"));
            Assert.assertTrue("Incorrect Exception emited", e.getCause().getCause() instanceof NullPointerException
                    || e.getCause().getCause() instanceof UnsupportedOperationException);
        }
        Assert.assertTrue("Exception not thrown", caught);
    }

    @Test
    public void testSilentExceptionHandling() {
        AtomicBoolean bool = new AtomicBoolean(false);
        Signal.registerWithoutTransaction("x", (event) -> {
            throw new NullPointerException();
        });

        try {
            TransactionManager manager = FenixFramework.getTransactionManager();
            manager.begin();
            Assert.assertEquals(false, bool.get());
            Signal.emit("x", new DomainObjectEvent<>(FenixFramework.getDomainRoot()));
            Assert.assertEquals(false, bool.get());
            manager.commit();
        } catch (Throwable e) {
            Assert.fail("Thrown exception");
            e.printStackTrace();
        }
    }

    @Test
    public void testMultipleSilentExceptionHandling() {
        AtomicBoolean bool = new AtomicBoolean(false);
        Signal.registerWithoutTransaction("x", (event) -> {
            throw new NullPointerException();
        });
        Signal.registerWithoutTransaction("x", (event) -> {
            throw new UnsupportedOperationException();
        });

        try {
            TransactionManager manager = FenixFramework.getTransactionManager();
            manager.begin();
            Assert.assertEquals(false, bool.get());
            Signal.emit("x", new DomainObjectEvent<>(FenixFramework.getDomainRoot()));
            Assert.assertEquals(false, bool.get());
            manager.commit();
        } catch (Throwable e) {
            Assert.fail("Thrown exception");
            e.printStackTrace();
        }
    }

    @Test
    public void testSignalEmitingWithinSignal() throws Exception {
        AtomicBoolean bool = new AtomicBoolean(false);
        Signal.register("signal1", (DomainObjectEvent<DomainRoot> event) -> {
            Signal.emit("signal2", event);
        });
        Signal.register("signal2", (DomainObjectEvent<DomainRoot> event) -> {
            bool.set(true);
        });

        TransactionManager manager = FenixFramework.getTransactionManager();
        manager.begin();
        Signal.emit("signal1", new DomainObjectEvent<>(FenixFramework.getDomainRoot()));
        manager.commit();
        Assert.assertTrue("Second signal wasn't emited", bool.get());
    }

    @Test
    public void testSignalEmitingWithinSignalWithoutTransaction() throws Exception {
        AtomicBoolean bool = new AtomicBoolean(false);
        Signal.registerWithoutTransaction("signal1", (DomainObjectEvent<DomainRoot> event) -> {
            Signal.emit("signal2", event);
        });
        Signal.registerWithoutTransaction("signal2", (DomainObjectEvent<DomainRoot> event) -> {
            bool.set(true);
        });

        TransactionManager manager = FenixFramework.getTransactionManager();
        manager.begin();
        Signal.emit("signal1", new DomainObjectEvent<>(FenixFramework.getDomainRoot()));
        manager.commit();
        Assert.assertTrue("Second signal wasn't emited", bool.get());
    }
}
