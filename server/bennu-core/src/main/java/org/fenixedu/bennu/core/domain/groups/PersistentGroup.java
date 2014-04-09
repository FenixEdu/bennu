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

import java.util.Set;

import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.groups.AnonymousGroup;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.LoggedGroup;
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

import com.google.common.base.Function;

/**
 * <p>
 * {@code Group}s represent access groups. These groups are domain entities but immutable and unique in semantics (with the sole
 * exception of {@link PersistentDynamicGroup}). That means that there is only one instance of a groups representing authenticated users, or
 * ('john' & 'mary').
 * </p>
 * 
 * <p>
 * Groups can be translated to and from a DSL of groups, using methods {@link #expression()} and {@link #parse(String)},
 * respectively. The language supports compositions (|), intersections (&), negations (!) and differences (-) over basic
 * constructs like: anonymous ({@link AnonymousGroup}), logged ({@link LoggedGroup}), anyone ({@link AnyoneGroup}), nobody (
 * {@link NobodyGroup}), P(istxxx, istxxxx,...) ({@link PersistentUserGroup}) or #name ({@link PersistentDynamicGroup}).
 * </p>
 * 
 * <p>
 * Subclasses should not have public constructors, instead they should provide {@code getInstance(...)} methods that ensure non
 * duplication of groups with the same semantics. Subclasses should also ensure immutability, all operators return new instances
 * of groups instead of changing the current one.
 * </p>
 * 
 * @see PersistentDynamicGroup
 * @see GroupOperator
 */
public abstract class PersistentGroup extends PersistentGroup_Base {
    public static final Function<PersistentGroup, Group> persistentGroupToGroup = new Function<PersistentGroup, Group>() {
        @Override
        public Group apply(PersistentGroup group) {
            return group.toGroup();
        }
    };

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
    public final Set<User> getMembers() {
        return toGroup().getMembers();
    }

    /**
     * Same as {@link #getMembers()} but at a given moment in time. This is like a time-machine for the groups domain.
     * 
     * @param when
     *            moment when to fetch the user list.
     * @return all member users in the system at the requested moment
     */
    public final Set<User> getMembers(DateTime when) {
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
    public final boolean isMember(User user) {
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
    public final boolean isMember(User user, DateTime when) {
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

    /**
     * Should return true is the instance can be discarded without damaging the system. As a general rule, if this is not in use
     * and can be recovered from the language, it can be discarded. Acceptable exceptions to this rule are singleton groups, since
     * they do not save up that many space and are too often used.
     * 
     * @return {@code true} if can be discarded, {@code false} otherwise
     */
    protected boolean isGarbageCollectable() {
        return getNegation() == null && getUnionsSet().isEmpty() && getIntersectionsSet().isEmpty()
                && getDifferenceAtFirstSet().isEmpty() && getDifferenceAtRestSet().isEmpty() && getDynamicGroupSet().isEmpty();
    }

    /**
     * Delete the object from the system. Assume true in {@link #isGarbageCollectable()} since it is tested before invoking this.
     */
    protected void gc() {
        setRoot(null);
        deleteDomainObject();
    }

    public static void garbageCollect() {
        for (PersistentGroup group : Bennu.getInstance().getGroupSet()) {
            if (group.isGarbageCollectable()) {
                group.gc();
            }
        }
    }
}
