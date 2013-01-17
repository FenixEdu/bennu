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
import com.google.common.collect.Iterables;

public class DifferenceGroup extends DifferenceGroup_Base {
	protected DifferenceGroup(Set<PersistentGroup> children) {
		super();
		init(children);
	}

	@Override
	protected String operator() {
		return "-";
	}

	@Override
	public Set<User> getMembers() {
		final Set<User> users = new HashSet<>();
		Iterator<PersistentGroup> iterator = getChildrenSet().iterator();
		if (iterator.hasNext()) {
			users.addAll(iterator.next().getMembers());
			while (iterator.hasNext()) {
				users.removeAll(iterator.next().getMembers());
			}
		}
		return users;
	}

	@Override
	public boolean isMember(final User user) {
		Iterator<PersistentGroup> iterator = getChildrenSet().iterator();
		if (iterator.hasNext()) {
			if (!iterator.next().isMember(user)) {
				return false;
			}
		} else {
			return false;
		}
		while (iterator.hasNext()) {
			if (iterator.next().isMember(user)) {
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
				users.removeAll(iterator.next().getMembers());
			}
		}
		return users;
	}

	@Override
	public boolean isMember(User user, DateTime when) {
		Iterator<PersistentGroup> iterator = getChildrenSet().iterator();
		if (iterator.hasNext()) {
			if (!iterator.next().isMember(user, when)) {
				return false;
			}
		} else {
			return false;
		}
		while (iterator.hasNext()) {
			if (iterator.next().isMember(user, when)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public PersistentGroup minus(PersistentGroup group) {
		Set<PersistentGroup> children = new HashSet<>(getChildrenSet());
		children.add(group);
		return DifferenceGroup.getInstance(children);
	}

	@Service
	public static DifferenceGroup getInstance(final PersistentGroup... children) {
		return getInstance(new HashSet<>(Arrays.asList(children)));
	}

	@Service
	public static DifferenceGroup getInstance(final Set<PersistentGroup> children) {
		DifferenceGroup group = select(DifferenceGroup.class, new Predicate<DifferenceGroup>() {
			@Override
			public boolean apply(@Nullable DifferenceGroup input) {
				return Iterables.elementsEqual(input.getChildrenSet(), children);
			}
		});
		return group != null ? group : new DifferenceGroup(children);
	}
}
