package ro.any.c12153.opexal.view.md;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.util.Constants;
import ro.any.c12153.dbutils.helpers.CallbackMethod;
import ro.any.c12153.dbutils.helpers.FieldMetaData;
import ro.any.c12153.opexal.entities.CoArea;
import ro.any.c12153.opexal.entities.CoOrder;
import ro.any.c12153.opexal.services.CoAreaServ;
import ro.any.c12153.opexal.services.CoOrderServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
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
 * @author catalin
 */
@Named(value = "coorderlist")
@ViewScoped
public class CoOrderList implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CoOrderList.class.getName());
    private static final String DB_LOAD_TABLE = "oxal1.tbl_int_co_order_load";
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject DataBaseLoadView dataLoad;
    private @Inject DialogController dialog;
    private @Inject SelectItemView<CoOrder> item;
    
    private String initError;
    private CoArea coarea;
    private CoOrderLazyDataModel list;
    private CoOrder selected;
    
    private void observeDbInsert(@Observes(notifyObserver = Reception.IF_EXISTS) @DbInserted CoOrder item){
        this.list.getWrappedData().add(0, item);
        this.list.setRowCount(this.list.getRowCount() + 1);
        this.selected = item;
    }
    
    private void observeDbUpdate(@Observes(notifyObserver = Reception.IF_EXISTS) @DbUpdated CoOrder item){
        for (int i = 0; i < this.list.getWrappedData().size(); i++){
            if (this.list.getWrappedData().get(i).getCod().equals(item.getCod())){
                this.list.getWrappedData().set(i, item);
                break;
            }
        }
        this.selected = item;
    }
    
    private void observeDbDelete(@Observes(notifyObserver = Reception.IF_EXISTS) @DbDeleted CoOrder item){
        this.list.getWrappedData().removeIf(x -> x.getCod().equals(item.getCod()));
        this.list.setRowCount(this.list.getRowCount() - 1);
        this.selected = null;
    }
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String ca = Optional.ofNullable(params.get("ca"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(ca), cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
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
        this.list = new CoOrderLazyDataModel(this.coarea.getCod(), cuser.getUname(), clocale);
    }
    
    public void clear(){
        this.dialog.clear();
        this.dataLoad.clear();
        this.item.clear();
    }
    
    public void newItem(){
        CoOrder rezultat = new CoOrder();
        rezultat.setCoarea(this.coarea.getCod());
        this.item.setSelected(rezultat);
        this.item.initLists();
    }
    
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new CoOrder(this.selected.getJson()));
                if (initLists) this.item.initLists();
            }            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }
    
    //**********************************************
    //upload initialization
    
    private List<FieldMetaData<?>> specificReservedFields(UUID uuid){
        FieldMetaData<String> field1 = new FieldMetaData<>();
        field1.setSqlName("load_id");
        field1.setDefaultValue(uuid.toString());

        FieldMetaData<String> field2 = new FieldMetaData<>();
        field2.setSqlName("coarea");
        field2.setDefaultValue(this.coarea.getCod());
        
        return Arrays.asList(new FieldMetaData<?>[]{field1, field2});
    }
    
    private CallbackMethod onComplete(UUID uuid){
        return () -> {
            try {
                CoOrderServ.mergeLoad(uuid, cuser.getUname());
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
    
    public void export(){
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ExternalContext econtext = fcontext.getExternalContext();
        econtext.responseReset();
        econtext.setResponseCharacterEncoding(StandardCharsets.UTF_8.name());
        econtext.setResponseContentType(Utils.MEDIA_EXCEL);
        econtext.setResponseHeader("Content-Disposition", "attachment; filename=raport.xlsx");
        econtext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<>()); //setare cookie pentru PrimeFaces.monitorDownload
        
        try(OutputStream stream = econtext.getResponseOutputStream();){
            CoOrderServ.toXlsx(this.coarea.getCod(), Optional.ofNullable(this.list.getFilter()), cuser.getUname(), stream);            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.lazy.down", clocale), ex.getMessage()));
        } finally {
            fcontext.responseComplete();
        }
    }

    public String getInitError() {
        return initError;
    }

    public CoArea getCoarea() {
        return coarea;
    }

    public CoOrderLazyDataModel getList() {
        return list;
    }

    public CoOrder getSelected() {
        return selected;
    }

    public void setSelected(CoOrder selected) {
        this.selected = selected;
    }
}
