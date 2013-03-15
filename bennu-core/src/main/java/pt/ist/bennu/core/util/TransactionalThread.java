/*
 * TransactionalThread.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.util;

import java.util.Locale;

import jvstm.TransactionalCommand;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.i18n.I18N;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.core.security.UserSession;
import pt.ist.fenixframework.pstm.Transaction;

/**
 * 
 * @author Pedro Santos
 * @author Luis Cruz
 * 
 */
public abstract class TransactionalThread extends Thread {
    private VirtualHost virtualHost;

    private UserSession user;

    private Locale locale;

    private final boolean readOnly;

    public TransactionalThread(boolean readOnly) {
        virtualHost = VirtualHost.getVirtualHostForThread();
        user = Authenticate.getUserSession();
        locale = I18N.getLocale();
        this.readOnly = readOnly;
    }

    public TransactionalThread() {
        this(false);
    }

    @Override
    public void run() {
        VirtualHost.setVirtualHostForThread(virtualHost);
        Authenticate.setUser(user);
        I18N.setLocale(locale);
        Transaction.withTransaction(readOnly, new TransactionalCommand() {
            @Override
            public void doIt() {
                transactionalRun();
            }
        });
    }

    public abstract void transactionalRun();
}
