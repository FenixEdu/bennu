/*
 * @(#)EmailTask.java
 *
 * Copyright 2012 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the E-mail SMTP Adapter Module.
 *
 *   The E-mail SMTP Adapter Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The E-mail Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the E-mail Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.emailNotifier.domain;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.Transaction;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class EmailTask extends EmailTask_Base {

    private static class SingleEmailDispatcher extends Thread {
	private final String oid;

	protected SingleEmailDispatcher(final Email email) {
	    this.oid = email.getExternalId();
	}

	@Override
	public void run() {
	    try {
		Transaction.withTransaction(false, new TransactionalCommand() {
		    public void doIt() {
			final Email email = AbstractDomainObject.fromExternalId(oid);
			email.deliver();
		    }
		});
	    } finally {
		Transaction.forceFinish();
	    }
	}
    }

    @Override
    public void runTask() {
	for (final Email email : EmailNotifier.getInstance().getEmailsSet()) {
	    final SingleEmailDispatcher emailDispatcher = new SingleEmailDispatcher(email);
	    emailDispatcher.start();
	    try {
		emailDispatcher.join();
	    } catch (final InterruptedException e) {
		throw new Error(e);
	    }
	}
    }

}
