package myorg.applicationTier;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import myorg.domain.RoleType;
import myorg.domain.User;
import myorg.domain.groups.Role;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.util.DomainReference;

public class Authenticate implements Serializable {

    private static final long serialVersionUID = -8446811315540707574L;
    private static final String randomValue;

    static {
	SecureRandom random = null;

	try {
	    random = SecureRandom.getInstance("SHA1PRNG");
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	}

	random.setSeed(System.currentTimeMillis());
	randomValue = String.valueOf(random.nextLong());
    }

    private static final Map<RoleType, Set<String>> roleUsernamesMap = new HashMap<RoleType, Set<String>>();

    private static Set<String> getUsernames(final RoleType roleType) {
	Set<String> usernames = roleUsernamesMap.get(roleType);
	if (usernames == null) {
	    usernames = new HashSet<String>();
	    roleUsernamesMap.put(roleType, usernames);		    
	}
	return usernames;
    }

    public static synchronized void initRole(final RoleType roleType, final String usernameStrings) {
	final Set<String> usernames = getUsernames(roleType);
	for (final String username : usernameStrings.split(",")) {
	    usernames.add(username.trim());
	}
    }

    public static class UserView implements pt.ist.fenixWebFramework.security.User, Serializable {

	private final DomainReference<User> userReference;

	private transient String privateConstantForDigestCalculation;

	private UserView(final String username) {
	    final User user = findByUsername(username);
	    userReference = new DomainReference<User>(user);
	}

	private User findByUsername(final String username) {
	    final User user = User.findByUsername(username);
	    return user == null ? new User(username) : user;
	}

	public String getUsername() {
	    return getUser().getUsername();
	}

	public boolean hasRole(final String roleAsString) {
	    final RoleType roleType = RoleType.valueOf(roleAsString);
	    final User user = getUser();
	    return user != null && user.hasRoleType(roleType);
	}

	@Override
	public boolean equals(Object obj) {
	    return obj instanceof UserView && getUsername().equals(((UserView) obj).getUsername());
	}

	@Override
	public int hashCode() {
	    return getUsername().hashCode();
	}

	public User getUser() {
	    return userReference == null ? null : userReference.getObject();
	}

	// TODO do an accurate and secure method here
	public String getPrivateConstantForDigestCalculation() {
	    if (privateConstantForDigestCalculation == null) {
		final User user = getUser();
		privateConstantForDigestCalculation = user.getUsername() + user.getPassword() + randomValue;
	    }
	    return privateConstantForDigestCalculation;
	}

	public static User getCurrentUser() {
	    final UserView userView = (UserView) pt.ist.fenixWebFramework.security.UserView.getUser();
	    return userView == null ? null : userView.getUser();
	}
    }

    @Service
    public static UserView authenticate(final String username, final String password) {
	final UserView userView = new UserView(username);
	pt.ist.fenixWebFramework.security.UserView.setUser(userView);

	final User user = userView.getUser();
	for (final Entry<RoleType, Set<String>> entry : roleUsernamesMap.entrySet()) {
	    if (entry.getValue().contains(username)) {
		addRoleType(user, entry.getKey());
	    }
	}

	return userView;
    }

    protected static void addRoleType(final User user, final RoleType roleType) {
	if (!user.hasRoleType(roleType)) {
	    user.addPeopleGroups(Role.getRole(roleType));
	}
    }

}
