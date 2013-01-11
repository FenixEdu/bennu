package pt.ist.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

public class NobodyGroup extends NobodyGroup_Base {
	protected NobodyGroup() {
		super();
		setSystem(Bennu.getInstance());
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

	@Service
	public static NobodyGroup getInstance() {
		final NobodyGroup group = getSystemGroup(NobodyGroup.class);
		return group == null ? new NobodyGroup() : group;
	}
}
