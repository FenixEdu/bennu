/*
 * Group.java
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

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.dml.runtime.Relation;

/**
 * <p>
 * {@code PersistentGroup}s represent access groups. These groups are domain entities but immutable and unique in semantics (with
 * the sole exception of {@link PersistentDynamicGroup}). That means that there is only one instance of a group representing
 * authenticated users, or only one instance of a group for a specific set of users
 * </p>
 * 
 * <p>
 * This is the persistent counter part of {@link Group}.
 * </p>
 * 
 * <p>
 * Subclasses should not have public constructors, instead they should provide {@code getInstance(...)} methods that ensure non
 * duplication of groups with the same semantics. Subclasses should also ensure immutability, all operators return new instances
 * of groups instead of changing the current one.
 * </p>
 * 
 * <p>
 * <b>Note:</b> Whereas in most situations users will not deal with {@link PersistentGroup} subclasses directly, if possible, the
 * core methods ( {@link #isMember(User)}, {@link #getMembers()}) should be invoked directly, as subclasses may have a more
 * performant way of computing the proper values.
 * </p>
 * 
 * @see PersistentDynamicGroup
 * @see GroupOperator
 */
public abstract class PersistentGroup extends PersistentGroup_Base {

    protected PersistentGroup() {
        super();
        setRoot(Bennu.getInstance());
    }

    public abstract Group toGroup();

    /**
     * Human readable, internationalized textual representation of this group.
     * 
     * @return internationalized name of the group.
     */
    public final String getPresentationName() {
        return toGroup().getPresentationName();
    }

    /**
     * Textual representation of this group in the group language.
     * 
     * @return this group in group language.
     */
    public final String expression() {
        return toGroup().getExpression();
    }

    /**
     * Computes the full member list of this group. Potentially processor consuming operation, preferably developers should orient
     * code to {@link #isMember(User)} or {@link #isMember(User, DateTime)} methods.
     * 
     * @return all member users in the system at the exact moment of the invocation
     */
    public Set<User> getMembers() {
        return toGroup().getMembers();
    }

    /**
     * Same as {@link #getMembers()} but at a given moment in time. This is like a time-machine for the groups domain.
     * 
     * @param when
     *            moment when to fetch the user list.
     * @return all member users in the system at the requested moment
     */
    public Set<User> getMembers(DateTime when) {
        return toGroup().getMembers(when);
    }

    /**
     * Tests if the given user is a member of the group.
     * 
     * @param user
     *            the user to test
     * @return <code>true</code> if member, <code>false</code> otherwise
     * 
     * @see #verify()
     */
    public boolean isMember(User user) {
        return toGroup().isMember(user);
    }

    /**
     * Same as {@link #isMember(User)} but at a given moment in time. This is like a time-machine for the groups domain.
     * 
     * @param user
     *            the user to test
     * @param when
     *            moment when to test the user.
     * @return <code>true</code> if member, <code>false</code> otherwise
     */
    public boolean isMember(User user, DateTime when) {
        return toGroup().isMember(user, when);
    }

    /**
     * Tests if the given user is a member of the group, throwing an exception if not.
     * 
     * @throws AuthorizationException
     *             if user is not a member of the group.
     */
    public final void verify() throws AuthorizationException {
        if (!isMember(Authenticate.getUser())) {
            throw AuthorizationException.unauthorized();
        }
    }

    protected Collection<Relation<?, ?>> getContextRelations() {
        return Collections.emptySet();
    }

    /**
     * Delete the object from the system if not in use. Works by disconnecting from the context relations.
     */
    protected void gc() {
        if (GroupGC.emptyCustomRelations(this)) {
            setRoot(null);
            GroupGC.cleanContext(this);
            deleteDomainObject();
        }
    }

    public static void garbageCollect() {
        GroupGC.gc();
    }

    public static void garbageCollect(PersistentGroup group) {
        group.gc();
    }

    protected static <T extends PersistentGroup> T singleton(Supplier<Optional<T>> selector, Supplier<T> creator) {
        return selector.get().orElseGet(() -> create(selector, creator));
    }

    @Atomic(mode = TxMode.WRITE)
    private static <T extends PersistentGroup> T create(Supplier<Optional<T>> selector, Supplier<T> creator) {
        return selector.get().orElseGet(creator);
    }
}
