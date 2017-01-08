package rs.acs.uns.sw.e2e.util;


import rs.acs.uns.sw.sct.util.Constants;

import javax.mail.*;
import java.io.IOException;
import java.util.Properties;

public class MailUtil {

    public static String getMailLink() throws MessagingException, IOException {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", rs.acs.uns.sw.sct.util.Constants.MailParameters.HOST_NAME);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", rs.acs.uns.sw.sct.util.Constants.MailParameters.HOST_PORT);

        Session session = Session.getDefaultInstance(properties, null);
        Store store = session.getStore("imaps");
        store.connect(Constants.MailParameters.HOST_NAME, Constants.MailParameters.AUTH_USER, Constants.MailParameters.AUTH_PASS);

        Folder sentMails = store.getFolder("[Gmail]/Sent Mail");
        sentMails.open(Folder.READ_WRITE);

        Message msg = sentMails.getMessage(sentMails.getMessageCount());
        String content = (String) msg.getContent();

        final int start = content.indexOf("href");
        final int end = content.indexOf("\">Potvrdi");
        return content.substring(start + 6, end);
    }
}
