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
package pt.ist.bennu.email.domain;

import java.util.Collection;

import pt.ist.bennu.email.util.EmailSender;
import pt.ist.bennu.scheduler.CronTask;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class ManualDispatcher extends CronTask {

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

    @Override
    public void runTask() {
        System.out.println("Running Manual Send");
        for (Email email : EmailNotifier.getInstance().getEmailsSet()) {
            deliver(email);
            email.delete();
        }
    }

}
