package ro.any.c12153.opexal.view.md;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.bkg.AppSingleton;
import ro.any.c12153.opexal.entities.IcPartener;
import ro.any.c12153.opexal.services.IcPartenerServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "icpartlist")
@ViewScoped
public class IcPartenerView implements Serializable{    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(IcPartenerView.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private List<IcPartener> list;
    private IcPartener selected;
    private String[] filterValues;
    private List<IcPartener> fitered;
    
    public void clearFilters(){
        this.filterValues = new String[]{"",""};
    }
    
    public void datainit(){
        try {
            this.clearFilters();
            this.list = IcPartenerServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.icpart.listinit", clocale), ex.getMessage()));
        }
    }
    
    public String getInitError() {
        return null;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<IcPartener> getList() {
        return list;
    }

    public IcPartener getSelected() {
        return selected;
    }

    public void setSelected(IcPartener selected) {
        this.selected = selected;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public String[] getFilterValues() {
        return filterValues;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFilterValues(String[] filterValues) {
        this.filterValues = filterValues;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<IcPartener> getFitered() {
        return fitered;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFitered(List<IcPartener> fitered) {
        this.fitered = fitered;
    }
}
