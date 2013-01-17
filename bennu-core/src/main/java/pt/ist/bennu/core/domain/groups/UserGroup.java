package pt.ist.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class UserGroup extends UserGroup_Base {
	protected UserGroup(Set<User> users) {
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
	public Set<User> getMembers(DateTime when) {
		return getMembers();
	}

	@Override
	public boolean isMember(User user, DateTime when) {
		return isMember(user);
	}

	@Override
	public UserGroup grant(User user) {
		Set<User> users = new HashSet<>(getMemberSet());
		users.add(user);
		return UserGroup.getInstance(users);
	}

	@Override
	public UserGroup revoke(User user) {
		Set<User> users = new HashSet<>(getMemberSet());
		users.remove(user);
		return UserGroup.getInstance(users);
	}

	@Service
	public static UserGroup getInstance(User... users) {
		return getInstance(new HashSet<>(Arrays.asList(users)));
	}

	@Service
	public static UserGroup getInstance(final Set<User> users) {
		UserGroup group = select(UserGroup.class, new Predicate<UserGroup>() {
			@Override
			public boolean apply(@Nullable UserGroup input) {
				return Sets.symmetricDifference(input.getMemberSet(), users).isEmpty();
			}
		});
		return group != null ? group : new UserGroup(users);
	}
}
