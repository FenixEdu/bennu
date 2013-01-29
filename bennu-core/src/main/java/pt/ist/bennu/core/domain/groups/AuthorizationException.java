package pt.ist.bennu.core.domain.groups;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.util.BundleUtil;

public class AuthorizationException extends Exception {
	private AuthorizationException(PersistentGroup group, User user) {
		super(BundleUtil.getString("resources.BennuResources", "error.bennu.group.unauthorized", user.getUsername(),
				group.getPresentationName()));
	}

	private AuthorizationException(String message) {
		super(BundleUtil.getString("resources.BennuResources", message));
	}

	public static AuthorizationException unauthorized(PersistentGroup group, User user) {
		return new AuthorizationException(group, user);
	}

	public static AuthorizationException badAccessGroupConfiguration() {
		return new AuthorizationException("error.bennu.group.badaccessgroupconfiguration");
	}
}
