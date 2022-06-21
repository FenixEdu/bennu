/* 
* @(#)TransactionalThread.java 
* 
* Copyright 2010 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package org.fenixedu.bennu.core.util;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 
 * @author Pedro Santos
 * @author Luis Cruz
 * 
 */
public abstract class TransactionalThread extends Thread {

    private final boolean readOnly;

    private Collection<Predicate<Throwable>> listeners;

    public TransactionalThread(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    public TransactionalThread() {
        this(false);
    }

    public void addExceptionListener(final Predicate<Throwable> listener) {
        if (listener == null) {
            throw new NullPointerException("Trying to add null exception listener");
        }
        synchronized (this) {
            if (listeners == null) {
                listeners = Collections.synchronizedCollection(new ArrayList<>());
            }
        }
        listeners.add(listener);
    }

    public void removeExceptionListener(final Consumer<Throwable> listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @Override
    @Atomic(mode = TxMode.READ)
    public void run() {
        try {
            if (readOnly) {
                transactionalRun();
            } else {
                callService();
            }
        } catch (final Throwable t) {
            if (listeners == null || listeners.stream().map(listener -> listener.test(t))
                    .reduce(false, (a, b) -> a || b)) {
                throw new Error(t);
            }
        }
    }

    public abstract void transactionalRun();

    @Atomic
    private void callService() {
        transactionalRun();
    }

    public static void runTx(final boolean readOnly, final Runnable runnable) {
        final Thread t = new TransactionalThread(readOnly) {
            @Override
            public void transactionalRun() {
                runnable.run();
            }
        };
        t.start();
        try {
            t.join();
        } catch (final InterruptedException ex) {
            throw new Error(ex);
        }
    }

}
