/*
 * @(#)TransactionalCustomTask.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Scheduler Module.
 *
 *   The Scheduler Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Scheduler Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Scheduler Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.bennu.core.domain.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Luis Cruz
 * 
 */
public abstract class TransactionalCustomTask extends CustomTask {

    private final Logger logger = LoggerFactory.getLogger(TransactionalCustomTask.class.getName());

    @Override
    public void run() {

        logger.info("Task " + this.getClass().getSimpleName() + " started");
        try {
            runTask();
        } finally {
            logger.info("Task " + this.getClass().getSimpleName() + " finished");
        }
    }

    protected abstract void runTask();

}
