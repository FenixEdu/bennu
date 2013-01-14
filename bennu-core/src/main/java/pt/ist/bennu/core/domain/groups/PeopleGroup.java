package pt.ist.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class PeopleGroup extends PeopleGroup_Base {
	protected PeopleGroup(Set<User> users) {
		super();
		getMemberSet().addAll(users);
	}

	@Override
	public String expression() {
		Iterable<String> usernames = Iterables.transform(getMemberSet(), new Function<User, String>() {
			@Override
			public String apply(User user) {
				return user.getUsername();
			}
		});

		return "P(" + Joiner.on(", ").join(usernames) + ")";
	}

	@Override
	public Set<User> getMembers() {
		return getMemberSet();
	}

	@Override
	public boolean isMember(User user) {
		return getMemberSet().contains(user);
	}

	@Override
	public PeopleGroup grant(User user) {
		Set<User> users = new HashSet<>(getMemberSet());
		users.add(user);
		return PeopleGroup.getInstance(users);
	}

	@Override
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
	public static PeopleGroup getInstance(final Set<User> users) {
		PeopleGroup group = select(PeopleGroup.class, new Predicate<PeopleGroup>() {
			@Override
			public boolean apply(@Nullable PeopleGroup input) {
				return Sets.symmetricDifference(input.getMemberSet(), users).isEmpty();
			}
		});
		return group != null ? group : new PeopleGroup(users);
	}
}
