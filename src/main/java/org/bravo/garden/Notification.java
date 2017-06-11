package org.bravo.garden;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Notification {

    private final static Logger log = LogManager.getLogger(Notification.class);

    private Properties config;

    public Notification(Properties config) {
        this.config = config;
    }

    public enum Type {EMAIL}

    public void sendNotification(String subject, String text) {
        sendNotification(subject, text, Type.EMAIL);
    }

    public void sendNotification(String subject, String text, Type type)  {
        switch (type) {
            case EMAIL:
                sendEMail(subject, text);
        }
    }

    private void sendEMail(String subject, String text) {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getProperty("notification_email_host"));
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "false");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                config.getProperty("notification_email_username"),
                                config.getProperty("notification_email_password"));
                    }
                });

        try {

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(config.getProperty("notification_email_from")));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(config.getProperty("notification_email_to")));
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setContent(text, "text/plain");
            Transport.send(msg);

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        log.info("Sent email to "+config.getProperty("notification_email_to")+" with subject: "+subject);
    }

}
