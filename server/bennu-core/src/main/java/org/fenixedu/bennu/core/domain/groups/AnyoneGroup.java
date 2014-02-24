/*
 * AnyoneGroup.java
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
package org.fenixedu.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Group that always returns <code>true</code> on membership tests.
 * 
 * @see Group
 */
public final class AnyoneGroup extends AnyoneGroup_Base {
    protected AnyoneGroup() {
        super();
        setRootForGroupConstant(getRoot());
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString("resources.BennuResources", "label.bennu.group.anyone");
    }

    @Override
    public String expression() {
        return "anyone";
    }

    @Override
    public Set<User> getMembers() {
        return Collections.unmodifiableSet(Bennu.getInstance().getUserSet());
    }

    @Override
    public boolean isMember(final User user) {
        return true;
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    @Override
    public Group and(Group group) {
        return group;
    }

    @Override
    public Group or(Group group) {
        return this;
    }

    @Override
    public Group minus(Group group) {
        if (group instanceof AnonymousGroup) {
            return LoggedGroup.getInstance();
        }
        if (group instanceof LoggedGroup) {
            return AnonymousGroup.getInstance();
        }
        return super.minus(group);
    }

    @Override
    public Group not() {
        return NobodyGroup.getInstance();
    }

    @Override
    protected boolean isGarbageCollectable() {
        // Singleton group, no point in delete
        return false;
    }

    /**
     * Get or create singleton instance of {@link AnyoneGroup}
     * 
     * @return singleton {@link AnyoneGroup} instance
     */
    public static AnyoneGroup getInstance() {
        AnyoneGroup instance = BennuGroupIndex.getAnyone();
        if (instance == null) {
            // reuse of unlinked instances of bennu 2.1 or less
            instance =
                    (AnyoneGroup) Iterables.tryFind(Bennu.getInstance().getGroupSet(), Predicates.instanceOf(AnyoneGroup.class))
                            .orNull();
            if (instance != null) {
                instance.setRootForGroupConstant(Bennu.getInstance());
            }
        }
        return instance != null ? instance : create();
    }

    @Atomic(mode = TxMode.WRITE)
    private static AnyoneGroup create() {
        AnyoneGroup instance = BennuGroupIndex.getAnyone();
        return instance != null ? instance : new AnyoneGroup();
    }
}
