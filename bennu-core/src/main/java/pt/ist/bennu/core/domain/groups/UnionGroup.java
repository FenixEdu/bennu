package pt.ist.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

public class UnionGroup extends UnionGroup_Base {
	protected UnionGroup(Set<PersistentGroup> children) {
		super();
		init(children);
	}

	@Override
	protected String operator() {
		return "|";
	}

	@Override
	public Set<User> getMembers() {
		final Set<User> users = new HashSet<>();
		for (final PersistentGroup persistentGroup : getChildrenSet()) {
			users.addAll(persistentGroup.getMembers());
		}
		return users;
	}

	@Override
	public boolean isMember(final User user) {
		for (final PersistentGroup persistentGroup : getChildrenSet()) {
			if (persistentGroup.isMember(user)) {
				return true;
			}
		}
		return false;
	}

	@Service
	public static UnionGroup getInstance(final PersistentGroup... children) {
		return getInstance(new HashSet<>(Arrays.asList(children)));
	}

	@Service
	public static UnionGroup getInstance(Set<PersistentGroup> children) {
		UnionGroup group = getInstance(UnionGroup.class, children);
		return group != null ? group : new UnionGroup(children);
	}
}
