package myorg.domain.groups;

import java.util.HashSet;
import java.util.Set;

import myorg.domain.User;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;

public class IntersectionGroup extends IntersectionGroup_Base {

    public IntersectionGroup(final PersistentGroup... persistentGroups) {
	super();
	for (final PersistentGroup persistentGroup : persistentGroups) {
	    addPersistentGroups(persistentGroup);
	}
    }

    @Override
    public Set<User> getMembers() {
	final Set<User> users = new HashSet<User>();
	if (hasAnyPersistentGroups()) {
	    users.addAll(getPersistentGroupsSet().iterator().next().getMembers());
	    for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
		users.retainAll(persistentGroup.getMembers());
	    }
	}
	return users;
    }

    @Override
    public String getName() {
	return BundleUtil
		.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.intersectionGroup.name");
    }

    @Override
    public boolean isMember(final User user) {
	if (getPersistentGroupsCount() == 0) {
	    return false;
	}
	for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
	    if (!persistentGroup.isMember(user)) {
		return false;
	    }
	}
	return true;
    }

    @Service
    public static IntersectionGroup createIntersectionGroup(final PersistentGroup... persistentGroups) {
	return new IntersectionGroup(persistentGroups);
    }

}
