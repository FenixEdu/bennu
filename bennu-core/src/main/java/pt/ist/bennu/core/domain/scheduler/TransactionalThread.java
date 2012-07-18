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
package pt.ist.bennu.core.domain.scheduler;

import java.util.ArrayList;

import jvstm.TransactionalCommand;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;

/**
 * 
 * @author  Pedro Santos
 * @author  Luis Cruz
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
	    listeners = new ArrayList<ExceptionListener>();
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
