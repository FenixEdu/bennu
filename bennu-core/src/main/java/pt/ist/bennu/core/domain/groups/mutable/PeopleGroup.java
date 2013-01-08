package pt.ist.bennu.core.domain.groups.mutable;

import java.util.Set;

import pt.ist.bennu.core.domain.User;

public class PeopleGroup extends PeopleGroup_Base {
	public PeopleGroup() {
		super();
	}

	public PeopleGroup(User user) {
		this();
		addMember(user);
	}

	public PeopleGroup(String name) {
		this();
		setName(name);
	}

	public PeopleGroup(User user, String name) {
		this();
		addMember(user);
		setName(name);
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
		PeopleGroup group = copyAndPushHistory(this);
		group.addMember(user);
		return group;
	}

	public PeopleGroup revoke(User user) {
		PeopleGroup group = copyAndPushHistory(this);
		group.removeMember(user);
		return group;
	}

	public PeopleGroup changeMembers(Set<User> users) {
		PeopleGroup group = copyAndPushHistory(this);
		group.getMemberSet().clear();
		group.getMemberSet().addAll(users);
		return group;
	}
}
