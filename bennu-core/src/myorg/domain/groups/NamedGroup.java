package myorg.domain.groups;

import myorg.domain.MyOrg;
import myorg.domain.User;

public class NamedGroup extends NamedGroup_Base {

    public NamedGroup() {
	super();
	final MyOrg myOrg = getMyOrg();
	setSystemGroupMyOrg(myOrg);
    }

    public NamedGroup(String groupName) {
	this();
	setGroupName(groupName);
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

    @Override
    public String getName() {
	return getGroupName();
    }
}
