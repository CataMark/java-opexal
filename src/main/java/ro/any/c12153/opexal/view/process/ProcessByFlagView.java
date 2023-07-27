package ro.any.c12153.opexal.view.process;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArray;
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexal.entities.CoArea;
import ro.any.c12153.opexal.entities.Flag;
import ro.any.c12153.opexal.services.CoAreaServ;
import ro.any.c12153.opexal.services.FlagServ;
import ro.any.c12153.opexal.services.MailService;
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
@Named(value = "prssflag")
@ViewScoped
public class ProcessByFlagView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProcessByFlagView.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    
    private String initError;
    private CoArea coarea;
    private List<Flag> list;
    private List<Flag> selected;
    private String finishScript;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String ca = Optional.ofNullable(params.get("ca"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(ca), cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.coarea == null ? "" : "&ca=" + Utils.paramEncode(this.coarea.getCod()));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void clear(){
        this.dialog.clear();
        this.selected = null;
        this.finishScript = null;
    }
    
    public void datainit(){
        try {
            this.list = FlagServ.getByTipAndCoarea(Flag.Tip.UPLOAD, this.coarea.getCod(), cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.flag.listinit", clocale), ex.getMessage()));
        }
    }
    
    private static CompletableFuture<Void> procesare(String coarea, JsonArray uuids, String userId){
        return CompletableFuture.runAsync(() -> {
            try {
                ProcessServ.processAllSteps(coarea, Optional.of(uuids), Optional.empty(), userId);
            } catch (Exception ex) {
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }
        });
    }
    
    public void process(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.flag.nosel", clocale));
            
            CompletableFuture.runAsync(() -> {
                try {
                    procesare(this.coarea.getCod(),
                            Json.createArrayBuilder(this.selected.stream()
                                    .map(Flag::getGuid)
                                    .collect(Collectors.toList()))
                                    .build(),
                            cuser.getUname())
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
            
            this.list.removeAll(this.selected);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.process", clocale), App.getBeanMess("info.process.start", clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.process", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.flag.nosel", clocale));
            
            FlagServ.delete(
                    Json.createArrayBuilder(
                            this.selected.stream()
                                    .map(Flag::getGuid)
                                    .collect(Collectors.toList())
                    ).build(),
                    cuser.getUname());
            this.list.removeAll(this.selected);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.flag.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.flag.del", clocale), ex.getMessage()));
        }
    }

    public String getInitError() {
        return initError;
    }
    
    public CoArea getCoarea() {
        return coarea;
    }

    public List<Flag> getList() {
        return list;
    }

    public List<Flag> getSelected() {
        return selected;
    }

    public void setSelected(List<Flag> selected) {
        this.selected = selected;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
