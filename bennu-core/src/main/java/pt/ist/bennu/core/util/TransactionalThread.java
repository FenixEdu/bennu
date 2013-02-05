/*
 * TransactionalThread.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-core.
 *
 * bennu-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.util;

import java.util.ArrayList;

import jvstm.TransactionalCommand;
import pt.ist.bennu.service.Service;
import pt.ist.fenixframework.pstm.Transaction;

/**
 * 
 * @author Pedro Santos
 * @author Luis Cruz
 * 
 */
public abstract class TransactionalThread extends Thread {
    public static interface ExceptionListener {
        public void notifyException(Throwable e);
    }

    private final boolean readOnly;

    private ArrayList<ExceptionListener> listeners;

    public TransactionalThread(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    public TransactionalThread() {
        this(false);
    }

    public void addExceptionListener(ExceptionListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void removeExceptionListener(ExceptionListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void run() {
        try {
            Transaction.withTransaction(true, new TransactionalCommand() {
                @Override
                public void doIt() {
                    try {
                        if (readOnly) {
                            transactionalRun();
                        } else {
                            callService();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        if (listeners != null) {
                            for (ExceptionListener listener : listeners) {
                                listener.notifyException(e);
                            }
                        }
                    }
                }
            });
        } finally {
            Transaction.forceFinish();
        }
    }

    public abstract void transactionalRun();

    @Service
    private void callService() {
        transactionalRun();
    }

}
