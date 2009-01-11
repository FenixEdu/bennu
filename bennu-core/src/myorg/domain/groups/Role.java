package myorg.domain.groups;

import myorg.domain.MyOrg;
import myorg.domain.RoleType;
import myorg.domain.User;
import pt.ist.fenixWebFramework.services.Service;

public class Role extends Role_Base {

    public Role(final RoleType roleType) {
        super();
        setMyOrg(MyOrg.getInstance());
        setRoleType(roleType);
    }

    @Service
    public static Role createRole(RoleType roleType) {
	return new Role(roleType);
    }

    public static Role getRole(final RoleType roleType) {
	for (final PersistentGroup group : MyOrg.getInstance().getPersistentGroupSet()) {
	    if (group instanceof Role) {
		final Role role = (myorg.domain.groups.Role) group;
		if (role.getRoleType() == roleType) {
		    return role;
		}
	    }
	}
	return createRole(roleType);
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
