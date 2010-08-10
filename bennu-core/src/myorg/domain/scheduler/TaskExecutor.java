/*
 * @(#)TaskExecutor.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Web Application Infrastructure.
 *
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
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

package myorg.domain.scheduler;

import pt.ist.fenixWebFramework.services.ServiceManager;
import pt.ist.fenixWebFramework.services.ServicePredicate;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.utl.ist.fenix.tools.util.i18n.Language;

public class TaskExecutor extends Thread {

    private final String taskId;

    private boolean successful;

    public boolean isSuccessful() {
	return successful;
    }

    private void setSuccessful(boolean successful) {
	this.successful = successful;
    }

    public TaskExecutor(final Task task) {
	taskId = task.getExternalId();
	setSuccessful(false);
    }

    @Override
    public void run() {
	super.run();
	Language.setLocale(Language.getDefaultLocale());
	ServiceManager.execute(new ServicePredicate() {

	    @Override
	    public void execute() {
		Task task = AbstractDomainObject.fromExternalId(taskId);
		try {
		    task.executeTask();
		    setSuccessful(true);
		} catch (Exception ex) {
		    throw new RuntimeException(ex);
		}
	    }

	});
    }

}
