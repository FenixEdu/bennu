package pt.ist.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

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

	@Service
	public static DifferenceGroup getInstance(final PersistentGroup... children) {
		return getInstance(new HashSet<>(Arrays.asList(children)));
	}

	@Service
	public static DifferenceGroup getInstance(Set<PersistentGroup> children) {
		DifferenceGroup group = getInstance(DifferenceGroup.class, children);
		return group != null ? group : new DifferenceGroup(children);
	}
}
