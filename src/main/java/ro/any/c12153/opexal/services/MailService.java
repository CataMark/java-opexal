package ro.any.c12153.opexal.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.services.SendGridServ;
import ro.any.c12153.shared.services.SendGridServ.MailMessageType;

/**
 *
 * @author C12153
 */
public class MailService {
    private static final Logger LOG = Logger.getLogger(MailService.class.getName());
    
    private static List<String> recipients(Optional<String> toAddress) throws Exception{
        if (toAddress.isPresent()){
            return Arrays.asList(new String[]{toAddress.get(), App.getProperty("default_mail_recipient")});
        } else {
            return Arrays.asList(new String[]{App.getProperty("default_mail_recipient")});
        }
    }
    
    private static List<String> recipients(List<String> toAddress) throws Exception{
        List<String> rezultat = new ArrayList<>();
        rezultat.addAll(toAddress);
        rezultat.add(App.getProperty("default_mail_recipient"));
        return rezultat;                
    }
    
    public static void sendHtmlError(Optional<String> toAddress, String subject, String body, Optional<String> language, String userId){
        try {
            SendGridServ.send(
                    MailMessageType.HTML,
                    "mail/mail_html_template_error_"+ language.orElse("ro") + ".html",
                    recipients(toAddress),
                    subject,
                    body,
                    userId);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, userId, ex);
        }        
    }
    
    public static void sendHtmlInfo(Optional<String> toAddress, String subject, String body, Optional<String> language, String userId){
        try {
            SendGridServ.send(
                    MailMessageType.HTML,
                    "mail/mail_html_template_info_"+ language.orElse("ro") + ".html",
                    recipients(toAddress),
                    subject,
                    body,
                    userId);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, userId, ex);
        }        
    }
    
    public static void sendHtmlError(List<String> toAddress, String subject, String body, Optional<String> language, String userId){
        try {
            SendGridServ.send(
                    MailMessageType.HTML,
                    "mail/mail_html_template_error_"+ language.orElse("ro") + ".html",
                    recipients(toAddress),
                    subject,
                    body,
                    userId);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, userId, ex);
        }        
    }
    
    public static void sendHtmlInfo(List<String> toAddress, String subject, String body, Optional<String> language, String userId){
        try {
            SendGridServ.send(
                    MailMessageType.HTML,
                    "mail/mail_html_template_info_"+ language.orElse("ro") + ".html",
                    recipients(toAddress),
                    subject,
                    body,
                    userId);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, userId, ex);
        }        
    }
}
