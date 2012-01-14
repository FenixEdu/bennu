package pt.ist.emailNotifier.domain;


public class MessageTransportResult extends MessageTransportResult_Base {

    public MessageTransportResult(final Email email, final Integer code, final String description) {
	super();
	setEmail(email);
	setCode(code);
	setDescription(description);
    }

    public void delete() {
	removeEmail();
	deleteDomainObject();
    }

}
