package pt.ist.bennu.core.domain;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;

import pt.ist.bennu.service.Service;

public class PasswordRecoveryRequest extends PasswordRecoveryRequest_Base {

	PasswordRecoveryRequest(final User user) {
		super();
		setRequested(new DateTime());
		setPrevious(user.getPasswordRecoveryRequest());
		setUser(user);
		setVerificationHash(calculateHash());
	}

	private String calculateHash() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getExternalId());
		builder.append(hashCode());
		builder.append(getRequested());
		builder.append(getUser().getUsername());
		builder.append(getUser().getExternalId());
		return DigestUtils.sha512Hex(builder.toString());
	}

	public String getRecoveryUrl(final String hostUrl) {
		final StringBuilder builder = new StringBuilder(hostUrl);
		builder.append("home.do?method=recoverPassword&passwordRecoveryRequestId=");
		builder.append(getExternalId());
		builder.append("&verificationHash=");
		builder.append(getVerificationHash());
		return builder.toString();
	}

	@Service
	public Boolean consume(final String verificationHash) {
		if (getConsumed() == null) {
			setConsumed(new DateTime());
			return getVerificationHash().equals(verificationHash);
		}
		return false;
	}

}
