package ro.any.c12153.opexal.rest;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.http.HttpStatus;
import ro.any.c12153.opexal.bkg.AppSingleton;
import ro.any.c12153.opexal.bkg.ProcessSharedFiles;
import ro.any.c12153.opexal.entities.JobSwitch;
import ro.any.c12153.opexal.services.AccountIntervalServ;
import ro.any.c12153.opexal.services.JobSwitchServ;
import ro.any.c12153.opexal.services.MailService;
import ro.any.c12153.opexal.services.PerioadaServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
@Path("/rpa")
public class RestRpa {
    private static final Logger LOG = Logger.getLogger(RestRpa.class.getName());
    
    @Context 
    private ServletContext context;
    
    @GET
    @Path("/accounts")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAccountIntervals(){
        
        StreamingOutput stream = (OutputStream writer) -> {
            try {
                AccountIntervalServ.intervalsToJson(AppSingleton.RPA_USER, writer);
            } catch (Exception ex) {
                App.log(LOG, Level.SEVERE, AppSingleton.RPA_USER, ex);
                throw new RuntimeException(ex.getMessage(), ex.getCause());
            }            
        };        
        return Response.ok(stream)
                .build();
    }
    
    @GET
    @Path("/openedyears/{laData}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getOpenedYears(@PathParam("laData") String laData){
        final String DATE_FORMAT = "yyyymmdd";
        try {
            if (!Utils.stringNotEmpty(laData))
                throw new Exception("DATE_PARAM_NOT_FOUND");
            
            final Date data = new SimpleDateFormat(DATE_FORMAT).parse(laData);
            
            JsonArrayBuilder json = Json.createArrayBuilder();
            PerioadaServ.getOpenedAni(data, null).forEach(x -> json.add(x));            
            return Response.ok(json.build())
                    .build();
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, AppSingleton.RPA_USER, ex);
            return Response.status(HttpStatus.SC_BAD_REQUEST, ex.getMessage())
                    .build();
        }
    }
    
    private static CompletableFuture<Void> callChildApp(ServletContext context, String numeFisier){
        return CompletableFuture.runAsync(()-> {
                HttpURLConnection connection = null;
                try {
                    if (AppSingleton.CHILD_APP) return;

                    String childAppURL = Optional.ofNullable(context.getInitParameter("ro.any.c12153.CHILD_APP_URL"))
                            .orElseThrow(() -> new Exception("CHILD_APP_ADRESS_NOT_FOUND"));
                    URL url = new URL(childAppURL.concat("/ws/rpa/load/").concat(numeFisier));
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.connect();               

                    if (connection.getResponseCode() <= 0 || connection.getResponseCode() > 299 )
                        throw new Exception("CHILD_APP_RESPONSE: " + connection.getResponseMessage());
                } catch (Exception ex) {
                    throw new CompletionException(ex.getMessage(), ex.getCause()); 
                } finally {
                    connection.disconnect();
                }
        });
    }
    
    private static CompletableFuture<Void> processFiles(File logFile){
        return CompletableFuture.runAsync(() -> {
            try {
                JobSwitch status = JobSwitchServ.getByCod(JobSwitch.JOB_PROCESS_RPA_FILES, AppSingleton.RPA_USER)
                    .orElseThrow(() -> new Exception("JOB_STATUS_NOT_FOUND: " + JobSwitch.JOB_PROCESS_RPA_FILES));
                if (status.getBlocat() == null || status.getBlocat().equals(Boolean.TRUE)) return;

                MailService.sendHtmlInfo(
                    Optional.empty(),
                    "JOB: " + JobSwitch.JOB_PROCESS_RPA_FILES,
                    "Job started",
                    Optional.of("en"),
                    AppSingleton.RPA_USER
                );

                ProcessSharedFiles process = new ProcessSharedFiles(logFile, AppSingleton.RPA_USER);
                process.run();

            } catch (Exception ex) {
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }
        });
    }
    
    @GET
    @Path("/load/{numeFisier}")
    public Response loadValues(@PathParam("numeFisier") String numeFisier){
        Entry<Integer, String> rezultat;
        try {          
            File logFile = new File(App.getProperty("rpa_folder")
                        .concat("/").concat(numeFisier).concat(".txt"));
            if (logFile.exists()){
                rezultat = new SimpleEntry<>(HttpStatus.SC_OK, "Log file found");
                
                CompletableFuture.runAsync(() -> {
                    try {
                        CompletableFuture.allOf(callChildApp(context, numeFisier), processFiles(logFile))
                                .get(10, TimeUnit.MINUTES);
                    } catch (Exception ex) {
                        MailService.sendHtmlError(
                            Optional.empty(),
                            "JOB: " + JobSwitch.JOB_PROCESS_RPA_FILES,
                            ex.getMessage(),
                            Optional.of("en"),
                            AppSingleton.RPA_USER
                        );
                        App.log(LOG, Level.SEVERE, AppSingleton.RPA_USER, ex);
                    }
                });
            } else {
                rezultat = new SimpleEntry<>(HttpStatus.SC_UNPROCESSABLE_ENTITY, "File not found");
            }
        } catch (Exception ex) {
            rezultat = new SimpleEntry<>(HttpStatus.SC_UNPROCESSABLE_ENTITY, ex.getMessage());
        }
        return Response.status(rezultat.getKey(), rezultat.getValue()).build();
    }
}