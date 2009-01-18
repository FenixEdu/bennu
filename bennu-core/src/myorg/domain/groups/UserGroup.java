package myorg.domain.groups;

import myorg.domain.User;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;

public class UserGroup extends UserGroup_Base {
    
    private UserGroup() {
        super();
    }

    @Override
    public boolean isMember(final User user) {
	return user != null;
    }

    @Service
    public static UserGroup getInstance() {
	final UserGroup userGroup = (UserGroup) PersistentGroup.getInstance(UserGroup.class);
	return userGroup == null ? new UserGroup() : userGroup;
    }

    @Override
    public String getName() {
	return BundleUtil.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.userGroup.name");
    }

}
