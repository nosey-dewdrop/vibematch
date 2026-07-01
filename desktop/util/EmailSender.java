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
 * Credentials come from environment variables first (VIBEMATCH_EMAIL_SENDER /
 * VIBEMATCH_EMAIL_PASSWORD / VIBEMATCH_SMTP_HOST / VIBEMATCH_SMTP_PORT), which
 * is how the cloud server should be configured, with no secret sitting on disk.
 * If those arent set we fall back to desktop/credentials.properties for local
 * dev. If neither is present, or sending fails, sendVerificationCode returns
 * false and the caller shows the code in a popup so the app still works.
 */
public class EmailSender {

    private static final String CREDS_FILE = "desktop/credentials.properties";

    public static boolean isConfigured() {
        return notBlank(sender()) && notBlank(password());
    }

    // returns true if the mail actually went out, false if we couldnt send it
    public static boolean sendVerificationCode(String toEmail, String code) {
        final String sender = sender();
        final String password = password();
        String host = host();
        String port = port();

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

    // each setting: environment variable wins, else the properties file, else
    // a sensible default for the smtp bits

    private static String sender() {
        return pick("VIBEMATCH_EMAIL_SENDER", "email.sender", null);
    }

    private static String password() {
        return pick("VIBEMATCH_EMAIL_PASSWORD", "email.password", null);
    }

    private static String host() {
        return pick("VIBEMATCH_SMTP_HOST", "email.smtp.host", "smtp.gmail.com");
    }

    private static String port() {
        return pick("VIBEMATCH_SMTP_PORT", "email.smtp.port", "587");
    }

    private static String pick(String envKey, String fileKey, String fallback) {
        String env = System.getenv(envKey);
        if (notBlank(env)) {
            return env;
        }
        Properties p = loadCreds();
        if (p != null) {
            String v = p.getProperty(fileKey);
            if (notBlank(v)) {
                return v;
            }
        }
        return fallback;
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
