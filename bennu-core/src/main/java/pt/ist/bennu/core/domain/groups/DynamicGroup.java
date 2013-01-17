package pt.ist.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.security.UserView;

import com.google.common.base.Predicate;

public class DynamicGroup extends DynamicGroup_Base {
	protected DynamicGroup() {
		super();
	}

	public DynamicGroup(String name, PersistentGroup group) {
		this();
		setName(name);
		setCreated(new DateTime());
		setCreator(UserView.getUser());
		setGroup(group);
	}

	@Override
	public String expression() {
		return "D(" + getName() + ")";
	}

	@Override
	public Set<User> getMembers() {
		return getGroup().getMembers();
	}

	@Override
	public boolean isMember(User user) {
		return getGroup().isMember(user);
	}

	@Override
	public Set<User> getMembers(DateTime when) {
		PersistentGroup group = getGroup(when);
		return group != null ? group.getMembers() : Collections.<User> emptySet();
	}

	@Override
	public boolean isMember(User user, DateTime when) {
		PersistentGroup group = getGroup(when);
		return group != null ? group.isMember(user) : false;
	}

	public PersistentGroup getGroup(DateTime when) {
		if (when.isAfter(getCreated())) {
			return getGroup();
		}
		if (hasPrevious()) {
			return getPrevious().getGroup(when);
		}
		return null;
	}

	private void pushHistory() {
		DynamicGroup old = new DynamicGroup();
		old.removeHost();
		old.setName(getName());
		old.setCreated(getCreated());
		old.setCreator(getCreator());
		old.setGroup(getGroup());
		old.setNext(this);
		setCreated(new DateTime());
		setCreator(UserView.getUser());
	}

	public DynamicGroup rename(String name) {
		pushHistory();
		setName(name);
		return this;
	}

	public DynamicGroup changeGroup(PersistentGroup group) {
		pushHistory();
		setGroup(group);
		return this;
	}

	@Override
	public PersistentGroup and(PersistentGroup group) {
		return changeGroup(getGroup().and(group));
	}

	@Override
	public PersistentGroup or(PersistentGroup group) {
		return changeGroup(getGroup().or(group));
	}

	@Override
	public PersistentGroup minus(PersistentGroup group) {
		return changeGroup(getGroup().minus(group));
	}

	@Override
	public PersistentGroup not() {
		return changeGroup(getGroup().not());
	}

	@Override
	public PersistentGroup grant(User user) {
		return changeGroup(getGroup().grant(user));
	}

	@Override
	public PersistentGroup revoke(User user) {
		return changeGroup(getGroup().revoke(user));
	}

	public static PersistentGroup getInstance(final String name) throws GroupException {
		DynamicGroup group = select(DynamicGroup.class, new Predicate<DynamicGroup>() {
			@Override
			public boolean apply(@Nullable DynamicGroup input) {
				return input.getName().equals(name);
			}
		});
		if (group != null) {
			return group;
		}
		throw new GroupException("Could not find dynamic group named: " + name);
	}
}
