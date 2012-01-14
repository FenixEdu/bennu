package pt.ist.emailNotifier.domain;

import org.joda.time.DateTime;

public class MessageId extends MessageId_Base {

    public MessageId(final Email email, final String messageID) {
	setSendTime(new DateTime());
	setEmail(email);
	setId(messageID);
    }

    public void delete() {
	removeEmail();
	deleteDomainObject();
    }

}
