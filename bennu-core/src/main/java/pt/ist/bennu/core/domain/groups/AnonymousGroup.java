package pt.ist.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

public class AnonymousGroup extends AnonymousGroup_Base {
	protected AnonymousGroup() {
		super();
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

	@Override
	public Set<User> getMembers(DateTime when) {
		return getMembers();
	}

	@Override
	public boolean isMember(User user, DateTime when) {
		return isMember(user);
	}

	@Service
	public static AnonymousGroup getInstance() {
		AnonymousGroup group = select(AnonymousGroup.class);
		return group == null ? new AnonymousGroup() : group;
	}
}
