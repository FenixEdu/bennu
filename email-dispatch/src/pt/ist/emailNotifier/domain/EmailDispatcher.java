package pt.ist.emailNotifier.domain;



public class EmailDispatcher extends EmailDispatcher_Base {

    public EmailDispatcher() {
	super();
    }

    @Override
    public void executeTask() {
//	for (Email email : EmailNotifier.getInstance().getEmails()) {
//	    email.deliver();
//	}
    }

    @Override
    public String getLocalizedName() {
	return getClass().getName();
    }

}
