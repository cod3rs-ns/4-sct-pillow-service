package rs.acs.uns.sw.sct.util;

import de.neuland.jade4j.Jade4J;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.verification.VerificationToken;
import rs.acs.uns.sw.sct.verification.VerificationTokenService;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Component that is used for sending mail to users.
 */
@Component
public class MailSender {
    @Autowired
    VerificationTokenService verificationTokenService;

    @Autowired
    UserService userService;

    @Autowired
    SimpMessagingTemplate template;

    private Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Sends email to newly registerd users.
     * @param name name of the user
     * @param address email address of the user
     */
    @Async
    public void sendRegistrationMail(String name, String address, String tokenValue){
        Map<String, Object> model = new HashMap<>();
        model.put("name", name);

        try {
            if (tokenValue == null)
                tokenValue = generateToken(address);
            model.put("link", Constants.MailParameters.TOKEN_CONFIRM_LINK + tokenValue);

            // Rendering html page for email
            String html = Jade4J.render("./src/main/resources/templates/mail-template.jade", model);
            sendMail(address, "Potvrda registracije", html);
            template.convertAndSend("/subscribe/email-sent/" + address, "true");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "", e);
        } catch (MessagingException e) {
            logger.log(Level.WARNING, "", e);
        }
    }

    /**
     * Send mail to advertiser upon resolving report of his announcement
     *
     * @param reportContent     content of report attached to announcement
     * @param announcementID    ID of reported announcement
     * @param announcementName  name of the reported announcement
     * @param address           address of the announcement's author
     */
    @Async
    public void sendReportAcceptedMail(String reportContent, Long announcementID, String announcementName, String address){
        Map<String, Object> model = new HashMap<>();
        model.put("reportContent", reportContent);
        model.put("announcementLink", Constants.MailParameters.ANNOUNCEMENT_CLIENT + String.valueOf(announcementID));
        model.put("announcementName", announcementName);

        try{
            // Rendering html page for email
            String html = Jade4J.render("./src/main/resources/templates/report-mail-template.jade", model);
            sendMail(address, "Prijava oglasa", html);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "", e);
        } catch (MessagingException e) {
            logger.log(Level.WARNING, "", e);
        }
    }

    private void sendMail(String address, String subject, String message) throws MessagingException {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setDefaultEncoding("UTF-8");

        Properties properties = new Properties();
        properties.put("mail.smtp.host", Constants.MailParameters.HOST_NAME);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", Constants.MailParameters.HOST_PORT);
        sender.setJavaMailProperties(properties);

        Session mailSession = Session.getDefaultInstance(properties, null);
        MimeMessage mailMessage = new MimeMessage(mailSession);

        final InternetAddress recipient = new InternetAddress(address);

        mailMessage.addRecipient(Message.RecipientType.TO, recipient);
        mailMessage.setSubject(subject);
        mailMessage.setContent(message, "text/html");

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(Constants.MailParameters.HOST_NAME, Constants.MailParameters.AUTH_USER, Constants.MailParameters.AUTH_PASS);
        transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
        transport.close();
    }

    private String generateToken(String userMail){
        // Generate VerificationToken
        Date date = new Date();
        date.setTime(date.getTime() + Constants.MailParameters.TOKEN_EXPIRE_TIME);
        final String tokenValue = UUID.randomUUID().toString();

        VerificationToken token = new VerificationToken(tokenValue, date, userService.getUserByEmail(userMail));
        verificationTokenService.save(token);

        return tokenValue;
    }
}
