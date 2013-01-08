package pt.ist.bennu.core.domain.groups.immutable;

import java.util.Set;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.mutable.PeopleGroup;
import pt.ist.bennu.core.security.UserView;
import pt.ist.bennu.service.Service;

public class ManagerGroup extends ManagerGroup_Base {
	protected ManagerGroup() {
		super();
		setManagerGroup(PeopleGroup.getInstance(UserView.getUser()));
	}

	@Override
	public Set<User> getMembers() {
		return getManagerGroup().getMembers();
	}

	@Override
	public boolean isMember(final User user) {
		return getManagerGroup().isMember(user);
	}

	public void grant(User user) {
		setManagerGroup(getManagerGroup().grant(user));
	}

	public void revoke(User user) {
		setManagerGroup(getManagerGroup().revoke(user));
	}

	@Service
	public static ManagerGroup getInstance() {
		final ManagerGroup group = getSystemGroup(ManagerGroup.class);
		return group == null ? new ManagerGroup() : group;
	}
}
