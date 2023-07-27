package ro.any.c12153.opexal.view.md;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
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
import ro.any.c12153.opexal.entities.CoCode;
import ro.any.c12153.opexal.entities.CostCenter;
import ro.any.c12153.opexal.entities.ProfitCenter;
import ro.any.c12153.opexal.services.CoCodeServ;
import ro.any.c12153.opexal.services.CostCenterServ;
import ro.any.c12153.opexal.services.ProfitCenterServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.events.DbDeleted;
import ro.any.c12153.shared.events.DbInserted;
import ro.any.c12153.shared.events.DbUpdated;

/**
 *
 * @author C12153
 */
@Named(value = "cstctr")
@ViewScoped
public class CostCenterItem implements Serializable, SelectItemView<CostCenter>{    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CostCenterItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject @DbInserted Event<CostCenter> dbInserted;
    private @Inject @DbUpdated Event<CostCenter> dbUpdated;
    private @Inject @DbDeleted Event<CostCenter> dbDeteled;
    
    private String initError;
    private List<CoCode> cocodes;
    private List<ProfitCenter> prctrs;
    private CostCenter selected;
    private String finishScript;
    
    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void initLists(){
        try {
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.cocodes = CoCodeServ.getAllByCoarea(this.selected.getCoarea(), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.prctrs = ProfitCenterServ.getAll(cuser.getUname());
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
    
    @Override
    public void clear(){
        this.initError = null;
        this.cocodes = null;
        this.prctrs = null;
        this.selected = null;
        this.finishScript = null;
    }
    
    public void save(){
        try {
            if (this.selected == null)
                throw new Exception(App.getBeanMess("err.cstctr.nok", clocale));
            
            if (this.selected.getMod_timp() == null){
                CostCenter rezultat = CostCenterServ.insert(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                this.dbInserted.fire(rezultat);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.cstctr.ins", clocale), App.getBeanMess("info.success",  clocale)));
            } else {
                CostCenter rezultat = CostCenterServ.update(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                this.dbUpdated.fire(rezultat);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.cstctr.upd", clocale), App.getBeanMess("info.success",  clocale)));                
            }
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.cstctr.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.cstctr.nok", clocale));
            
            if (!CostCenterServ.delete(this.selected.getCod(), this.selected.getCoarea(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));
            this.dbDeteled.fire(this.selected);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.cstctr.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.cstctr.del", clocale), ex.getMessage()));
        }
    }

    @Override
    public String getInitError() {
        return initError;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<CoCode> getCocodes() {
        return cocodes;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<ProfitCenter> getPrctrs() {
        return prctrs;
    }

    @Override
    public CostCenter getSelected() {
        return selected;
    }

    @Override
    public void setSelected(CostCenter selected) {
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
