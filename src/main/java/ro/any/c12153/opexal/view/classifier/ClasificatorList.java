package ro.any.c12153.opexal.view.classifier;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.entities.Clasificator;
import ro.any.c12153.opexal.entities.CoArea;
import ro.any.c12153.opexal.services.ClasificatorServ;
import ro.any.c12153.opexal.services.CoAreaServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.events.DbDeleted;
import ro.any.c12153.shared.events.DbInserted;

/**
 *
 * @author catalin
 */
@Named(value = "classifierlist")
@ViewScoped
public class ClasificatorList implements Serializable, SelectTableView<Clasificator>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ClasificatorList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject DialogController dialog;
    private @Inject SelectItemView<Clasificator> item;
    
    private CoArea coarea;
    private String initError;
    private List<Clasificator> list;
    private Clasificator selected;
    private String[] filterValues;
    private List<Clasificator> filtered;
    
    private void observeDbInsert(@Observes(notifyObserver = Reception.IF_EXISTS) @DbInserted Clasificator item){
        this.list.add(item);
        this.selected = item;
    }
    
    private void observeDbDelete(@Observes(notifyObserver = Reception.IF_EXISTS) @DbDeleted Clasificator item){
        this.list.removeIf(x -> x.getId().equals(item.getId()));
        this.selected = null;
    }
    
    public void clearFilters(){
        this.filterValues = new String[]{"",""};
    }
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String ca = Optional.ofNullable(params.get("ca"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(ca), cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            
            this.clearFilters();
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
    
    public void datainit(){
        try {
            this.clearFilters();
            this.list = ClasificatorServ.getAllModelLog(this.coarea.getCod(), cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.classifier.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        Clasificator rezultat = new Clasificator();
        rezultat.setCoarea(this.coarea.getCod());
        this.item.setSelected(rezultat);
        this.item.initLists();
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new Clasificator(this.selected.getJson()));
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
    public String getInitError() {
        return initError;
    } 
    
    public CoArea getCoarea() {
        return coarea;
    }

    @Override
    public List<Clasificator> getList() {
        return list;
    }

    public Clasificator getSelected() {
        return selected;
    }

    @Override
    public void setSelected(Clasificator selected) {
        this.selected = selected;
    }

    public String[] getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(String[] filterValues) {
        this.filterValues = filterValues;
    }

    public List<Clasificator> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<Clasificator> filtered) {
        this.filtered = filtered;
    }
}
