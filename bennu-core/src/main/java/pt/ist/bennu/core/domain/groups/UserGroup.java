package pt.ist.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

public class UserGroup extends UserGroup_Base {
	protected UserGroup() {
		super();
		setSystem(Bennu.getInstance());
	}

	@Override
	public Set<User> getMembers() {
		return Collections.unmodifiableSet(Bennu.getInstance().getUsersSet());
	}

	@Override
	public boolean isMember(final User user) {
		return user != null;
	}

	@Service
	public static UserGroup getInstance() {
		final UserGroup group = getSystemGroup(UserGroup.class);
		return group == null ? new UserGroup() : group;
	}
}
