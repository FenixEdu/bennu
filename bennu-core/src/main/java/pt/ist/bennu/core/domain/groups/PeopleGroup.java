package pt.ist.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.bennu.service.Service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class PeopleGroup extends PeopleGroup_Base {
	protected PeopleGroup(Set<User> users) {
		super();
		getMemberSet().addAll(users);
	}

	@Override
	public String getName() {
		Iterable<String> names = Iterables.transform(getMemberSet(), new Function<User, String>() {
			@Override
			public String apply(User user) {
				return user.getShortPresentationName();
			}
		});

		return BundleUtil.getString("BennuResources", "label.persistent.group." + getClass().getSimpleName(), Joiner.on(", ")
				.join(names));
	}

	@Override
	public Set<User> getMembers() {
		return getMemberSet();
	}

	@Override
	public boolean isMember(User user) {
		return getMemberSet().contains(user);
	}

	public PeopleGroup grant(User user) {
		Set<User> users = new HashSet<>(getMemberSet());
		users.add(user);
		return PeopleGroup.getInstance(users);
	}

	public PeopleGroup revoke(User user) {
		Set<User> users = new HashSet<>(getMemberSet());
		users.remove(user);
		return PeopleGroup.getInstance(users);
	}

	@Service
	public static PeopleGroup getInstance(User... users) {
		return getInstance(new HashSet<>(Arrays.asList(users)));
	}

	@Service
	public static PeopleGroup getInstance(Set<User> users) {
		for (PersistentGroup group : Bennu.getInstance().getGroupsSet()) {
			if (group instanceof PeopleGroup) {
				PeopleGroup intersectionGroup = (PeopleGroup) group;
				if (Iterables.elementsEqual(intersectionGroup.getMemberSet(), users)) {
					return intersectionGroup;
				}
			}
		}
		return new PeopleGroup(users);
	}
}
