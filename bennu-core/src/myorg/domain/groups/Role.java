package myorg.domain.groups;

import myorg.domain.MyOrg;
import myorg.domain.RoleType;
import myorg.domain.User;
import pt.ist.fenixWebFramework.services.Service;

public class Role extends Role_Base {

    public Role(final RoleType roleType) {
        super();
        final MyOrg myOrg = MyOrg.getInstance();
        setMyOrg(myOrg);
        setSystemGroupMyOrg(myOrg);
        setRoleType(roleType);
    }

    @Service
    public static Role createRole(final RoleType roleType) {
	final Role role = find(roleType);
	return role == null ? new Role(roleType) : role;
    }

    protected static Role find(final RoleType roleType) {
	for (final PersistentGroup group : MyOrg.getInstance().getSystemGroupsSet()) {
	    if (group instanceof Role) {
		final Role role = (myorg.domain.groups.Role) group;
		if (role.getRoleType() == roleType) {
		    return role;
		}
	    }
	}
	return null;
    }

    public static Role getRole(final RoleType roleType) {
	final Role role = find(roleType);
	return role == null ? createRole(roleType) : role;
    }

    @Override
    public boolean isMember(final User user) {
	if (user != null) {
	    for (final People people : user.getPeopleGroupsSet()) {
		if (people == this) {
		    return true;
		}
	    }
	}
	return false;
    }

}
