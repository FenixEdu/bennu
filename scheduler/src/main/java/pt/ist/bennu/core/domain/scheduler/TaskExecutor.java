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

package pt.ist.bennu.core.domain.scheduler;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.utl.ist.fenix.tools.util.i18n.Language;

public class TaskExecutor extends TransactionalThread {

    private final String taskId;

    private boolean successful = false;

    public boolean isSuccessful() {
	return successful;
    }

    public TaskExecutor(final Task task) {
	taskId = task.getExternalId();
    }

    @Override
    public void transactionalRun() {
	try {
	    // TODO : This needs to be placed in the apps task configuration
	    //        and used whenever the app is launchede.
	    VirtualHost.setVirtualHostForThread("dot.ist.utl.pt");
	    Language.setLocale(Language.getDefaultLocale());
	    final Task task = AbstractDomainObject.fromExternalId(taskId);
	    task.executeTask();
	    successful = true;
	} finally {
	    VirtualHost.releaseVirtualHostFromThread();
	}
    }

}
