package pt.ist.emailNotifier.domain;

import java.util.Collection;

import pt.utl.ist.fenix.tools.smtp.EmailSender;
import myorg.domain.scheduler.WriteCustomTask;

public class ManualDispatcher extends WriteCustomTask {

    @Override
    protected void doService() {
	System.out.println("Running Manual Send");
	for (Email email : EmailNotifier.getInstance().getEmails()) {
	    deliver(email);
	    email.delete();
	}
    }

    protected void deliver(final Email email) {
	final String fromName = email.getFromName();
	final String fromAddress = email.getFromAddress();
	final String[] replyTos = email.replyTos();
	final Collection<String> toAddresses = email.toAddresses();
	final Collection<String> ccAddresses = email.ccAddresses();
	final Collection<String> bccAddresses = email.bccAddresses();
	final String subject = email.getSubject();
	final String body = email.getBody();

	EmailSender.send(fromName, fromAddress, replyTos, toAddresses, ccAddresses, bccAddresses, subject, body);

	final StringBuilder stringBuilder = new StringBuilder();
	stringBuilder.append("Sent email from: ");
	stringBuilder.append(fromName);
	stringBuilder.append("<");
	stringBuilder.append(fromAddress);
	stringBuilder.append("> Subject: ");
	stringBuilder.append(subject);
	if (replyTos != null) {
	    stringBuilder.append(" Reply to ");
	    stringBuilder.append(replyTos.length);
	}
	if (toAddresses != null) {
	    stringBuilder.append(" To ");
	    stringBuilder.append(toAddresses.size());
	}
	if (ccAddresses != null) {
	    stringBuilder.append(" CC ");
	    stringBuilder.append(ccAddresses.size());
	}
	if (bccAddresses != null) {
	    stringBuilder.append(" BCC ");
	    stringBuilder.append(bccAddresses.size());
	}
	System.out.println(stringBuilder.toString());
    }

}
