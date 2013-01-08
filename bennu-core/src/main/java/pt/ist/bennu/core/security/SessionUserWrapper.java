package pt.ist.bennu.core.security;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

class SessionUserWrapper implements Serializable {
	private static final long serialVersionUID = -16953310282144136L;

	private final String userExternalId;

	private final String privateConstantForDigestCalculation;

	private final DateTime userViewCreationDateTime = new DateTime();

	private final DateTime lastLogoutDateTime;

	public SessionUserWrapper(final User user) {
		userExternalId = user.getExternalId();

		SecureRandom random = null;

		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new Error("No secure algorithm available.");
		}

		random.setSeed(System.currentTimeMillis());

		privateConstantForDigestCalculation = user.getUsername() + user.getPassword() + random.nextLong();

		lastLogoutDateTime = user.getLastLogoutDateTime();
	}

	public SessionUserWrapper(final String username) {
		this(findByUsername(username));
	}

	private static User findByUsername(final String username) {
		final User user = User.findByUsername(username);
		if (user == null) {
			final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
			if (virtualHost != null && virtualHost.isCasEnabled() || Bennu.getInstance().getUsersCount() == 0) {
				return new User(username);
			}
			return new User(username);
			// throw new Error("authentication.exception");
		}
		return user;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SessionUserWrapper && userExternalId.equals(((SessionUserWrapper) obj).userExternalId);
	}

	@Override
	public int hashCode() {
		return userExternalId.hashCode();
	}

	public User getUser() {
		return userExternalId == null ? null : (User) AbstractDomainObject.fromExternalId(userExternalId);
	}

	public String getUsername() {
		return getUser() != null ? getUser().getUsername() : null;
	}

	public String getPrivateConstantForDigestCalculation() {
		return privateConstantForDigestCalculation;
	}

	public DateTime getUserCreationDateTime() {
		return userViewCreationDateTime;
	}

	public DateTime getLastLogoutDateTime() {
		return lastLogoutDateTime;
	}
}