package ro.any.c12153.opexal.view.md;

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
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.services.PerioadaServ;
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
import ro.any.c12153.shared.events.DbUpdated;

/**
 *
 * @author C12153
 */
@Named(value = "periodlist")
@ViewScoped
public class PerioadaList implements Serializable, SelectTableView<Perioada>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(PerioadaList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private @Inject DialogController dialog;
    private @Inject SelectItemView<Perioada> item;
    private Short an;
    private List<Perioada> list;
    private Perioada selected;
    
    private void observeDbInsert(@Observes(notifyObserver = Reception.IF_EXISTS) @DbInserted Perioada item){
        this.list.add(item);
        this.selected = item;
    }
    
    private void observeDbUpdate(@Observes(notifyObserver = Reception.IF_EXISTS) @DbUpdated Perioada item){
        for (int i = 0; i < this.list.size(); i++){
            if (this.list.get(i).getId().equals(item.getId())){
                this.list.set(i, item);
                break;
            }
        }
        this.selected = item;
    }
    
    private void observesDbDelete(@Observes(notifyObserver = Reception.IF_EXISTS) @DbDeleted Perioada item){
        this.list.removeIf(x -> x.getId().equals(item.getId()));
        this.selected = null;
    }
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String an_param = Optional.ofNullable(params.get("an"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.an.not", clocale)));
            this.an = Short.parseShort(Utils.paramDecode(an_param));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.an == null ? "" : "&an=" + Utils.paramEncode(this.an.toString()));            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        try {
            this.list = PerioadaServ.getByAn(this.an, cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.perioada.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        Perioada rezultat = new Perioada();
        rezultat.setAn(this.an);
        rezultat.setInchis(Boolean.FALSE);
        this.item.setSelected(rezultat);
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new Perioada(this.selected.getJson()));
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

    public Short getAn() {
        return an;
    }

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<Perioada> getList() {
        return list;
    }

    public Perioada getSelected() {
        return selected;
    }

    @Override
    public void setSelected(Perioada selected) {
        this.selected = selected;
    }
}
