package myorg.domain.groups;

import java.util.Collections;
import java.util.Set;

import myorg.domain.User;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;

public class EmptyGroup extends EmptyGroup_Base {
    
    private EmptyGroup() {
        super();
        setSystemGroupMyOrg(getMyOrg());
    }

    @Override
    public Set<User> getMembers() {
	return Collections.emptySet();
    }

    @Override
    public String getName() {
	return BundleUtil.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.emptyGroup.name");
    }

    @Override
    public boolean isMember(final User user) {
	return false;
    }

    @Service
    public static EmptyGroup getInstance() {
	final EmptyGroup emptyGroup = (EmptyGroup) PersistentGroup.getSystemGroup(EmptyGroup.class);
	return emptyGroup == null ? new EmptyGroup() : emptyGroup;
    }
    
}
