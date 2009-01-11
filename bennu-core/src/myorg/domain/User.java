package myorg.domain;

import java.util.Comparator;

import myorg.domain.groups.People;
import myorg.domain.groups.Role;

public class User extends User_Base {

    public static final Comparator<User> COMPARATOR_BY_NAME = new Comparator<User>() {

	@Override
	public int compare(final User user1, final User user2) {
	    return user1.getUsername().compareTo(user2.getUsername());
	}
	
    };

    public User() {
        super();
        setMyOrg(MyOrg.getInstance());
    }

    public User(final String username) {
	this();
	setUsername(username);
    }

    public static User findByUsername(final String username) {
	for (final User user : MyOrg.getInstance().getUserSet()) {
	    if (user.getUsername().equalsIgnoreCase(username)) {
		return user;
	    }
	}
	return null;
    }

    public boolean hasRoleType(final RoleType roleType) {
	for (final People people : getPeopleGroupsSet()) {
	    if (people instanceof Role) {
		final Role role = (Role) people;
		if (role.getRoleType() == roleType) {
		    return true;
		}
	    }
	}
	return false;
    }

}
