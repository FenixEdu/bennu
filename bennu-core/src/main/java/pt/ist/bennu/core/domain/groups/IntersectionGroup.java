package pt.ist.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class IntersectionGroup extends IntersectionGroup_Base {
	protected IntersectionGroup(Set<PersistentGroup> children) {
		super();
		init(children);
	}

	@Override
	protected String operator() {
		return "&";
	}

	@Override
	public Set<User> getMembers() {
		final Set<User> users = new HashSet<>();
		Iterator<PersistentGroup> iterator = getChildrenSet().iterator();
		if (iterator.hasNext()) {
			users.addAll(iterator.next().getMembers());
			while (iterator.hasNext()) {
				users.retainAll(iterator.next().getMembers());
			}
		}
		return users;
	}

	@Override
	public boolean isMember(final User user) {
		if (getChildrenCount() == 0) {
			return false;
		}
		for (final PersistentGroup persistentGroup : getChildrenSet()) {
			if (!persistentGroup.isMember(user)) {
				return false;
			}
		}
		return true;
	}

	@Service
	public static IntersectionGroup getInstance(final PersistentGroup... children) {
		return getInstance(new HashSet<>(Arrays.asList(children)));
	}

	@Service
	public static IntersectionGroup getInstance(final Set<PersistentGroup> children) {
		IntersectionGroup group = select(IntersectionGroup.class, new Predicate<IntersectionGroup>() {
			@Override
			public boolean apply(@Nullable IntersectionGroup input) {
				return Sets.symmetricDifference(input.getChildrenSet(), children).isEmpty();
			}
		});
		return group != null ? group : new IntersectionGroup(children);
	}
}
