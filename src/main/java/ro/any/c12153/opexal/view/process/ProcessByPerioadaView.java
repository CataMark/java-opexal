package ro.any.c12153.opexal.view.process;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexal.entities.CoArea;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.services.CoAreaServ;
import ro.any.c12153.opexal.services.MailService;
import ro.any.c12153.opexal.services.PerioadaServ;
import ro.any.c12153.opexal.services.ProcessServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author catalin
 */
@Named(value = "prssper")
@ViewScoped
public class ProcessByPerioadaView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProcessByPerioadaView.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private String initError;
    private CoArea coarea;
    private Perioada perioada;
    private String finishScript;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String pd = Optional.ofNullable(params.get("pd"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.perioada.not", clocale)));
            String co = Optional.ofNullable(params.get("co"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.perioada = PerioadaServ.getById(Utils.paramDecode(pd), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("title.perioada.not", clocale)));
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(co), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    })
            ).get(30, TimeUnit.SECONDS);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            if (this.perioada != null){ 
                rezultat += "&an=" + Utils.paramEncode(this.perioada.getAn().toString());
                rezultat += "&pd=" + Utils.paramEncode(this.perioada.getId());
            }
            if (this.coarea != null) rezultat += "&co=" + Utils.paramEncode(this.coarea.getCod());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void clear(){
        this.dialog.clear();
        this.finishScript = null;
    }
    
    public static CompletableFuture<Void> procesare(String coarea, Perioada perioada, String userId){
        return CompletableFuture.runAsync(() -> {
            try {
                ProcessServ.processAllSteps(coarea, Optional.empty(), Optional.of(perioada), userId);
            } catch (Exception ex) {
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }
        });
    }
    
    public void process(){
        try {
            //TODO: throw error if perioada is closed
            
            CompletableFuture.runAsync(() -> {
                try {
                    procesare(this.coarea.getCod(), this.perioada, cuser.getUname())
                            .get(30, TimeUnit.MINUTES);
                    
                    MailService.sendHtmlInfo(
                            Optional.of(cuser.getEmail()),
                            App.getBeanMess("title.process", clocale),
                            App.getBeanMess("info.process.end",  clocale),
                            Optional.of(clocale.getLanguage()),
                            cuser.getUname()
                    );
                } catch (Exception ex) {
                    MailService.sendHtmlError(
                        Optional.of(cuser.getEmail()),
                        App.getBeanMess("title.process", clocale),
                        ex.getMessage(),
                        Optional.of(clocale.getLanguage()),
                        cuser.getUname()
                    );
                    ex.printStackTrace();
                }
            });
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.process", clocale), App.getBeanMess("info.process.start", clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.process", clocale), ex.getMessage()));
        }
    }

    public String getInitError() {
        return initError;
    }

    public CoArea getCoarea() {
        return coarea;
    }

    public Perioada getPerioada() {
        return perioada;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
