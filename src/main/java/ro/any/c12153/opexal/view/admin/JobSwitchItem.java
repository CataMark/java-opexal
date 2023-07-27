package ro.any.c12153.opexal.view.admin;

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
import ro.any.c12153.opexal.entities.JobSwitch;
import ro.any.c12153.opexal.services.JobSwitchServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.events.DbUpdated;

/**
 *
 * @author catalin
 */
@Named(value = "jobswitch")
@ViewScoped
public class JobSwitchItem implements Serializable, SelectItemView<JobSwitch>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(JobSwitchItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject @DbUpdated Event<JobSwitch> dbUpdated;
    
    private JobSwitch selected;
    private String finishScript;
    
    @Override
    public void initLists(){        
    }
    
    @Override
    public void clear(){
        this.selected = null;
        this.finishScript = null;
    }
    
    public void save(){
        try {
            if (this.selected == null || !Utils.stringNotEmpty(this.selected.getCod()) || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.jobswitch.nok", clocale));
            
            JobSwitch rezultat = JobSwitchServ.update(this.selected, cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
            this.dbUpdated.fire(rezultat);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.jobswitch.save", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.jobswitch.save", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public String getInitError() {
        return null;
    }

    @Override
    public JobSwitch getSelected() {
        return selected;
    }

    @Override
    public void setSelected(JobSwitch selected) {
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
