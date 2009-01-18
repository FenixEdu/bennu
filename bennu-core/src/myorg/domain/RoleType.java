package myorg.domain;

import myorg.util.BundleUtil;

public enum RoleType {

    MANAGER;

    public String getPresentationName() {
	return BundleUtil.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.roleType.name." + name());
    }

}
