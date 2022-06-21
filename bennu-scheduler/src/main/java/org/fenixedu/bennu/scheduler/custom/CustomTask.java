/*
 * @(#)CustomTask.java
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

package org.fenixedu.bennu.scheduler.custom;

import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.log.ExecutionLog;
import pt.ist.fenixframework.Atomic.TxMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public abstract class CustomTask extends CronTask {

    private static final Collection<Consumer<CustomTask>> HANDLERS = new ArrayList<>();

    public static void registerHandler(final Consumer<CustomTask> consumer) {
        HANDLERS.add(consumer);
    }

    private String code;
    private String user;

    public void init(String code, String user) {
        if (this.code != null || this.user != null) {
            throw new IllegalStateException("CustomTask already initialized!");
        }
        this.code = code;
        this.user = user;
    }

    @Override
    protected ExecutionLog createExecutionLog() {
        // this will blow up if either code or user are null
        try {
            return ExecutionLog.newCustomExecution(getClassName(), code, user);
        } finally {
            HANDLERS.forEach(h -> h.accept(this));
        }
    }

    @Override
    public TxMode getTxMode() {
        return TxMode.WRITE;
    }

    public String getSourceCode() {
        return code;
    }

    public String getTaskRunner() {
        return user;
    }

}
