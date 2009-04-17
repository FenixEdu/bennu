package myorg.domain.groups;

import java.util.HashSet;
import java.util.Set;

import myorg.domain.User;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;

public class UnionGroup extends UnionGroup_Base {
    
    public UnionGroup(final PersistentGroup... persistentGroups) {
        super();
        for (final PersistentGroup persistentGroup : persistentGroups) {
            addPersistentGroups(persistentGroup);
        }
    }

    @Override
    public Set<User> getMembers() {
	final Set<User> users = new HashSet<User>();
	for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
	    users.addAll(persistentGroup.getMembers());
	}
	return users;
    }

    @Override
    public String getName() {
	return BundleUtil.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.anyoneGroup.name");
    }

    @Override
    public boolean isMember(final User user) {
	for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
	    if (persistentGroup.isMember(user)) {
		return true;
	    }
	}
	return false;
    }

    @Service
    public static UnionGroup createUnionGroup(final PersistentGroup... persistentGroups) {
	return new UnionGroup(persistentGroups);
    }

}
