/*
 * IntersectionGroup.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-core.
 *
 * bennu-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * Intersection composition group.
 * 
 * @see PersistentGroup
 */
public class IntersectionGroup extends IntersectionGroup_Base {
	protected IntersectionGroup(Set<PersistentGroup> children) {
		super();
		init(children);
	}

	@Override
	protected String operator() {
		return "&";
	}

	@Override
	public Set<User> getMembers() {
		final Set<User> users = new HashSet<>();
		Iterator<PersistentGroup> iterator = getChildrenSet().iterator();
		if (iterator.hasNext()) {
			users.addAll(iterator.next().getMembers());
			while (iterator.hasNext()) {
				users.retainAll(iterator.next().getMembers());
			}
		}
		return users;
	}

	@Override
	public boolean isMember(final User user) {
		if (getChildrenCount() == 0) {
			return false;
		}
		for (final PersistentGroup persistentGroup : getChildrenSet()) {
			if (!persistentGroup.isMember(user)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Set<User> getMembers(DateTime when) {
		final Set<User> users = new HashSet<>();
		Iterator<PersistentGroup> iterator = getChildrenSet().iterator();
		if (iterator.hasNext()) {
			users.addAll(iterator.next().getMembers(when));
			while (iterator.hasNext()) {
				users.retainAll(iterator.next().getMembers(when));
			}
		}
		return users;
	}

	@Override
	public boolean isMember(User user, DateTime when) {
		if (getChildrenCount() == 0) {
			return false;
		}
		for (final PersistentGroup persistentGroup : getChildrenSet()) {
			if (!persistentGroup.isMember(user, when)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public PersistentGroup and(PersistentGroup group) {
		Set<PersistentGroup> children = new HashSet<>(getChildrenSet());
		children.add(group);
		return IntersectionGroup.getInstance(children);
	}

	/**
	 * @see #getInstance(Set)
	 */
	@Service
	public static IntersectionGroup getInstance(final PersistentGroup... children) {
		return getInstance(new HashSet<>(Arrays.asList(children)));
	}

	/**
	 * Get or create instance of a {@link IntersectionGroup} between the requested children.
	 * 
	 * @param children the groups to make a {@link IntersectionGroup} on.
	 * @return singleton {@link IntersectionGroup} instance
	 */
	@Service
	public static IntersectionGroup getInstance(final Set<PersistentGroup> children) {
		IntersectionGroup group = select(IntersectionGroup.class, new Predicate<IntersectionGroup>() {
			@Override
			public boolean apply(@Nullable IntersectionGroup input) {
				return Sets.symmetricDifference(input.getChildrenSet(), children).isEmpty();
			}
		});
		return group != null ? group : new IntersectionGroup(children);
	}
}
