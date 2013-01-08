package pt.ist.bennu.core.domain;

import java.util.Comparator;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.service.Service;

public class User extends User_Base {

	public interface UserPresentationStrategy {
		public String present(User user);

		public String shortPresent(User user);
	}

	public static final UserPresentationStrategy defaultStrategy = new UserPresentationStrategy() {

		@Override
		public String present(User user) {
			return user.getUsername();
		}

		@Override
		public String shortPresent(User user) {
			return user.getUsername();
		}

	};

	public static final Comparator<User> COMPARATOR_BY_NAME = new Comparator<User>() {

		@Override
		public int compare(final User user1, final User user2) {
			return user1.getUsername().compareTo(user2.getUsername());
		}

	};

	private static UserPresentationStrategy strategy = defaultStrategy;

	public User(final String username) {
		super();
		setUsername(username);
		setBennu(Bennu.getInstance());
		setCreated(new DateTime());
	}

	public static User findByUsername(final String username) {
		for (final User user : Bennu.getInstance().getUsersSet()) {
			if (user.getUsername().equalsIgnoreCase(username)) {
				return user;
			}
		}
		return null;
	}

	@Service
	@Override
	public void setLastLogoutDateTime(final DateTime lastLogoutDateTime) {
		super.setLastLogoutDateTime(lastLogoutDateTime);
	}

	@Service
	public void generatePassword() {
		final String password = RandomStringUtils.randomAlphanumeric(10);
		setPassword(password);
	}

	@Override
	public void setPassword(final String password) {
		final String newHash = DigestUtils.sha512Hex(password);
		final String oldHash = getPassword();
		if (newHash.equals(oldHash)) {
			throw new DomainException("BennuResources", "message.error.bad.old.password");
		}
		super.setPassword(newHash);
	}

	public boolean matchesPassword(final String password) {
		final String hash = DigestUtils.sha512Hex(password);
		return hash.equals(getPassword());
	}

	@Service
	public static User createNewUser(final String username) {
		return new User(username);
	}

	public String getPresentationName() {
		return strategy.present(this);
	}

	public String getShortPresentationName() {
		return strategy.shortPresent(this);
	}

	@Override
	public String toString() {
		return getUsername();
	}

	public static void registerUserPresentationStrategy(UserPresentationStrategy newStrategy) {
		if (strategy != defaultStrategy) {
			Logger.getLogger(User.class).warn("Overriding non-default strategy");
		}
		strategy = newStrategy;
	}

	public PasswordRecoveryRequest createNewPasswordRecoveryRequest() {
		super.setPassword(null);
		return new PasswordRecoveryRequest(this);
	}
}
