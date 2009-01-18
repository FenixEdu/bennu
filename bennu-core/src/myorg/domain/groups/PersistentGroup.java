package myorg.domain.groups;

import java.util.Set;

import myorg.domain.MyOrg;
import myorg.domain.User;

public abstract class PersistentGroup extends PersistentGroup_Base {
    
    public PersistentGroup() {
        super();
        setMyOrg(MyOrg.getInstance());
        setOjbConcreteClass(getClass().getName());
    }

    public boolean isMember(final User user) {
	return false;
    }

    public abstract String getName();

    public abstract Set<User> getMembers();

    protected static PersistentGroup getInstance(final Class clazz) {
	for (final PersistentGroup persistentGroup : MyOrg.getInstance().getPersistentGroupsSet()) {
	    if (persistentGroup.getClass().isAssignableFrom(clazz)) {
		return persistentGroup;
	    }
	}
	return null;
    }

    protected static PersistentGroup getSystemGroup(final Class clazz) {
	for (final PersistentGroup persistentGroup : MyOrg.getInstance().getSystemGroupsSet()) {
	    if (persistentGroup.getClass().isAssignableFrom(clazz)) {
		return persistentGroup;
	    }
	}
	return null;
    }

}
