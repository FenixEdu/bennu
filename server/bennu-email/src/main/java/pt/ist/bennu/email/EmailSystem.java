/*
 * EmailSystem.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-email.
 *
 * bennu-email is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-email is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-email.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.email;

import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.util.ConfigurationManager;

import com.google.common.base.Charsets;

public class EmailSystem extends ConfigurationManager {
    private static Logger logger = LoggerFactory.getLogger(EmailSystem.class);

    public static Session emailSession() {
        final Properties properties = new Properties();

        properties.put("mail.smtp.auth", getProperty("mail.smtp.auth"));
        properties.put("mail.smtp.starttls.enable", getProperty("mail.smtp.starttls.enable"));
        properties.put("mail.smtp.host", getProperty("mail.smtp.host"));
        properties.put("mail.smtp.name", getProperty("mail.smtp.name"));
        properties.put("mail.smtp.port", getProperty("mail.smtp.port"));

        properties.put("mailSender.max.recipients", getProperty("mailSender.max.recipients"));
        properties.put("mailingList.host.name", getProperty("mailingList.host.name"));
        if (getBooleanProperty("mail.smtp.auth", false)) {
            return Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(getProperty("mail.smtp.auth.username"),
                            getProperty("mail.smtp.auth.password"));
                }
            });
        }
        return Session.getDefaultInstance(properties);
    }

    public static MimeMessage message(Session session, InternetAddress from, List<InternetAddress> to, List<InternetAddress> cc,
            List<InternetAddress> bcc, String subject, Multipart content) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(from);
        if (to != null) {
            for (InternetAddress address : to) {
                message.addRecipient(RecipientType.TO, address);
            }
        }
        if (cc != null) {
            for (InternetAddress address : cc) {
                message.addRecipient(RecipientType.CC, address);
            }
        }
        if (bcc != null) {
            for (InternetAddress address : bcc) {
                message.addRecipient(RecipientType.BCC, address);
            }
        }
        message.setSubject(subject, Charsets.UTF_8.name());
        message.setContent(content);
        return message;
    }

    public static MimeMessage message(Session session, InternetAddress from, List<InternetAddress> to, List<InternetAddress> cc,
            List<InternetAddress> bcc, String subject, BodyPart... parts) throws MessagingException {
        MimeMultipart content = new MimeMultipart();
        for (BodyPart part : parts) {
            content.addBodyPart(part);
        }
        return message(session, from, to, cc, bcc, subject, content);
    }

    public static void send(MimeMessage message) throws MessagingException {
        Transport.send(message);
        logger.debug("Successfully sent message: {}", message.getMessageID());
    }
}
