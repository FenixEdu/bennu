package pt.ist.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

public class NobodyGroup extends NobodyGroup_Base {
	protected NobodyGroup() {
		super();
	}

	@Override
	public String expression() {
		return "nobody";
	}

	@Override
	public Set<User> getMembers() {
		return Collections.emptySet();
	}

	@Override
	public boolean isMember(final User user) {
		return false;
	}

	@Override
	public Set<User> getMembers(DateTime when) {
		return getMembers();
	}

	@Override
	public boolean isMember(User user, DateTime when) {
		return isMember(user);
	}

	@Service
	public static NobodyGroup getInstance() {
		NobodyGroup group = select(NobodyGroup.class);
		return group == null ? new NobodyGroup() : group;
	}
}
