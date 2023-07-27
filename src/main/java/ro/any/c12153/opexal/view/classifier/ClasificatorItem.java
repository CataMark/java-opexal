package ro.any.c12153.opexal.view.classifier;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexal.entities.Clasificator;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.services.ClasificatorServ;
import ro.any.c12153.opexal.services.MailService;
import ro.any.c12153.opexal.services.PerioadaServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.events.DbDeleted;
import ro.any.c12153.shared.events.DbInserted;

/**
 *
 * @author catalin
 */
@Named(value = "classifier")
@ViewScoped
public class ClasificatorItem implements Serializable, SelectItemView<Clasificator>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ClasificatorItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject @DbInserted Event<Clasificator> dbInserted;
    private @Inject @DbDeleted Event<Clasificator> dbDeteled;
    
    private String initError;
    private List<Perioada> perioade;
    private String selPerId;
    private Clasificator selected;
    private String finishScript;
    
    @Override
    public void initLists(){
        try {
            this.perioade = PerioadaServ.getOpened(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    private static CompletableFuture<Clasificator> creazaModel(String coarea, Perioada perioada, String userId){
        return CompletableFuture.supplyAsync(() -> {
            Clasificator rezultat = null;
            try {
                rezultat = ClasificatorServ.createModel(coarea, perioada, userId);
            } catch (Exception ex) {
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }
            return rezultat;
        });
    }
    
    public void create(){
        try {
            if (this.selected == null || !Utils.stringNotEmpty(this.selected.getCoarea()))
                throw new Exception(App.getBeanMess("err.classifier.nok", clocale));
            
            if (!Utils.stringNotEmpty(this.selPerId)) throw new Exception(App.getBeanMess("err.perioada.nok", clocale));            
            final Perioada selPer = this.perioade.stream()
                    .filter(x -> this.selPerId.equals(x.getId()))
                    .findFirst()
                    .orElse(null);            
            if (selPer == null) throw new Exception(App.getBeanMess("err.perioada.nok", clocale));
            
            CompletableFuture.runAsync(() -> {
                try {
                    Clasificator rezultat = creazaModel(this.selected.getCoarea(), selPer, cuser.getUname())
                            .get(2, TimeUnit.HOURS);
                    this.dbInserted.fire(rezultat);
                    MailService.sendHtmlInfo(
                            Optional.of(cuser.getEmail()),
                            App.getBeanMess("title.classifier.ins", clocale),
                            App.getBeanMess("info.classifier.mail.end",  clocale),
                            Optional.of(clocale.getLanguage()),
                            cuser.getUname()
                    );
                } catch (Exception ex) {
                    MailService.sendHtmlError(
                        Optional.of(cuser.getEmail()),
                        App.getBeanMess("title.classifier.ins", clocale),
                        ex.getMessage(),
                        Optional.of(clocale.getLanguage()),
                        cuser.getUname()
                    );
                    ex.printStackTrace();
                }
            });
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.classifier.ins", clocale), App.getBeanMess("info.classifier.mail.start",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);

        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.classifier.ins", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.classifer.nok", clocale));
            
            if (!ClasificatorServ.deleteModel(this.selected.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));
            this.dbDeteled.fire(this.selected);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.classifier.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.classifier.del", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void clear(){
        this.initError = null;
        this.perioade = null;
        this.selPerId = null;
        this.selected = null;
        this.finishScript = null;
    }
    
    @Override
    public String getInitError() {
        return this.initError;
    }

    public List<Perioada> getPerioade() {
        return perioade;
    }

    public String getSelPerId() {
        return selPerId;
    }

    public void setSelPerId(String selPerId) {
        this.selPerId = selPerId;
    } 

    @Override
    public Clasificator getSelected() {
        return selected;
    }

    @Override
    public void setSelected(Clasificator selected) {
        this.selected = selected;
    }

    @Override
    public String getFinishScript() {
        return finishScript;
    }

    @Override
    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
