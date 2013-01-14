package pt.ist.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

public class AnyoneGroup extends AnyoneGroup_Base {
	protected AnyoneGroup() {
		super();
	}

	@Override
	public String expression() {
		return "anyone";
	}

	@Override
	public Set<User> getMembers() {
		return Collections.emptySet();
	}

	@Override
	public boolean isMember(final User user) {
		return true;
	}

	@Service
	public static AnyoneGroup getInstance() {
		AnyoneGroup group = select(AnyoneGroup.class);
		return group == null ? new AnyoneGroup() : group;
	}
}
