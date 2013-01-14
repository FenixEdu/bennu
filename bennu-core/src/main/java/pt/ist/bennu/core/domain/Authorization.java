package pt.ist.bennu.core.domain;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.core.security.UserView;

public class Authorization extends Authorization_Base {
	protected Authorization() {
		super();
	}

	public Authorization(PersistentGroup group) {
		setCreated(new DateTime());
		setCreator(UserView.getUser());
		setGroup(group);
	}

	public Set<User> getMembers() {
		return getGroup().getMembers();
	}

	public boolean isMember(User user) {
		return getGroup().isMember(user);
	}

	public Set<User> getMembers(DateTime when) {
		PersistentGroup group = getGroup(when);
		return group != null ? group.getMembers() : Collections.<User> emptySet();
	}

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

	public void changeGroup(PersistentGroup group) {
		Authorization old = new Authorization();
		old.setCreated(getCreated());
		old.setCreator(getCreator());
		old.setNext(this);
		setCreated(new DateTime());
		setCreator(UserView.getUser());
		super.setGroup(group);
	}

	public void and(PersistentGroup group) {
		changeGroup(getGroup().and(group));
	}

	public void or(PersistentGroup group) {
		changeGroup(getGroup().or(group));
	}

	public void minus(PersistentGroup group) {
		changeGroup(getGroup().minus(group));
	}

	public void not() {
		changeGroup(getGroup().not());
	}

	public void grant(User user) {
		changeGroup(getGroup().grant(user));
	}

	public void revoke(User user) {
		changeGroup(getGroup().revoke(user));
	}

	@Override
	public void setGroup(PersistentGroup group) {
		throw new UnsupportedOperationException();
	}
}
