package pt.ist.bennu.core.util.legacy;

import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.legacy.People;
import pt.ist.bennu.core.domain.groups.legacy.Role;

public class LegacyUtil {

    public static boolean hasRoleType(final User user, final RoleType roleType) {
        if (user == null) {
            return false;
        }
        for (final People people : user.getPeopleGroupsSet()) {
            if (people instanceof Role) {
                final Role role = (Role) people;
                if (role.isRole(roleType) && role.isMember(user)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasRoleType(final User user, final String roleAsString) {
        if (user == null) {
            return false;
        }

        for (final People people : user.getPeopleGroupsSet()) {
            if (people instanceof Role) {
                final Role role = (Role) people;
                if (role.getGroupName().equals(roleAsString) && role.isMember(user)) {
                    return true;
                }
            }
        }
        return false;
    }

}
