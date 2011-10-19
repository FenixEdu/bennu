/*
 * @(#)SchedulerThread.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: JoÃ£o Figueiredo, Luis Cruz, Paulo Abrantes, Susana
Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Web Application Infrastructure.
 *
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser
General 
 *   Public License as published by the Free Software Foundation, either
version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   Bennu is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
License
 *   along with Bennu. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.domain.scheduler;

import jvstm.TransactionalCommand;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;

public abstract class TransactionalThread extends Thread {

    private final boolean readOnly;

    public TransactionalThread(final boolean readOnly) {
	this.readOnly = readOnly;
    }

    public TransactionalThread() {
	this(false);
    }

    @Override
    public void run() {
	try {
	    Transaction.withTransaction(true, new TransactionalCommand() {
		@Override
		public void doIt() {
		    if (readOnly) {
			transactionalRun();
		    } else {
			callService();
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


