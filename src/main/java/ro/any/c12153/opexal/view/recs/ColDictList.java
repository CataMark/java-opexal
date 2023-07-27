package ro.any.c12153.opexal.view.recs;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
import ro.any.c12153.dbutils.helpers.CallbackMethod;
import ro.any.c12153.dbutils.helpers.FieldMetaData;
import ro.any.c12153.opexal.entities.ColumnDictionary;
import ro.any.c12153.opexal.entities.SapTransaction;
import ro.any.c12153.opexal.services.ColumnDictionaryServ;
import ro.any.c12153.opexal.services.SapTransactionServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.Utils;
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
@Named(value = "coldictlist")
@ViewScoped
public class ColDictList implements Serializable, SelectTableView<ColumnDictionary>{    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ColDictList.class.getName());
    private static final String DEFAULT_LANG = "en";
    private static final String DB_LOAD_TABLE = "oxal1.tbl_int_mrecs_columns_load";
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private @Inject DataBaseLoadView dataLoad;
    private @Inject DialogController dialog;
    private @Inject SelectItemView<ColumnDictionary> item;
    private SapTransaction transaction;
    private List<ColumnDictionary> list;
    private ColumnDictionary selected;
    private String[] filterValue;
    private List<ColumnDictionary> filtered;
    
    private void observeDbInsert(@Observes(notifyObserver = Reception.IF_EXISTS) @DbInserted ColumnDictionary item){
        this.list.add(item);
        this.selected = item;
    }
    
    private void observeDbUpdate(@Observes(notifyObserver = Reception.IF_EXISTS) @DbUpdated ColumnDictionary item){
        for (int i = 0; i < this.list.size(); i++){
            if (this.list.get(i).getId().equals(item.getId())){
                this.list.set(i, item);
                break;
            }
        }
        this.selected = item;
    }
    
    private void observeDbDelete(@Observes(notifyObserver = Reception.IF_EXISTS) @DbDeleted ColumnDictionary item){
        this.list.removeIf(x -> x.getId().equals(item.getId()));
        this.selected = null;
    }
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String tr_param = Optional.ofNullable(params.get("tr"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.saptrz.not", clocale)));
            this.transaction = SapTransactionServ.getByCod(Utils.paramDecode(tr_param), cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.saptrz.not", clocale)));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            if (this.transaction != null) rezultat += "&tr=" + Utils.paramEncode(this.transaction.getCod());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void clearFilters(){
        this.filterValue = new String[]{"",""};
    }
    
    public void datainit(){
        try {
            this.clearFilters();
            this.list = ColumnDictionaryServ.getByTranzAndLang(this.transaction.getCod(), DEFAULT_LANG, cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.coldict.listinit", clocale), ex.getMessage()));
        }
    }

    @Override
    public void newItem() {
        ColumnDictionary rezultat = new ColumnDictionary();
        rezultat.setTranz(this.transaction.getCod());
        rezultat.setLang(DEFAULT_LANG);
        this.item.setSelected(rezultat);
    }

    @Override
    public void passSelected(boolean initLists) {
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new ColumnDictionary(this.selected.getJson()));
                if (initLists) this.item.initLists();
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }

    @Override
    public void clear() {
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
        
        FieldMetaData<String> field2 = new FieldMetaData<>();
        field2.setSqlName("sap_tranz");
        field2.setDefaultValue(this.transaction.getCod());
        
        FieldMetaData<String> field3 = new FieldMetaData<>();
        field3.setSqlName("lang");
        field3.setDefaultValue(DEFAULT_LANG);
        
        return Arrays.asList(new FieldMetaData<?>[]{field1, field2, field3});
    }
    
    @SuppressWarnings("UseSpecificCatch")
    private CallbackMethod onComplete(UUID uuid){        
        return () -> {
            try {
                ColumnDictionaryServ.mergeLoad(uuid, cuser.getUname());
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
        return this.initError;
    }

    public SapTransaction getTransaction() {
        return transaction;
    }
    
    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<ColumnDictionary> getList() {
        return list;
    }

    public ColumnDictionary getSelected() {
        return selected;
    }

    @Override
    public void setSelected(ColumnDictionary selected) {
        this.selected = selected;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public String[] getFilterValue() {
        return filterValue;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFilterValue(String[] filterValue) {
        this.filterValue = filterValue;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<ColumnDictionary> getFiltered() {
        return filtered;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFiltered(List<ColumnDictionary> filtered) {
        this.filtered = filtered;
    }
}
