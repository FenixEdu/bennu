/*
 * @(#)Email.java
 *
 * Copyright 2009 Instituto Superior Tecnico
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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.joda.time.DateTime;

import pt.ist.bennu.core._development.PropertiesManager;
import pt.ist.emailNotifier.util.EmailAddressList;
import pt.utl.ist.fenix.tools.util.StringAppender;

/**
 * 
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class Email extends Email_Base {

    private static Session SESSION = null;
    private static int MAX_MAIL_RECIPIENTS;

    private static synchronized Session init() {
        final Properties properties = new Properties();
        properties.put("mail.smtp.host", PropertiesManager.getProperty("mail.smtp.host"));
        properties.put("mail.smtp.name", PropertiesManager.getProperty("mail.smtp.name"));
        properties.put("mailSender.max.recipients", PropertiesManager.getProperty("mailSender.max.recipients"));
        properties.put("mailingList.host.name", PropertiesManager.getProperty("mailingList.host.name"));
        properties.put("mail.debug", "true");
        final Session tempSession = Session.getDefaultInstance(properties, null);
        for (final Entry<Object, Object> entry : tempSession.getProperties().entrySet()) {
            System.out.println("key: " + entry.getKey() + "   value: " + entry.getValue());
        }
        MAX_MAIL_RECIPIENTS = Integer.parseInt(properties.getProperty("mailSender.max.recipients"));
        SESSION = tempSession;
        return SESSION;
    }

    public Email() {
        super();
        setEmailNotifier(EmailNotifier.getInstance());
    }

    public Email(final String fromName, final String fromAddress, final String subject, final String body, String... toAddresses) {
        this(fromName, fromAddress, null, Arrays.asList(toAddresses), null, null, subject, body);
    }

    public Email(final String fromName, final String fromAddress, final String[] replyTos, final Collection<String> toAddresses,
            final Collection<String> ccAddresses, final Collection<String> bccAddresses, final String subject, final String body) {
        this(fromName, fromAddress, replyTos, toAddresses, ccAddresses, bccAddresses, subject, body, null);
    }

    public Email(final String fromName, final String fromAddress, final String[] replyTos, final Collection<String> toAddresses,
            final Collection<String> ccAddresses, final Collection<String> bccAddresses, final String subject, final String body,
            final String htmlBody) {
        this();
        setFromName(fromName);
        setFromAddress(fromAddress);
        setReplyTos(new EmailAddressList(replyTos == null ? null : Arrays.asList(replyTos)));
        setToAddresses(new EmailAddressList(toAddresses));
        setCcAddresses(new EmailAddressList(ccAddresses));
        setBccAddresses(new EmailAddressList(bccAddresses));
        setSubject(subject);
        setBody(body);
        setHtmlBody(htmlBody);
    }

    public void delete() {
        for (final MessageTransportResult messageTransportResult : getMessageTransportResultSet()) {
            messageTransportResult.delete();
        }
        for (final MessageId messageId : getMessageIdsSet()) {
            messageId.delete();
        }
        setEmailNotifier(null);
        super.deleteDomainObject();
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

    private void logProblem(final String description) {
        new MessageTransportResult(this, null, description);
    }

    private void logProblem(final MessagingException e) {
        logProblem(e.getMessage());
        final Exception nextException = e.getNextException();
        if (nextException != null) {
            if (nextException instanceof MessagingException) {
                logProblem((MessagingException) nextException);
            } else {
                logProblem(nextException.getMessage());
            }
        }
    }

    private void abort() {
        final Collection<String> failed = new HashSet<String>();
        final EmailAddressList failedAddresses = getFailedAddresses();
        if (failedAddresses != null && !failedAddresses.isEmpty()) {
            failed.addAll(failedAddresses.toCollection());
        }
        final EmailAddressList toAddresses = getToAddresses();
        if (toAddresses != null && !toAddresses.isEmpty()) {
            failed.addAll(toAddresses.toCollection());
        }
        final EmailAddressList ccAddresses = getCcAddresses();
        if (ccAddresses != null && !ccAddresses.isEmpty()) {
            failed.addAll(ccAddresses.toCollection());
        }
        final EmailAddressList bccAddresses = getBccAddresses();
        if (bccAddresses != null && !bccAddresses.isEmpty()) {
            failed.addAll(bccAddresses.toCollection());
        }
        final EmailAddressList emailAddressList = new EmailAddressList(failed);
        setFailedAddresses(emailAddressList);

        setToAddresses(null);
        setCcAddresses(null);
        setBccAddresses(null);
    }

    private void retry(final EmailAddressList toAddresses, final EmailAddressList ccAddresses, final EmailAddressList bccAddresses) {
        setToAddresses(toAddresses);
        setCcAddresses(ccAddresses);
        setBccAddresses(bccAddresses);
    }

    private static String encode(final String string) {
        try {
            return string == null ? "" : MimeUtility.encodeText(string);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            return string;
        }
    }

    protected static String constructFromString(final String fromName, String fromAddress) {
        return (fromName == null || fromName.length() == 0) ? fromAddress : StringAppender.append(fromName.replace(',', ' '),
                " <", fromAddress, ">");
    }

    private class EmailMimeMessage extends MimeMessage {

        private String fenixMessageId = null;

        public EmailMimeMessage() {
            super(SESSION == null ? init() : SESSION);
        }

        @Override
        public String getMessageID() throws MessagingException {
            if (fenixMessageId == null) {
                final String externalId = getExternalId();
                fenixMessageId = externalId + "." + new DateTime().getMillis() + "@fenix";
            }
            return fenixMessageId;
        }

        @Override
        protected void updateMessageID() throws MessagingException {
            setHeader("Message-ID", getMessageID());
        }

        public void send(final Email email) throws MessagingException {
            if (email.getFromName() == null) {
                logProblem("error.from.address.cannot.be.null");
                abort();
                return;
            }

            final String from = constructFromString(encode(email.getFromName()), email.getFromAddress());

            final String[] replyTos = email.replyTos();
            final Address[] replyToAddresses = new Address[replyTos == null ? 0 : replyTos.length];
            if (replyTos != null) {
                for (int i = 0; i < replyTos.length; i++) {
                    try {
                        replyToAddresses[i] = new InternetAddress(encode(replyTos[i]));
                    } catch (final AddressException e) {
                        logProblem("invalid.reply.to.address: " + replyTos[i]);
                        abort();
                        return;
                    }
                }
            }

            setFrom(new InternetAddress(from));
            setSubject(encode(email.getSubject()));
            setReplyTo(replyToAddresses);

            final MimeMultipart mimeMultipart = new MimeMultipart();

            final String htmlBody = getHtmlBody();
            if (htmlBody != null && !htmlBody.trim().isEmpty()) {
                final BodyPart bodyPart = new MimeBodyPart();
                bodyPart.setContent(htmlBody, "text/html; charset='utf-8'");
                mimeMultipart.addBodyPart(bodyPart);
            }

            final String body = email.getBody();
            if (body != null && !body.trim().isEmpty()) {
                final BodyPart bodyPart = new MimeBodyPart();
                bodyPart.setText(body);
                mimeMultipart.addBodyPart(bodyPart);
            }

            setContent(mimeMultipart);

            addRecipientsAux();

            new MessageId(getEmail(), getMessageID());

            Transport.send(this);

            final Address[] allRecipients = getAllRecipients();
            setConfirmedAddresses(allRecipients);
        }

        private void addRecipientsAux() {
            boolean hasAnyToOrCC = false;
            if (hasAnyRecipients(getToAddresses())) {
                final EmailAddressList tos = getToAddresses();
                final EmailAddressList remainder = addRecipientsAux(RecipientType.TO, tos);
                setToAddresses(remainder);
                hasAnyToOrCC = true;
            }
            if (hasAnyRecipients(getCcAddresses())) {
                final EmailAddressList ccs = getCcAddresses();
                final EmailAddressList remainder = addRecipientsAux(RecipientType.CC, ccs);
                setCcAddresses(remainder);
                hasAnyToOrCC = true;
            }
            if (!hasAnyToOrCC && hasAnyRecipients(getBccAddresses())) {
                final EmailAddressList bccs = getBccAddresses();
                final EmailAddressList remainder = addRecipientsAux(RecipientType.BCC, bccs);
                setBccAddresses(remainder);
            }
        }

        private EmailAddressList addRecipientsAux(final javax.mail.Message.RecipientType recipientType,
                final EmailAddressList emailAddressList) {
            final String[] emailAddresses = emailAddressList.toArray();
            for (int i = 0; i < emailAddresses.length; i++) {
                final String emailAddress = emailAddresses[i];
                try {
                    if (emailAddressFormatIsValid(emailAddress)) {
                        addRecipient(recipientType, new InternetAddress(encode(emailAddress)));
                    } else {
                        logProblem("invalid.email.address.format: " + emailAddress);
                    }
                } catch (final AddressException e) {
                    logProblem(e.getMessage() + " " + emailAddress);
                } catch (final MessagingException e) {
                    logProblem(e.getMessage() + " " + emailAddress);
                }
                if (i == MAX_MAIL_RECIPIENTS && i + 1 < emailAddresses.length) {
                    final String all = emailAddressList.toString();
                    final int next = all.indexOf(emailAddress) + emailAddress.length() + 2;
                    return new EmailAddressList(all.substring(next));
                }
            }
            return null;
        }

        public boolean emailAddressFormatIsValid(String emailAddress) {
            if ((emailAddress == null) || (emailAddress.length() == 0)) {
                return false;
            }

            if (emailAddress.indexOf(' ') > 0) {
                return false;
            }

            String[] atSplit = emailAddress.split("@");
            if (atSplit.length != 2) {
                return false;
            } else if ((atSplit[0].length() == 0) || (atSplit[1].length() == 0)) {
                return false;
            }

            String domain = new String(atSplit[1]);

            if (domain.lastIndexOf('.') == (domain.length() - 1)) {
                return false;
            }

            if (domain.indexOf('.') <= 0) {
                return false;
            }

            return true;
        }
    }

    private Email getEmail() {
        return this;
    }

    private void setConfirmedAddresses(final Address[] recipients) {
        final Collection<String> addresses = new HashSet<String>();
        final EmailAddressList confirmedAddresses = getConfirmedAddresses();
        if (confirmedAddresses != null && !confirmedAddresses.isEmpty()) {
            addresses.addAll(confirmedAddresses.toCollection());
        }
        if (recipients != null) {
            for (final Address address : recipients) {
                addresses.add(address.toString());
            }
        }
        setConfirmedAddresses(new EmailAddressList(addresses));
    }

    private void setFailedAddresses(final Address[] recipients) {
        final Collection<String> addresses = new HashSet<String>();
        final EmailAddressList failedAddresses = getFailedAddresses();
        if (failedAddresses != null && !failedAddresses.isEmpty()) {
            addresses.addAll(failedAddresses.toCollection());
        }
        if (recipients != null) {
            for (final Address address : recipients) {
                addresses.add(address.toString());
            }
        }
        setFailedAddresses(new EmailAddressList(addresses));
    }

    private void resend(final Address[] recipients) {
//	final Collection<String> addresses = new HashSet<String>();
//	final EmailAddressList bccAddresses = getBccAddresses();
//	if (bccAddresses != null && !bccAddresses.isEmpty()) {
//	    addresses.addAll(bccAddresses.toCollection());
//	}
        if (recipients != null) {
            for (final Address address : recipients) {
                final String[] replyTos = getReplyTos() == null ? null : getReplyTos().toArray();
                final Email email =
                        new Email(getFromName(), getFromAddress(), replyTos, Collections.EMPTY_SET, Collections.EMPTY_SET,
                                Collections.singleton(address.toString()), getSubject(), getBody());
// TODO : it will be resent but we can no longer connect it to the message...
//		email.setMessage(getMessage());
                //addresses.add(address.toString());
            }
        }
//	setBccAddresses(new EmailAddressList(addresses));
    }

    public void deliver() {
//	if ((getMessage() != null) && getMessage().getCreated().plusDays(5).isBeforeNow()) {
//	    setEmailNotifier(null);
//	} else {
        final EmailAddressList toAddresses = getToAddresses();
        final EmailAddressList ccAddresses = getCcAddresses();
        final EmailAddressList bccAddresses = getBccAddresses();

        final EmailMimeMessage emailMimeMessage = new EmailMimeMessage();
        try {
            emailMimeMessage.send(this);
        } catch (final SendFailedException e) {
            logProblem(e);

            final Address[] invalidAddresses = e.getInvalidAddresses();
            setFailedAddresses(invalidAddresses);
            final Address[] validSentAddresses = e.getValidSentAddresses();
            setConfirmedAddresses(validSentAddresses);
            final Address[] validUnsentAddresses = e.getValidUnsentAddresses();
            resend(validUnsentAddresses);
        } catch (final MessagingException e) {
            logProblem(e);
//	    	abort();
            retry(toAddresses, ccAddresses, bccAddresses);
        }

        if (!hasAnyRecipients()) {
            setEmailNotifier(null);
        }
//	}
    }

    private boolean hasAnyRecipients() {
        return hasAnyRecipients(getToAddresses()) || hasAnyRecipients(getCcAddresses()) || hasAnyRecipients(getBccAddresses());
    }

    private boolean hasAnyRecipients(final EmailAddressList emailAddressList) {
        return emailAddressList != null && !emailAddressList.isEmpty();
    }

    @Deprecated
    public java.util.Set<pt.ist.emailNotifier.domain.MessageId> getMessageIds() {
        return getMessageIdsSet();
    }

    @Deprecated
    public java.util.Set<pt.ist.emailNotifier.domain.MessageTransportResult> getMessageTransportResult() {
        return getMessageTransportResultSet();
    }

}
