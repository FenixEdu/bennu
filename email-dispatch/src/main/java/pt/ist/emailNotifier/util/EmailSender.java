package pt.ist.emailNotifier.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
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
import javax.mail.util.ByteArrayDataSource;

import pt.ist.bennu.core.util.ConfigurationManager;

import com.google.common.base.Joiner;

public class EmailSender {

    public static class EmailSendResult {
        final Collection<String> unsentAddresses = new ArrayList<String>(0);
        final SortedSet<String> messageIDs = new TreeSet<String>();

        public Collection<String> getUnsentAddresses() {
            return unsentAddresses;
        }

        public SortedSet<String> getMessageIDs() {
            return messageIDs;
        }
    }

    private static final int MAX_MAIL_RECIPIENTS;

    private static final Session session;
    static {
        final Properties properties = new Properties();
        properties.put("mail.smtp.host", ConfigurationManager.getProperty("mail.smtp.host"));
        properties.put("mail.smtp.name", ConfigurationManager.getProperty("mail.smtp.name"));
        properties.put("mailSender.max.recipients", ConfigurationManager.getProperty("mailSender.max.recipients"));
        properties.put("mailingList.host.name", ConfigurationManager.getProperty("mailingList.host.name"));
        session = Session.getDefaultInstance(properties, null);
        for (final Entry<Object, Object> entry : session.getProperties().entrySet()) {
            System.out.println("key: " + entry.getKey() + "   value: " + entry.getValue());
        }
        MAX_MAIL_RECIPIENTS = Integer.parseInt(properties.getProperty("mailSender.max.recipients"));
    }

    public static Collection<String> forward(final MimeMessage message, final List<String> bccAddressesToforward) {
        if (message == null) {
            throw new NullPointerException("error.message.cannot.be.null");
        }

        final ArrayList<String> unsent = new ArrayList<String>(0);

        final List<String> bccAddressesList = new ArrayList<String>(new HashSet<String>(bccAddressesToforward));
        for (int i = 0; i < bccAddressesList.size(); i += MAX_MAIL_RECIPIENTS) {
            final List<String> subList = bccAddressesList.subList(i, Math.min(bccAddressesList.size(), i + MAX_MAIL_RECIPIENTS));
            try {
                MimeMessage newMessage = new MimeMessage(message);
                newMessage.setRecipients(javax.mail.internet.MimeMessage.RecipientType.TO, new InternetAddress[] {});
                newMessage.setRecipients(javax.mail.internet.MimeMessage.RecipientType.CC, new InternetAddress[] {});
                newMessage.setRecipients(javax.mail.internet.MimeMessage.RecipientType.BCC, new InternetAddress[] {});
                addRecipients(newMessage, Message.RecipientType.BCC, subList, unsent);
                Transport.send(newMessage);
            } catch (SendFailedException e) {
                registerInvalidAddresses(unsent, e, null, null, subList);
            } catch (MessagingException e) {
                if (subList != null) {
                    unsent.addAll(subList);
                }

                e.printStackTrace();
            }
        }

        return unsent;
    }

    private static String encode(final String string) {
        try {
            return string == null ? "" : MimeUtility.encodeText(string);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            return string;
        }
    }

    public static EmailSendResult send(final String fromName, final String fromAddress, final String[] replyTos,
            final Collection<String> toAddresses, final Collection<String> ccAddresses, final Collection<String> bccAddresses,
            final String subject, final String body) {
        return send(fromName, fromAddress, replyTos, toAddresses, ccAddresses, bccAddresses, subject, body, new byte[0][0],
                new String[0], new String[0]);
    }

