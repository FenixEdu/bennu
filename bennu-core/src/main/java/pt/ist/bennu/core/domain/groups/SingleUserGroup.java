package pt.ist.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.service.Service;

public class SingleUserGroup extends SingleUserGroup_Base {
	private SingleUserGroup(final User user) {
		super();
		if (user.hasSingleUserGroup()) {
			throw new DomainException("BennuResources", "user.already.has.single.user.group");
		}
		setUser(user);
	}

	@Override
	public Set<User> getMembers() {
		return Collections.singleton(getUser());
	}

	@Override
	public String getName() {
		return getUser().getPresentationName();
	}

	@Override
	public boolean isMember(final User user) {
		return user == getUser();
	}

	@Service
	public static SingleUserGroup getInstance(final User user) {
		return user == null ? null : user.hasSingleUserGroup() ? user.getSingleUserGroup() : new SingleUserGroup(user);
	}
}
