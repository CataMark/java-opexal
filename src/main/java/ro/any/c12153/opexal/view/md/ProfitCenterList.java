package ro.any.c12153.opexal.view.md;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.dbutils.helpers.CallbackMethod;
import ro.any.c12153.dbutils.helpers.FieldMetaData;
import ro.any.c12153.opexal.entities.ProfitCenter;
import ro.any.c12153.opexal.services.ProfitCenterServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DataBaseLoadView;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.events.DbDeleted;
import ro.any.c12153.shared.events.DbInserted;
import ro.any.c12153.shared.events.DbUpdated;

/**
 *
 * @author C12153
 */
@Named(value = "prctrlist")
@ViewScoped
public class ProfitCenterList implements Serializable, SelectTableView<ProfitCenter>{    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProfitCenterList.class.getName());
    private static final String DB_LOAD_TABLE = "oxal1.tbl_int_profit_center_load";
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject DataBaseLoadView dataLoad;
    private @Inject DialogController dialog;
    private @Inject SelectItemView<ProfitCenter> item;
    
    private List<ProfitCenter> list;
    private ProfitCenter selected;
    private String[] filterValues;
    private List<ProfitCenter> filtered;
    
    private void observeDbInsert(@Observes(notifyObserver = Reception.IF_EXISTS) @DbInserted ProfitCenter item){
        this.list.add(item);
        this.selected = item;
    }
    
    private void observeDbUpdate(@Observes(notifyObserver = Reception.IF_EXISTS) @DbUpdated ProfitCenter item){
        for (int i = 0; i < this.list.size(); i++){
            if (this.list.get(i).getCod().equals(item.getCod())){
                this.list.set(i, item);
                break;
            }
        }
        this.selected = item;
    }
    
    private void observeDbDelete(@Observes(notifyObserver = Reception.IF_EXISTS) @DbDeleted ProfitCenter item){
        this.list.removeIf(x -> x.getCod().equals(item.getCod()));
        this.selected = null;
    }
    
    public void clearFilters(){
        this.filterValues = new String[]{"",""};
    }
    
    public void datainit(){
        try {
            this.clearFilters();
            this.list = ProfitCenterServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.prctr.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        this.item.setSelected(new ProfitCenter());
        this.item.initLists();
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new ProfitCenter(this.selected.getJson()));
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
        this.dataLoad.clear();
        this.dialog.clear();
        this.item.clear();
    }
    
    //**********************************************
    //upload initialization
    
    private List<FieldMetaData<?>> specificReservedFields(UUID uuid){
        FieldMetaData<String> field1 = new FieldMetaData<>();
        field1.setSqlName("load_id");
        field1.setDefaultValue(uuid.toString());
        
        return Arrays.asList(new FieldMetaData<?>[]{field1});
    }
    
    private CallbackMethod onComplete(UUID uuid){
        return () -> {
            try {
                ProfitCenterServ.mergeLoad(uuid, cuser.getUname());
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex.getCause());
            }
        };
    }
    
    public void initDbLoad(String finishScript){
        try {
            UUID load_uuid = UUID.randomUUID();
            this.dataLoad.setTabela(DB_LOAD_TABLE);
            this.dataLoad.setSpecificReservedFields(this.specificReservedFields(load_uuid));
            this.dataLoad.setOnComplete(this.onComplete(load_uuid));
            this.dataLoad.setMaxFileSize(20*1024*1024);
            this.dataLoad.setFinishScript(finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dload.data", clocale), ex.getMessage()));
        }
    }
    
    //**********************************************
    
    @Override
    public String getInitError() {
        return null;
    }

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<ProfitCenter> getList() {
        return list;
    }

    public ProfitCenter getSelected() {
        return selected;
    }

    @Override
    public void setSelected(ProfitCenter selected) {
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
    public List<ProfitCenter> getFiltered() {
        return filtered;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFiltered(List<ProfitCenter> filtered) {
        this.filtered = filtered;
    }
}
