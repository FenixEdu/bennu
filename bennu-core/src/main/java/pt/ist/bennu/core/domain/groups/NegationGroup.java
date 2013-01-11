package pt.ist.bennu.core.domain.groups;

import java.util.HashSet;
import java.util.Set;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

public class NegationGroup extends NegationGroup_Base {
	protected NegationGroup(PersistentGroup negated) {
		super();
		setNegated(negated);
	}

	@Override
	public String expression() {
		return "! " + getNegated().expression();
	}

	@Override
	public Set<User> getMembers() {
		Set<User> users = new HashSet<>(Bennu.getInstance().getUsersSet());
		users.removeAll(getNegated().getMembers());
		return users;
	}

	@Override
	public boolean isMember(User user) {
		return !getNegated().isMember(user);
	}

	@Service
	public static NegationGroup getInstance(final PersistentGroup persistentGroup) {
		for (PersistentGroup group : Bennu.getInstance().getGroupsSet()) {
			if (group instanceof NegationGroup) {
				NegationGroup negationGroup = (NegationGroup) group;
				if (negationGroup.getNegated().equals(persistentGroup)) {
					return negationGroup;
				}
			}
		}
		return new NegationGroup(persistentGroup);
	}
}
