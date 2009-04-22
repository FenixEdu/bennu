package pt.ist.emailNotifier.domain;

import java.util.Arrays;
import java.util.Collection;

import pt.ist.emailNotifier.util.EmailAddressList;
import pt.ist.fenixframework.pstm.Transaction;

public class Email extends Email_Base {

    public Email() {
	super();
	setEmailNotifier(EmailNotifier.getInstance());
    }

    public Email(final String fromName, final String fromAddress, final String[] replyTos, final Collection<String> toAddresses,
	    final Collection<String> ccAddresses, final Collection<String> bccAddresses, final String subject, final String body) {

	this();
	setFromName(fromName);
	setFromAddress(fromAddress);
	setReplyTos(new EmailAddressList(replyTos == null ? null : Arrays.asList(replyTos)));
	setToAddresses(new EmailAddressList(toAddresses));
	setCcAddresses(new EmailAddressList(ccAddresses));
	setBccAddresses(new EmailAddressList(bccAddresses));
	setSubject(subject);
	setBody(body);
    }

    public void delete() {
	removeEmailNotifier();
	Transaction.deleteObject(this);
    }

    public String[] replyTos() {
	return getReplyTos() == null ? null : getReplyTos().toArray();
    }

    public Collection<String> toAddresses() {
	return getToAddresses() == null ? null : getToAddresses().toCollection();
    }

    public Collection<String> ccAddresses() {
	return getCcAddresses() == null ? null : getCcAddresses().toCollection();
    }

    public Collection<String> bccAddresses() {
	return getBccAddresses() == null ? null : getBccAddresses().toCollection();
    }

}
