package pt.ist.bennu.core.domain.groups.immutable;

import java.util.Set;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.mutable.PeopleGroup;
import pt.ist.bennu.core.security.UserView;
import pt.ist.bennu.service.Service;

public class ManagerGroup extends ManagerGroup_Base {
	protected ManagerGroup() {
		super();
		setManagerGroup(new PeopleGroup(UserView.getUser()));
	}

	@Override
	public Set<User> getMembers() {
		return getManagerGroup().getMembers();
	}

	@Override
	public boolean isMember(final User user) {
		return getManagerGroup().isMember(user);
	}

	@Service
	public static ManagerGroup getInstance() {
		final ManagerGroup group = getSystemGroup(ManagerGroup.class);
		return group == null ? new ManagerGroup() : group;
	}
}
