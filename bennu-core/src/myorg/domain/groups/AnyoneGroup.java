package myorg.domain.groups;

import java.util.Set;

import myorg.domain.MyOrg;
import myorg.domain.User;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;

public class AnyoneGroup extends AnyoneGroup_Base {

    private AnyoneGroup() {
        super();
        final MyOrg myOrg = getMyOrg();
        setSystemGroupMyOrg(myOrg);
    }

    @Override
    public boolean isMember(final User user) {
	return true;
    }

    @Service
    public static AnyoneGroup getInstance() {
	final AnyoneGroup anyoneGroup = (AnyoneGroup) PersistentGroup.getSystemGroup(AnyoneGroup.class);
	return anyoneGroup == null ? new AnyoneGroup() : anyoneGroup;
    }

    @Override
    public String getName() {
	return BundleUtil.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.anyoneGroup.name");
    }

    @Override
    public Set<User> getMembers() {
	return null;
    }

}
