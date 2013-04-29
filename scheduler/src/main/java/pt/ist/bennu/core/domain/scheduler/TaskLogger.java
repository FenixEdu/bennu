/*
 * @(#)TaskLogger.java
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

import pt.ist.fenixframework.FenixFramework;

public class TaskLogger {

    static private final int MAX_LOG_ENTRIES = 100;
    private final String taskId;
    private final Boolean successful;

    public TaskLogger(final Task task) {
        taskId = task.getExternalId();
        this.successful = null;
    }

    public TaskLogger(final Task task, final boolean successful) {
        taskId = task.getExternalId();
        this.successful = Boolean.valueOf(successful);
    }

    public void run() {
        final Task task = FenixFramework.getDomainObject(taskId);
        if (successful == null) {
            task.createNewLog();
        } else {
            task.updateLastLog(successful);
            task.cleanupLogs(MAX_LOG_ENTRIES);
        }
    }
}
