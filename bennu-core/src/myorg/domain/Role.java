package myorg.domain;

import myorg.domain.MyOrg;
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
	for (final Role role : MyOrg.getInstance().getRolesSet()) {
	    if (role.getRoleType() == roleType) {
		return role;
	    }
	}
	return createRole(roleType);
    }
    
}
