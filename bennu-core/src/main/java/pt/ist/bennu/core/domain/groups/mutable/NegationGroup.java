package pt.ist.bennu.core.domain.groups.mutable;

import java.util.HashSet;
import java.util.Set;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.service.Service;

public class NegationGroup extends NegationGroup_Base {
	public NegationGroup(PersistentGroup persistentGroup) {
		super();
		setPersistentGroup(persistentGroup);
	}

	@Override
	public Set<User> getMembers() {
		Set<User> users = new HashSet<>(Bennu.getInstance().getUsersSet());
		users.removeAll(getPersistentGroup().getMembers());
		return users;
	}

	@Override
	public String getName() {
		return "NOT " + getPersistentGroup().getName();
	}

	@Override
	public boolean isMember(User user) {
		return !getPersistentGroup().isMember(user);
	}

	@Service
	public static NegationGroup createNegationGroup(final PersistentGroup persistentGroup) {
		return new NegationGroup(persistentGroup);
	}

	@Service
	public static NegationGroup getOrCreateGroup(final PersistentGroup persistentGroup) {
		for (PersistentGroup group : Bennu.getInstance().getGroupsSet()) {
			if (group instanceof NegationGroup) {
				NegationGroup negationGroup = (NegationGroup) group;
				if (negationGroup.getPersistentGroup().equals(persistentGroup)) {
					return negationGroup;
				}
			}
		}
		return new NegationGroup(persistentGroup);
	}
}
