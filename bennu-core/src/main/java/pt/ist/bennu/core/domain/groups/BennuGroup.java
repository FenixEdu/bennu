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
package pt.ist.bennu.core.domain.groups;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.RecognitionException;
import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.exceptions.AuthorizationException;
import pt.ist.bennu.core.domain.exceptions.BennuCoreDomainException;
import pt.ist.bennu.core.grouplanguage.GroupExpressionParser;
import pt.ist.bennu.core.security.Authenticate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * <p>
 * {@code Group}s represent access groups. These groups are domain entities but immutable and unique in semantics (with the sole
 * exception of {@link DynamicGroup}). That means that there is only one instance of a groups representing authenticated users, or
 * ('john' & 'mary').
 * </p>
 * 
 * <p>
 * Groups can be translated to and from a DSL of groups, using methods {@link #expression()} and {@link #parse(String)},
 * respectively. The language supports compositions (|), intersections (&), negations (!) and differences (-) over basic
 * constructs like: anonymous ({@link AnonymousGroup}), logged ({@link LoggedGroup}), anyone ({@link AnyoneGroup}), nobody (
 * {@link NobodyGroup}), P(istxxx, istxxxx,...) ({@link UserGroup}) or #name ({@link DynamicGroup}).
 * </p>
 * 
 * <p>
 * Subclasses should not have public constructors, instead they should provide {@code getInstance(...)} methods that ensure non
 * duplication of groups with the same semantics. Subclasses should also ensure immutability, all operators return new instances
 * of groups instead of changing the current one.
 * </p>
 * 
 * @see DynamicGroup
 * @see CustomGroup
 */
public abstract class BennuGroup extends BennuGroup_Base {
    protected BennuGroup() {
        super();
        setHost(VirtualHost.getVirtualHostForThread());
    }

    /**
     * Human readable, internalionalized textual representation of this group.
     * 
     * @return internationalized name of the group.
     */
    public abstract String getPresentationName();

    /**
     * Textual representation of this group in the group language.
     * 
     * @return this group in group language.
     */
    public abstract String expression();

    /**
     * Computes the full member list of this group. Potentially processor consuming operation, preferably developers should orient
     * code to {@link #isMember(User)} or {@link #isMember(User, DateTime)} methods.
     * 
     * @return all member users in the system at the exact moment of the invocation
     */
    public abstract Set<User> getMembers();

    /**
     * Same as {@link #getMembers()} but at a given moment in time. This is like a time-machine for the groups domain.
     * 
     * @param when
     *            moment when to fetch the user list.
     * @return all member users in the system at the requested moment
     */
    public abstract Set<User> getMembers(DateTime when);

    /**
     * Tests if the given user is a member of the group.
     * 
     * @param user
     *            the user to test
     * @return <code>true</code> if member, <code>false</code> otherwise
     * 
     * @see #verify()
     */
    public abstract boolean isMember(User user);

    /**
     * Same as {@link #isMember(User)} but at a given moment in time. This is like a time-machine for the groups domain.
     * 
     * @param user
     *            the user to test
     * @param when
     *            moment when to test the user.
     * @return <code>true</code> if member, <code>false</code> otherwise
     */
    public abstract boolean isMember(User user, DateTime when);

    /**
     * Tests if the given user is a member of the group, throwing an exception if not.
     * 
     * @throws AuthorizationException
     *             if user is not a member of the group.
     */
    public void verify() throws AuthorizationException {
        if (!isMember(Authenticate.getUser())) {
            throw AuthorizationException.unauthorized();
        }
    }

    /**
     * Intersect with given group. Returns the resulting group without changing {@code this} or the argument.
     * 
     * @param group group to intersect with
     * @return group resulting of the intersection between '{@code this}' and '{@code group}'
     */
    public BennuGroup and(BennuGroup group) {
        Set<BennuGroup> children = new HashSet<>();
        children.add(this);
        children.add(group);
        return IntersectionGroup.getInstance(children);
    }

    /**
     * Unite with given group. Returns the resulting group without changing {@code this} or the argument.
     * 
     * @param group
     *            group to unite with
     * @return group resulting of the union between '{@code this}' and '{@code group}'
     */
    public BennuGroup or(BennuGroup group) {
        Set<BennuGroup> children = new HashSet<>();
        children.add(this);
        children.add(group);
        return UnionGroup.getInstance(children);
    }

    /**
     * Subtract with given group. Returns the resulting group without changing {@code this} or the argument.
     * 
     * @param group
     *            group to subtract with
     * @return group resulting of all members of '{@code this}' except members of '{@code group}'
     */
    public BennuGroup minus(BennuGroup group) {
        Set<BennuGroup> children = new HashSet<>();
        children.add(this);
        children.add(group);
        return DifferenceGroup.getInstance(children);
    }

    /**
     * Negate the group. Returns the resulting group without changing {@code this}.
     * 
     * @return inverse group
     */
    public BennuGroup not() {
        return NegationGroup.getInstance(this);
    }

    /**
     * Grants access to the given user. Returns the resulting group without changing {@code this}.
     * 
     * @param user
     *            user to grant access to
     * @return group resulting of the union between '{@code this}' and the group of the given user
     */
    public BennuGroup grant(User user) {
        return or(UserGroup.getInstance(user));
    }

    /**
     * Revokes access to the given user. Returns the resulting group without changing {@code this}.
     * 
     * @param user
     *            user to revoke access from
     * @return group resulting of the difference between '{@code this}' and the group of the given user
     */
    public BennuGroup revoke(User user) {
        return minus(UserGroup.getInstance(user));
    }

    /**
     * Should return true is the instance can be discarded without damaging the system. As a general rule, if this is not in use
     * and can be recovered from the language, it can be discarded. Acceptable exceptions to this rule are singleton groups, since
     * they do not save up that many space and are too often used.
     * 
     * @return {@code true} if can be discarded, {@code false} otherwise
     */
    protected boolean isGarbageCollectable() {
        return getNegation() == null && getCompositionsSet().isEmpty() && getDynamicGroupSet().isEmpty();
    }

    /**
     * Delete the object from the system. Assume true in {@link #isGarbageCollectable()} since it is tested before invoking this.
     */
    protected void gc() {
        setHost(null);
        deleteDomainObject();
    }

    /**
     * Parse group from the group language expression.
     * 
     * @param expression
     *            the group in textual form
     * @return group representing the semantics of the expression.
     * @throws GroupException
     *             if a parsing error occurs
     */
    public static BennuGroup parse(String expression) {
        try {
            return GroupExpressionParser.parse(expression);
        } catch (RecognitionException | IOException e) {
            throw BennuCoreDomainException.groupParsingError(e);
        }
    }

    public static Set<BennuGroup> userAccessibleGroups(User user) {
        Set<BennuGroup> groups = new HashSet<>();
        Set<BennuGroup> ignored = new HashSet<>();
        processAccessibleGroups(groups, ignored, AnyoneGroup.getInstance(), user);
        processAccessibleGroups(groups, ignored, LoggedGroup.getInstance(), user);
        for (UserGroup group : user.usersGroups()) {
            processAccessibleGroups(groups, ignored, group, user);
        }
        for (BennuGroup group : CustomGroup.groupsForUser(user)) {
            processAccessibleGroups(groups, ignored, group, user);
        }
        for (NegationGroup group : VirtualHost.getVirtualHostForThread().getNegationSet()) {
            if (group.isMember(user)) {
                processAccessibleGroups(groups, ignored, group, user);
            }
        }
        return groups;
    }

    // These we are not interested to see listed as accessible groups, either because they are obvious, or because they are meaningless
    // unless used in some context.
    private static Set<Class<? extends BennuGroup>> IGNORES = new HashSet<>(Arrays.asList(AnonymousGroup.class,
            AnyoneGroup.class, NobodyGroup.class, LoggedGroup.class, DifferenceGroup.class, IntersectionGroup.class,
            NegationGroup.class, UnionGroup.class));

    private static void processAccessibleGroups(Set<BennuGroup> groups, Set<BennuGroup> ignored, BennuGroup group, User user) {
        if (!groups.contains(group) && !ignored.contains(group)) {
            if (IGNORES.contains(group.getClass())) {
                ignored.add(group);
            } else {
                groups.add(group);
            }
            for (DynamicGroup dynamic : group.getDynamicGroupSet()) {
                processAccessibleGroups(groups, ignored, dynamic, user);
            }
            for (CompositionGroup composition : group.getCompositionsSet()) {
                if (composition instanceof UnionGroup || composition.isMember(user)) {
                    processAccessibleGroups(groups, ignored, composition, user);
                }
            }
        }
    }

    public static void garbageCollect() {
        VirtualHost host = VirtualHost.getVirtualHostForThread();
        for (BennuGroup group : host.getGroupsSet()) {
            if (group.isGarbageCollectable()) {
                group.gc();
            }
        }
    }

    /**
     * Selects the (known to be singleton) group of a given type. (Intended to use in implementations of {@code getInstance(...)}
     * methods).
     * 
     * @param type
     *            the wanted type
     * @return group instance.
     */
    protected static <T extends BennuGroup> T select(final Class<? extends T> type) {
        return (T) Iterables.tryFind(VirtualHost.getVirtualHostForThread().getGroupsSet(), Predicates.instanceOf(type)).orNull();
    }

    /**
     * Selects the group of a given type that matches the predicate. (Intended to use in implementations of
     * {@code getInstance(...)} methods).
     * 
     * @param type
     *            the wanted type
     * @param predicate
     *            the predicate to apply to group instances
     * @return group instance.
     */
    protected static <T extends BennuGroup> T select(final Class<? extends T> type, final Predicate<? super T> predicate) {
        @SuppressWarnings("unchecked")
        Predicate<? super BennuGroup> realPredicate =
                Predicates.and(Predicates.instanceOf(type), (Predicate<? super BennuGroup>) predicate);
        return (T) Iterables.tryFind(VirtualHost.getVirtualHostForThread().getGroupsSet(), realPredicate).orNull();
    }
}
