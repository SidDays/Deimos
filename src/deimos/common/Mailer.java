package deimos.common;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Contains utility functions to mail the generated output.
 * Uses Gmail's free SMTP server.
 * 
 * References:
 * www.tutorialspoint.com/javamail_api/javamail_api_send_email_with_attachment.htm
 * www.tutorialspoint.com/javamail_api/javamail_api_gmail_smtp_server.htm
 * www.mkyong.com/java/java-properties-file-examples/
 * stackoverflow.com/questions/16115453/javamail-could-not-convert-socket-to-tls-gmail
 * 
 * @author Siddhesh Karekar
 *
 */
public class Mailer {

	public static void mailToDeimosTeam(String subject, String body, String attachfile) {

		String to = DeimosConfig.EMAIL_SEND;
		String from = DeimosConfig.EMAIL_SEND;
		String username = DeimosConfig.EMAIL_SEND_USERNAME;
		String password = DeimosConfig.EMAIL_SEND_PASSWORD;

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		// Get the Session object.
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();
			
			messageBodyPart.setText(body);

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			if(attachfile != null) {
				
				// Part two is attachment
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(attachfile);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(attachfile);
				multipart.addBodyPart(messageBodyPart);

			}
			
			// Send the complete message parts
			message.setContent(multipart);
			

			// Send message
			Transport.send(message);

			System.out.printf("Sent message from %s to %s.\n", to, from);

		}
		catch (MessagingException e) {

			throw new RuntimeException(e);
		}

	}
}
