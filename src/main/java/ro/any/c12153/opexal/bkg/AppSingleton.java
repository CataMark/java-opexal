package ro.any.c12153.opexal.bkg;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.Singleton;
import ro.any.c12153.opexal.entities.JobSwitch;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.services.JobSwitchServ;
import ro.any.c12153.opexal.services.MailService;
import ro.any.c12153.shared.App;

/**
 *
 * @author catalin
 */
@Startup
@Singleton
public class AppSingleton implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AppSingleton.class.getName());
    
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    public static final Map<String, String> FLAG_MODEL = new ConcurrentHashMap<>();
    public static final Map<String, String> FLAG_PROCESS = new ConcurrentHashMap<>();
    public static final String RPA_USER = "RPA";
    public static final String SYS_USER = "SYSTEM";
    public static final boolean CHILD_APP = Boolean.parseBoolean(Optional.ofNullable(System.getenv("CHILD_APP")).orElse("false"));
    
    @PostConstruct
    private void init(){
        System.out.println("Scheduling singleton initialised");
    }
    
    public static String getArffFilePath(String coarea) throws Exception{
        return App.getProperty("weka_folder").concat(coarea).concat("_training.arff");
    }
    
    public static String getModelFilePath(String coarea, Perioada startPerioada) throws Exception{
        return App.getProperty("weka_folder").concat(coarea).concat("_")
                    .concat(startPerioada.getAn().toString() + (startPerioada.getLuna() < 10 ? "_0" : "_") + startPerioada.getLuna().toString())
                    .concat(".model");
    }
    
    public static String getRpaHomeDir() throws Exception{
        return App.getProperty("rpa_folder");
    }
    
    private static CompletableFuture<Void> createModel(){
        return CompletableFuture.runAsync(() -> {
            try{
                JobSwitch status = JobSwitchServ.getByCod(JobSwitch.JOB_CLASSIFCATION_MODEL, SYS_USER)
                    .orElseThrow(() -> new Exception("JOB_STATUS_NOT_FOUND: " + JobSwitch.JOB_CLASSIFCATION_MODEL));
                if (status.getBlocat() == null || status.getBlocat().equals(Boolean.TRUE)) return;

                MailService.sendHtmlInfo(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_CLASSIFCATION_MODEL,
                    "Job started",
                    Optional.of("en"),
                    SYS_USER
                );

                Jobs.createTrainingModel();

                MailService.sendHtmlInfo(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_CLASSIFCATION_MODEL,
                    "Job finished",
                    Optional.of("en"),
                    SYS_USER
                );

            } catch (Exception ex){
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }                
        });
    }
    
    @Schedule(dayOfWeek = "Sun", hour = "12", minute = "0", second = "0", persistent = false)
    public void doCreateModel(){
        CompletableFuture.runAsync(() -> {
            try {
                createModel().get(20, TimeUnit.HOURS);
            } catch (Exception ex) {
                MailService.sendHtmlError(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_CLASSIFCATION_MODEL,
                    ex.getMessage(),
                    Optional.of("en"),
                    SYS_USER
                );
                App.log(LOG, Level.SEVERE, SYS_USER, ex);
            }
        });
    }
    
    private static CompletableFuture<Void> deleteRPAfiles(){
        return CompletableFuture.runAsync(() -> {
            try {
                JobSwitch status = JobSwitchServ.getByCod(JobSwitch.JOB_DELETE_OLD_RPA_FILES, SYS_USER)
                        .orElseThrow(() -> new Exception("JOB_STATUS_NOT_FOUND: " + JobSwitch.JOB_DELETE_OLD_RPA_FILES));
                if (status.getBlocat() == null || status.getBlocat().equals(Boolean.TRUE)) return;

                MailService.sendHtmlInfo(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_DELETE_OLD_RPA_FILES,
                    "Job started",
                    Optional.of("en"),
                    SYS_USER
                );

                Jobs.deleteOldRPAfiles();

                MailService.sendHtmlInfo(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_DELETE_OLD_RPA_FILES,
                    "Job finished",
                    Optional.of("en"),
                    SYS_USER
                );

            } catch (Exception ex) {
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }
        });
    }
    
    @Schedule(dayOfWeek = "Mon-Fri", hour = "20", minute = "0", second = "0", persistent = false)
    public void doDeleteRPAFiles(){
        CompletableFuture.runAsync(() -> {
            try {
                deleteRPAfiles().get(2, TimeUnit.HOURS);
            } catch (Exception ex) {
                MailService.sendHtmlError(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_DELETE_OLD_RPA_FILES,
                    ex.getMessage(),
                    Optional.of("en"),
                    SYS_USER
                );
                App.log(LOG, Level.SEVERE, SYS_USER, ex);
            }
        });
    }
    
    private static CompletableFuture<Void> openPeriod(){
        return CompletableFuture.runAsync(() -> {
            try {
                JobSwitch status = JobSwitchServ.getByCod(JobSwitch.JOB_OPEN_CURRENT_PERIOD, SYS_USER)
                        .orElseThrow(() -> new Exception("JOB_STATUS_NOT_FOUND: " + JobSwitch.JOB_OPEN_CURRENT_PERIOD));
                if (status.getBlocat() == null || status.getBlocat().equals(Boolean.TRUE)) return;

                MailService.sendHtmlInfo(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_OPEN_CURRENT_PERIOD,
                    "Job started",
                    Optional.of("en"),
                    SYS_USER
                );
                
                Jobs.openCurrentPeriod();
                
                MailService.sendHtmlInfo(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_OPEN_CURRENT_PERIOD,
                    "Job finished",
                    Optional.of("en"),
                    SYS_USER
                );
                
            } catch (Exception ex) {
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }
        });
    }
    
    @Schedule(dayOfWeek = "*", hour = "0", minute = "1", second = "0", persistent = false)
    public void doOpenPeriod(){
        CompletableFuture.runAsync(() -> {
            try {
                openPeriod().get(30, TimeUnit.SECONDS);
            } catch (Exception ex) {
                MailService.sendHtmlError(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_OPEN_CURRENT_PERIOD,
                    ex.getMessage(),
                    Optional.of("en"),
                    SYS_USER
                );
                App.log(LOG, Level.SEVERE, SYS_USER, ex);
            }
        });
    }
    
    private static CompletableFuture<Void> checkPeriod(){
        return CompletableFuture.runAsync(() -> {
            try {
                JobSwitch status = JobSwitchServ.getByCod(JobSwitch.JOB_CHECK_OPENED_PERIODS, SYS_USER)
                        .orElseThrow(() -> new Exception("JOB_STATUS_NOT_FOUND: " + JobSwitch.JOB_CHECK_OPENED_PERIODS));
                if (status.getBlocat() == null || status.getBlocat().equals(Boolean.TRUE)) return;
                
                MailService.sendHtmlInfo(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_CHECK_OPENED_PERIODS,
                    "Job started",
                    Optional.of("en"),
                    SYS_USER
                );
                
                Jobs.checkOpenedPeriods();
                
                MailService.sendHtmlInfo(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_CHECK_OPENED_PERIODS,
                    "Job finished",
                    Optional.of("en"),
                    SYS_USER
                );
                
            } catch (Exception ex) {
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }
        });
    }
    
    @Schedule(dayOfWeek = "Mon-Fri", hour = "23", minute = "0", second = "0", persistent = false)
    public void doCheckPeriod(){
        CompletableFuture.runAsync(() -> {
            try {
                checkPeriod().get(60, TimeUnit.SECONDS);
            } catch (Exception ex) {
                MailService.sendHtmlError(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_CHECK_OPENED_PERIODS,
                    ex.getMessage(),
                    Optional.of("en"),
                    SYS_USER
                );
                App.log(LOG, Level.SEVERE, SYS_USER, ex);
            }
        });
    }
}
