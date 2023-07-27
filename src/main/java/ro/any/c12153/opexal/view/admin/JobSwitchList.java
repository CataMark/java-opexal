package ro.any.c12153.opexal.view.admin;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.entities.JobSwitch;
import ro.any.c12153.opexal.services.JobSwitchServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.events.DbUpdated;

/**
 *
 * @author catalin
 */
@Named(value = "jobswitchlist")
@ViewScoped
public class JobSwitchList implements Serializable, SelectTableView<JobSwitch>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(JobSwitchList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<JobSwitch> item;
    private List<JobSwitch> list;
    private JobSwitch selected;
    
    private void observeDbUpdate(@Observes(notifyObserver = Reception.IF_EXISTS) @DbUpdated JobSwitch item){
        for (int i = 0; i < this.list.size(); i++){
            if (this.list.get(i).getCod().equals(item.getCod())){
                this.list.set(i, item);
                break;
            }
        }
        this.selected = item;
    }
    
    public void datainit(){
        try {
            this.list = JobSwitchServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.perioada.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new JobSwitch(this.selected.getJson()));
                if (initLists) this.item.initLists();
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void clear(){
        this.dialog.clear();
        this.item.clear();
    }
    
    @Override
    public String getInitError(){
        return null;
    }

    @Override
    public List<JobSwitch> getList() {
        return list;
    }

    public JobSwitch getSelected() {
        return selected;
    }

    @Override
    public void setSelected(JobSwitch selected) {
        this.selected = selected;
    }
}
