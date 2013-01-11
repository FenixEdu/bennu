package pt.ist.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

public class AnonymousGroup extends AnonymousGroup_Base {
	protected AnonymousGroup() {
		super();
		setSystem(Bennu.getInstance());
	}

	@Override
	public String expression() {
		return "anonymous";
	}

	@Override
	public Set<User> getMembers() {
		return Collections.emptySet();
	}

	@Override
	public boolean isMember(final User user) {
		return user == null;
	}

	@Service
	public static AnonymousGroup getInstance() {
		final AnonymousGroup group = selectSystemGroup(AnonymousGroup.class);
		return group == null ? new AnonymousGroup() : group;
	}
}
