/*
 * PersistentGroup.java
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
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.antlr.runtime.RecognitionException;
import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.grouplanguage.GroupExpressionParser;
import pt.ist.bennu.core.security.UserView;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * <p>
 * {@code PersistentGroup}s represent access groups. These groups are domain entities but immutable and unique in semantics (with
 * the sole exception of {@link DynamicGroup}). That means that there is only one instance of a groups representing authenticated
 * users, or ('john' & 'mary').
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
public abstract class PersistentGroup extends PersistentGroup_Base {
	protected PersistentGroup() {
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
		if (!isMember(UserView.getUser())) {
			throw AuthorizationException.unauthorized(this, UserView.getUser());
		}
	}

	/**
	 * Intersect with given group. Returns the resulting group without changing {@code this} or the argument.
	 * 
	 * @param group
	 *            group to intersect with
	 * @return group resulting of the intersection between '{@code this}' and '{@code group}'
	 */
	public PersistentGroup and(PersistentGroup group) {
		Set<PersistentGroup> children = new HashSet<>();
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
	public PersistentGroup or(PersistentGroup group) {
		Set<PersistentGroup> children = new HashSet<>();
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
	public PersistentGroup minus(PersistentGroup group) {
		Set<PersistentGroup> children = new HashSet<>();
		children.add(this);
		children.add(group);
		return DifferenceGroup.getInstance(children);
	}

	/**
	 * Negate the group. Returns the resulting group without changing {@code this}.
	 * 
	 * @return inverse group
	 */
	public PersistentGroup not() {
		return NegationGroup.getInstance(this);
	}

	/**
	 * Grants access to the given user. Returns the resulting group without changing {@code this}.
	 * 
	 * @param user
	 *            user to grant access to
	 * @return group resulting of the union between '{@code this}' and the group of the given user
	 */
	public PersistentGroup grant(User user) {
		return UnionGroup.getInstance(this, UserGroup.getInstance(user));
	}

	/**
	 * Revokes access to the given user. Returns the resulting group without changing {@code this}.
	 * 
	 * @param user
	 *            user to revoke access from
	 * @return group resulting of the difference between '{@code this}' and the group of the given user
	 */
	public PersistentGroup revoke(User user) {
		return DifferenceGroup.getInstance(this, UserGroup.getInstance(user));
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
	public static PersistentGroup parse(String expression) {
		try {
			return GroupExpressionParser.parse(expression);
		} catch (RecognitionException | IOException | GroupException e) {
			throw GroupException.groupParsingError(e);
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
	protected static <T extends PersistentGroup> T select(final @Nonnull Class<? extends T> type) {
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
	protected static <T extends PersistentGroup> T select(final @Nonnull Class<? extends T> type,
			final @Nonnull Predicate<? super T> predicate) {
		@SuppressWarnings("unchecked")
		Predicate<? super PersistentGroup> realPredicate = Predicates.and(Predicates.instanceOf(type),
				(Predicate<? super PersistentGroup>) predicate);
		return (T) Iterables.tryFind(VirtualHost.getVirtualHostForThread().getGroupsSet(), realPredicate).orNull();
	}
}