    public static EmailSendResult send(final String fromName, final String fromAddress, final String[] replyTos,
            final Collection<String> toAddresses, final Collection<String> ccAddresses, final Collection<String> bccAddresses,
            final String subject, final String body, final byte[][] attachments, final String[] attachmentFilenames,
            final String[] attachmentTypes) {

        if (fromAddress == null) {
            throw new NullPointerException("error.from.address.cannot.be.null");
        }

        final EmailSendResult emailSendResult = new EmailSendResult();

        final String from = constructFromString(encode(fromName), fromAddress);
        final boolean hasToAddresses = (toAddresses != null && !toAddresses.isEmpty()) ? true : false;
        final boolean hasCCAddresses = (ccAddresses != null && !ccAddresses.isEmpty()) ? true : false;

        final Address[] replyToAddresses = new Address[replyTos == null ? 0 : replyTos.length];
        if (replyTos != null) {
            for (int i = 0; i < replyTos.length; i++) {
                try {
                    replyToAddresses[i] = new InternetAddress(encode(replyTos[i]));
                } catch (AddressException e) {
                    throw new Error("invalid.reply.to.address: " + replyTos[i]);
                }
            }
        }

        if (hasToAddresses || hasCCAddresses) {
            try {
                final MimeMessage mimeMessageTo = new MimeMessage(session);
                mimeMessageTo.setFrom(new InternetAddress(from));
                mimeMessageTo.setSubject(encode(subject));
                mimeMessageTo.setReplyTo(replyToAddresses);

                final MimeMultipart mimeMultipart = new MimeMultipart();

                final BodyPart bodyPart = new MimeBodyPart();
                bodyPart.setText(body);
                mimeMultipart.addBodyPart(bodyPart);

                addAttachments(mimeMultipart, attachments, attachmentFilenames, attachmentTypes);

                mimeMessageTo.setContent(mimeMultipart);

                if (hasToAddresses) {
                    addRecipients(mimeMessageTo, Message.RecipientType.TO, toAddresses, emailSendResult.unsentAddresses);
                }

                if (hasCCAddresses) {
                    addRecipients(mimeMessageTo, Message.RecipientType.CC, ccAddresses, emailSendResult.unsentAddresses);
                }

                Transport.send(mimeMessageTo);
                System.out.println(mimeMessageTo.getMessageID());
                emailSendResult.messageIDs.add(mimeMessageTo.getMessageID());
            } catch (SendFailedException e) {
                registerInvalidAddresses(emailSendResult.unsentAddresses, e, toAddresses, ccAddresses, null);
            } catch (MessagingException e) {
                if (toAddresses != null) {
                    emailSendResult.unsentAddresses.addAll(toAddresses);
                }

                if (ccAddresses != null) {
                    emailSendResult.unsentAddresses.addAll(ccAddresses);
                }

                e.printStackTrace();
            }
        }

        if (bccAddresses != null && !bccAddresses.isEmpty()) {
            final List<String> bccAddressesList = new ArrayList<String>(new HashSet<String>(bccAddresses));
            for (int i = 0; i < bccAddressesList.size(); i += MAX_MAIL_RECIPIENTS) {
                List<String> subList = null;
                try {
                    subList = bccAddressesList.subList(i, Math.min(bccAddressesList.size(), i + MAX_MAIL_RECIPIENTS));
                    final MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(from));
                    message.setSubject(encode(subject));
                    message.setReplyTo(replyToAddresses);

                    final MimeMultipart mimeMultipart = new MimeMultipart();
                    final BodyPart bodyPart = new MimeBodyPart();
                    bodyPart.setText(body);
                    mimeMultipart.addBodyPart(bodyPart);

                    addAttachments(mimeMultipart, attachments, attachmentFilenames, attachmentTypes);

                    message.setContent(mimeMultipart);

                    addRecipients(message, Message.RecipientType.BCC, subList, emailSendResult.unsentAddresses);

                    Transport.send(message);
                    System.out.println(message.getMessageID());
                    emailSendResult.messageIDs.add(message.getMessageID());
                } catch (SendFailedException e) {
                    registerInvalidAddresses(emailSendResult.unsentAddresses, e, null, null, subList);
                } catch (MessagingException e) {
                    if (subList != null) {
                        emailSendResult.unsentAddresses.addAll(subList);
                    }

                    e.printStackTrace();
                }
            }
        }

        return emailSendResult;
    }

    private static void addAttachments(final MimeMultipart mimeMultipart, final byte[][] attachments,
            final String[] attachmentFilenames, final String[] attachmentTypes) throws MessagingException {
        for (int a = 0; a < attachments.length; a++) {
            final byte[] attachment = attachments[a];
            final String attachmentFilename = attachmentFilenames[a];
            final String attachmentType = attachmentTypes[a];

            final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            final DataSource source = new ByteArrayDataSource(attachment, attachmentType);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(attachmentFilename);
            mimeMultipart.addBodyPart(attachmentBodyPart);
        }
    }

    protected static void registerInvalidAddresses(final Collection<String> unsentAddresses, final SendFailedException e,
            final Collection<String> toAddresses, final Collection<String> ccAddresses, final Collection<String> bccAddresses) {
        e.printStackTrace();
        if (e.getValidUnsentAddresses() != null) {
            for (int i = 0; i < e.getValidUnsentAddresses().length; i++) {
                unsentAddresses.add(e.getValidUnsentAddresses()[i].toString());
            }
        } else {
            if (e.getValidSentAddresses() == null || e.getValidSentAddresses().length == 0) {
                if (toAddresses != null) {
                    unsentAddresses.addAll(toAddresses);
                }
                if (ccAddresses != null) {
                    unsentAddresses.addAll(ccAddresses);
                }
                if (bccAddresses != null) {
                    unsentAddresses.addAll(bccAddresses);
                }
            }
        }
    }

    protected static String constructFromString(final String fromName, String fromAddress) {
        if (fromName == null || fromName.length() == 0) {
            return fromAddress;
        }
        return Joiner.on("").join(fromName.replace(',', ' '), " <", fromAddress, ">");
    }

    protected static void addRecipients(final Message mensagem, final RecipientType recipientType,
            final Collection<String> emailAddresses, Collection<String> unsentMails) throws MessagingException {
        if (emailAddresses != null) {
            for (final String emailAddress : emailAddresses) {
                try {
                    if (emailAddressFormatIsValid(emailAddress)) {
                        System.out.println("Sending to: " + emailAddress);
                        mensagem.addRecipient(recipientType, new InternetAddress(encode(emailAddress)));
                    } else {
                        System.out.println("skipped: " + emailAddress);
                        unsentMails.add(emailAddress);
                    }
                } catch (AddressException e) {
                    System.out.println("skipped due to address exception: " + emailAddress);
                    unsentMails.add(emailAddress);
                }
            }
        }
    }

    public static boolean emailAddressFormatIsValid(String emailAddress) {
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
