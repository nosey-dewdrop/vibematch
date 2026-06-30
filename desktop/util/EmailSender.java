package util;

import java.io.FileInputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * Sends the verification code email over SMTP (gmail by default).
 *
 * Reading the credentials from desktop/credentials.properties keeps the
 * password out of the code and out of git. If that file is missing, or sending
 * fails for any reason, sendVerificationCode just returns false and the caller
 * shows the code in a popup instead. That way the app still works for testing
 * even with no email set up.
 */
public class EmailSender {

    private static final String CREDS_FILE = "desktop/credentials.properties";

    public static boolean isConfigured() {
        Properties p = loadCreds();
        if (p == null) {
            return false;
        }
        String sender = p.getProperty("email.sender");
        String pass = p.getProperty("email.password");
        return notBlank(sender) && notBlank(pass);
    }

    // returns true if the mail actually went out, false if we couldnt send it
    public static boolean sendVerificationCode(String toEmail, String code) {
        Properties creds = loadCreds();
        if (creds == null) {
            return false;
        }

        final String sender = creds.getProperty("email.sender");
        final String password = creds.getProperty("email.password");
        String host = creds.getProperty("email.smtp.host", "smtp.gmail.com");
        String port = creds.getProperty("email.smtp.port", "587");

        if (!notBlank(sender) || !notBlank(password)) {
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender, "vibematch"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Your vibematch verification code");
            message.setText("Hey!\n\nYour verification code is: " + code
                    + "\n\nType this in the app to finish signing up.\n\nSee you inside,\nvibematch");
            Transport.send(message);
            return true;
        } catch (Exception e) {
            // dont crash, just let the caller fall back to the popup
            System.out.println("email send failed: " + e.getMessage());
            return false;
        }
    }

    private static Properties loadCreds() {
        FileInputStream in = null;
        try {
            in = new FileInputStream(CREDS_FILE);
            Properties p = new Properties();
            p.load(in);
            return p;
        } catch (Exception e) {
            // file probably just isnt there, thats fine
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ignore) {
            }
        }
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private EmailSender() {
    }
}
