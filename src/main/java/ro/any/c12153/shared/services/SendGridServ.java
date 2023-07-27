package ro.any.c12153.shared.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Crypto;

/**
 * Send e-mail messages through SengGrid
 * @author C12153
 */
public class SendGridServ {
    private static final Logger LOG = Logger.getLogger(SendGridServ.class.getName());
    
    private static final String BODY_PLACEHOLDER = "{{mail-body}}";
    @SuppressWarnings("PublicInnerClass")
    public enum MailMessageType{HTML, TEXT}
    
    private static Email from;
    private static String key;
    
    static {
        try {
            from = new Email(App.getProperty("sendgrid_sender_mail"), App.getProperty("sendgrid_sender_mail_name"));
            key = Crypto.decrypt(App.getProperty("sendgrid_token"));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, null, ex);
        }
    }
    
    private static void deliver(Mail mail) throws Exception{
        SendGrid sg = new SendGrid(key);            
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());            

        Response response = sg.api(request);
        if (!Integer.toString(response.getStatusCode()).startsWith("2"))
            throw new Exception("SENDGRID STATUS: " + Integer.toString(response.getStatusCode()));
    }
    
    /**
     * Send HTML or TEXT e-mail message to one recipient
     * @param tip
     * @param templatePath - template's file path inside project structure including file termination. Template file must contain '{{mail-body}}' placeholder for including the 'body' parameter value.
     * @param toAddress - recipient's e-mail address
     * @param subject
     * @param userId
     * @param body
     * @throws java.lang.Exception
     */
    public static void send(MailMessageType tip, String templatePath, String toAddress, String subject, String body, String userId) throws Exception{
        Email to = new Email(toAddress);
        Content content = new Content(
                (tip == MailMessageType.HTML ? "text/html" : "text/plain"),
                App.getLinesFromResource(templatePath).replace(BODY_PLACEHOLDER, body)
        );
        Mail mail = new Mail(from, subject, to, content);
        deliver(mail);
    }
    
    /**
     * Send HTML or TEXT e-mail message to multiple recipients
     * @param tip
     * @param templatePath - template's file path inside project structure including file termination. Template file must contain '{{mail-body}}' placeholder for including the 'body' parameter value.
     * @param toAddress - recipients e-mail addresses
     * @param subject
     * @param body
     * @param userId
     * @throws java.lang.Exception
     */
    public static void send(MailMessageType tip, String templatePath, List<String> toAddress, String subject, String body, String userId) throws Exception{
        Personalization to = new Personalization();
        toAddress.forEach(x -> to.addTo(new Email(x)));
        Content content = new Content(
                (tip == MailMessageType.HTML ? "text/html" : "text/plain"),
                App.getLinesFromResource(templatePath).replace(BODY_PLACEHOLDER, body)
        );
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.addPersonalization(to);
        mail.setSubject(subject);
        mail.addContent(content);            
        deliver(mail);
    }
    
    /**
     * Send text e-mail with no template to one recipient
     * @param toAddress - recipient's e-mail address
     * @param subject
     * @param message
     * @param userId 
     * @throws java.lang.Exception 
     */
    public static void send(String toAddress, String subject, String message, String userId) throws Exception{
        Email to = new Email(toAddress);
        Content content = new Content("text/plain" ,message);
        Mail mail = new Mail(from, subject, to, content);
        deliver(mail);
    }
    
    /**
     * Send text e-mail with no template to multiple recipients
     * @param toAddress - recipients e-mail addresses
     * @param subject
     * @param message
     * @param userId
     * @throws java.lang.Exception
     */
    public static void send(List<String> toAddress, String subject, String message, String userId) throws Exception{
        Personalization to = new Personalization();
        toAddress.forEach(x -> to.addTo(new Email(x)));
        Content content = new Content("text/plain", message);
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.addPersonalization(to);
        mail.setSubject(subject);
        mail.addContent(content);            
        deliver(mail);
    }
}
