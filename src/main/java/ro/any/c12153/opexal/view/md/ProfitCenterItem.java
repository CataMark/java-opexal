package ro.any.c12153.opexal.view.md;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexal.entities.ProfitCenter;
import ro.any.c12153.opexal.entities.Segment;
import ro.any.c12153.opexal.services.ProfitCenterServ;
import ro.any.c12153.opexal.services.SegmentServ;
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
@Named(value = "prctr")
@ViewScoped
public class ProfitCenterItem implements Serializable, SelectItemView<ProfitCenter>{    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProfitCenterItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject @DbInserted Event<ProfitCenter> dbInserted;
    private @Inject @DbUpdated Event<ProfitCenter> dbUpdated;
    private @Inject @DbDeleted Event<ProfitCenter> dbDeteled;
    
    private String initError;
    private List<Segment> segmente;
    private ProfitCenter selected;
    private String finishScript;
    
    @Override
    public void initLists() {
        try {
            this.segmente = SegmentServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    @Override
    public void clear(){
        this.initError = null;
        this.segmente = null;
        this.selected = null;
        this.finishScript = null;
    }
    
    public void save(){
        try {
            if (this.selected == null)
                throw new Exception(App.getBeanMess("err.prctr.nok", clocale));
            
            if (this.selected.getMod_timp() == null){
                ProfitCenter rezultat = ProfitCenterServ.insert(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                this.dbInserted.fire(rezultat);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.prctr.ins", clocale), App.getBeanMess("info.success",  clocale)));
            } else {
                ProfitCenter rezultat = ProfitCenterServ.update(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                this.dbUpdated.fire(rezultat);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.prctr.upd", clocale), App.getBeanMess("info.success",  clocale)));
            }
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.prctr.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.prctr.nok", clocale));
            
            if (!ProfitCenterServ.delete(this.selected.getCod(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));
            this.dbDeteled.fire(this.selected);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.prctr.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.prctr.del", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public String getInitError() {
        return this.initError;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<Segment> getSegmente() {
        return segmente;
    }

    @Override
    public ProfitCenter getSelected() {
        return selected;
    }

    @Override
    public void setSelected(ProfitCenter selected) {
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
