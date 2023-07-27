package ro.any.c12153.opexal.view.recs;

import java.io.Serializable;
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
import ro.any.c12153.opexal.entities.CoCode;
import ro.any.c12153.opexal.entities.UploadMatrix;
import ro.any.c12153.opexal.services.UploadMatrixServ;
import ro.any.c12153.opexal.entities.SapTransaction;
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
@Named(value = "upmtrx")
@ViewScoped
public class UploadMatrixItem implements Serializable, SelectItemView<UploadMatrix>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UploadMatrixItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject @DbInserted Event<UploadMatrix> dbInserted;
    private @Inject @DbUpdated Event<UploadMatrix> dbUpdated;
    private @Inject @DbDeleted Event<UploadMatrix> dbDeleted;
    
    private CoCode cocode;
    private SapTransaction transaction;
    private UploadMatrix selected;
    private String finishScript;

    @Override
    public void initLists() {
        
    }

    @Override
    public void clear() {
        this.cocode = null;
        this.transaction = null;
        this.selected = null;
        this.finishScript = null;
    }
    
    public void save(){
        try {
            if (this.selected == null)
                throw new Exception(App.getBeanMess("err.upmtrx.nok", clocale));
            
            if (this.selected.getMod_timp() == null){
                this.selected.setCocode(this.cocode.getCod());
                this.selected.setTranz(this.transaction.getCod());
                
                UploadMatrix rezultat = UploadMatrixServ.insert(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                this.dbInserted.fire(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.upmtrx.ins", clocale), App.getBeanMess("info.success",  clocale)));
            } else {
                UploadMatrix rezultat = UploadMatrixServ.update(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                this.dbUpdated.fire(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.upmtrx.upd", clocale), App.getBeanMess("info.success",  clocale)));
            }
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.upmtrx.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.upmtrx.nok", clocale));
            
            if (!UploadMatrixServ.delete(this.selected.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));
            this.dbDeleted.fire(this.selected);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.upmtrx.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.upmtrx.del", clocale), ex.getMessage()));
        }
    }

    @Override
    public String getInitError() {
        return null;
    }

    public CoCode getCocode() {
        return cocode;
    }

    public void setCocode(CoCode cocode) {
        this.cocode = cocode;
    }

    public SapTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(SapTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public UploadMatrix getSelected() {
        return this.selected;
    }

    @Override
    public void setSelected(UploadMatrix selected) {
        this.selected = selected;
    }

    @Override
    public String getFinishScript() {
        return this.finishScript;
    }

    @Override
    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
