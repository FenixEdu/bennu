/*
 * Bennu.java
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
package org.fenixedu.bennu.core.domain;

import java.util.Set;

import org.fenixedu.bennu.core.domain.groups.Group;
import org.fenixedu.bennu.core.domain.groups.NegationGroup;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

/**
 * Root class of the domain.
 */
public final class Bennu extends Bennu_Base {
    private Bennu() {
        super();
        setRoot(FenixFramework.getDomainRoot());
    }

    public static Bennu getInstance() {
        if (FenixFramework.getDomainRoot().getBennu() == null) {
            return initialize();
        }
        return FenixFramework.getDomainRoot().getBennu();
    }

    @Atomic(mode = TxMode.WRITE)
    private static Bennu initialize() {
        if (FenixFramework.getDomainRoot().getBennu() == null) {
            return new Bennu();
        }
        return FenixFramework.getDomainRoot().getBennu();
    }

    @Override
    public Set<User> getUserSet() {
        //FIXME: remove when the framework enables read-only slots
        return super.getUserSet();
    }

    @Override
    public Set<Group> getGroupSet() {
        //FIXME: remove when the framework enables read-only slots
        return super.getGroupSet();
    }

    @Override
    public Set<NegationGroup> getNegationSet() {
        //FIXME: remove when the framework enables read-only slots
        return super.getNegationSet();
    }
}
