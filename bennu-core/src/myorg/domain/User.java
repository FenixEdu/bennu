package myorg.domain;

public class User extends User_Base {

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
	for (final Role role : getRolesSet()) {
	    if (role.getRoleType() == roleType) {
		return true;
	    }
	}
	return false;
    }

}
