package pt.ist.bennu.core.domain.groups;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

import com.google.common.base.Predicate;

public class NegationGroup extends NegationGroup_Base {
	protected NegationGroup(PersistentGroup negated) {
		super();
		setNegated(negated);
	}

	@Override
	public String expression() {
		return "! " + getNegated().expression();
	}

	@Override
	public Set<User> getMembers() {
		Set<User> users = new HashSet<>(Bennu.getInstance().getUsersSet());
		users.removeAll(getNegated().getMembers());
		return users;
	}

	@Override
	public boolean isMember(User user) {
		return !getNegated().isMember(user);
	}

	@Override
	public Set<User> getMembers(DateTime when) {
		Set<User> users = new HashSet<>(Bennu.getInstance().getUsersSet());
		users.removeAll(getNegated().getMembers(when));
		return users;
	}

	@Override
	public boolean isMember(User user, DateTime when) {
		return !getNegated().isMember(user, when);
	}

	@Override
	public PersistentGroup not() {
		return getNegated();
	}

	@Service
	public static NegationGroup getInstance(final PersistentGroup persistentGroup) {
		NegationGroup group = select(NegationGroup.class, new Predicate<NegationGroup>() {
			@Override
			public boolean apply(@Nullable NegationGroup input) {
				return input.getNegated().equals(persistentGroup);
			}
		});
		return group != null ? group : new NegationGroup(persistentGroup);
	}
}
