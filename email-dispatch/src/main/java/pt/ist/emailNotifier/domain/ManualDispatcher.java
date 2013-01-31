/*
 * @(#)ManualDispatcher.java
 *
 * Copyright 2012 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the E-mail SMTP Adapter Module.
 *
 *   The E-mail SMTP Adapter Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The E-mail Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the E-mail Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.emailNotifier.domain;

import java.util.Collection;

import pt.ist.bennu.core.domain.scheduler.WriteCustomTask;
import pt.utl.ist.fenix.tools.smtp.EmailSender;

/**
 * 
 * @author Luis Cruz
 * 
 */
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
