package myorg.domain.groups;

import java.util.Set;

import myorg.domain.MyOrg;
import myorg.domain.User;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;

public class AnonymousGroup extends AnonymousGroup_Base {

    @Service
    public static AnonymousGroup getInstance() {
	final AnonymousGroup anonymousGroup = (AnonymousGroup) PersistentGroup.getSystemGroup(AnonymousGroup.class);
	return anonymousGroup == null ? new AnonymousGroup() : anonymousGroup;
    }

    public AnonymousGroup() {
        super();
        final MyOrg myOrg = getMyOrg();
        setSystemGroupMyOrg(myOrg);
    }

    @Override
    public boolean isMember(final User user) {
	return user == null;
    }

    @Override
    public Set<User> getMembers() {
	return null;
    }

    @Override
    public String getName() {
	return BundleUtil.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.anonymousGroup.name");
    }

}
