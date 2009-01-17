package myorg.domain.groups;

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

    protected static PersistentGroup getInstance(final Class clazz) {
	for (final PersistentGroup persistentGroup : MyOrg.getInstance().getPersistentGroupsSet()) {
	    if (persistentGroup.getClass().isAssignableFrom(clazz)) {
		return persistentGroup;
	    }
	}
	return null;
    }

}
