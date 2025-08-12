import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {

    private static final String FROM_EMAIL = "sluteam2025@gmail.com";
    private static final String FROM_PASSWORD = "ysxg slcl gjah osvo"; 

    public static void sendEmail(String recipientEmail, String userName, String subject, String messsage) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText("Hello " + userName + ",\n\n" + messsage + "\n\n— SLU Management System");
          

            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public static void sendEmailBulk(java.util.List<String> recipientEmails, String subject, String message) {
        if (recipientEmails == null || recipientEmails.isEmpty()) return;

        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        try {
            Message mime = new MimeMessage(session);
            mime.setFrom(new InternetAddress(FROM_EMAIL, "SLU Management System"));
            mime.setRecipients(Message.RecipientType.TO, InternetAddress.parse(FROM_EMAIL));
            mime.setSubject(subject);

            String body = (message == null ? "" : message.trim()) + "\n\n— SLU Management System";
            mime.setText(body);

            javax.mail.internet.InternetAddress[] bcc = recipientEmails.stream()
                    .map(addr -> {
                        try { return new InternetAddress(addr); }
                        catch (Exception ignored) { return null; }
                    })
                    .filter(a -> a != null)
                    .toArray(InternetAddress[]::new);
            mime.setRecipients(Message.RecipientType.BCC, bcc);

            Transport.send(mime);
            System.out.println("Bulk email sent to " + bcc.length + " recipients.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Bulk email failed: " + e.getMessage(), e);
        }
    }
}