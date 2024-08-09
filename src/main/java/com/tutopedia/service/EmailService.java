package com.tutopedia.service;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tutopedia.model.TutorialFileData;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailService {
	// @Autowired
	// private JavaMailSender mailSender;

	private String username;
    private String password;
    private Properties prop;

    public void setConnectionParams(String host, int port, String username, String password) {
        prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.ssl.trust", host);

        this.username = username;
        this.password = password;
    }
	
	private Message createMimeMessage(Session session, String from, String to, String subject) throws MessagingException {
		Message mimeMessage = new MimeMessage(session);

		System.out.println("From: " + from);
		System.out.println("To: " + to);
 		System.out.println("Subject: " + subject);
 		
		mimeMessage.setFrom(new InternetAddress(from));
		mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(to));
		mimeMessage.setSubject(subject);
		
		return mimeMessage;
	}

	private Multipart createMimeMultipart(Multipart multipart, String content, String filename, byte[] attachment) throws MessagingException {
		if (multipart == null) {
			multipart = new MimeMultipart();
		}

		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText(content);
		System.out.println("Text: " + content);
		
		MimeBodyPart attachmentPart = new MimeBodyPart();
		DataSource dataSource = new ByteArrayDataSource(attachment, "application/octet-stream");
		attachmentPart.setDataHandler(new DataHandler(dataSource));
		System.out.println("Attachment: " + filename);
		attachmentPart.setFileName(filename);
		
		multipart.addBodyPart(textPart);
		multipart.addBodyPart(attachmentPart);

		return multipart;
	}

	private Session getSession() {
		Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
		
		return session;
	}
	
	public void sendEmail(String from, String to, String tutorialTitle, TutorialFileData fileData) throws MessagingException {
		System.out.println("Send Email");
		System.out.println("Title: " + tutorialTitle + " : " + fileData.getFilename() + " (" + fileData.getTutorial().length + ")");
		
		Session session = getSession();
		
		Message mimeMessage = createMimeMessage(session, from, to, "Publication for " + tutorialTitle);

		Multipart multipart = createMimeMultipart(null, tutorialTitle + " : " + fileData.getFilename(), fileData.getFilename(), fileData.getTutorial());
		
		mimeMessage.setContent(multipart);
		
		Transport.send(mimeMessage);
	}

	public void sendEmail(String from, String to, Map<String, TutorialFileData> filesToSend) throws MessagingException {
		System.out.println("Send Email with multiple files");

		Session session = getSession();

		for (String tutorialTitle : filesToSend.keySet()) {
			TutorialFileData data = filesToSend.get(tutorialTitle);
			
			System.out.println("Title: " + tutorialTitle + " : " + data.getFilename() + " (" + data.getTutorial().length + ")");
		}
		
		String message = filesToSend.keySet().stream().map(s -> {
			String value = filesToSend.get(s).getFilename();
			return "'" + s + "' : '" + value + "'";
		}).collect(Collectors.joining("\n"));

		System.out.println(message);
		
		Message mimeMessage = createMimeMessage(session, from, to, "Different publications");

		Multipart multipart = null;
		
		for (String title : filesToSend.keySet()) {
			multipart = createMimeMultipart(multipart, message, filesToSend.get(title).getFilename(), filesToSend.get(title).getTutorial());
		}

		mimeMessage.setContent(multipart);
		Transport.send(mimeMessage);
	}
}
