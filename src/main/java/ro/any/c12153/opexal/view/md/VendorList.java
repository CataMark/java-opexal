package ro.any.c12153.opexal.view.md;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import ro.any.c12153.opexal.entities.Vendor;
import ro.any.c12153.opexal.services.VendorServ;
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
@Named(value = "vendorlist")
@ViewScoped
public class VendorList implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(VendorList.class.getName());
    private static final String DB_LOAD_TABLE = "oxal1.tbl_int_vendor_load";
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject DataBaseLoadView dataLoad;
    private @Inject DialogController dialog;
    private @Inject SelectItemView<Vendor> item;
    
    private VendorLazyDataModel list;
    private Vendor selected;
    
    private void observeDbInsert(@Observes(notifyObserver = Reception.IF_EXISTS) @DbInserted Vendor item){
        this.list.getWrappedData().add(0, item);
        this.list.setRowCount(this.list.getRowCount() + 1);
        this.selected = item;
    }
    
    private void observeDbUpdate(@Observes(notifyObserver = Reception.IF_EXISTS) @DbUpdated Vendor item){
        for (int i = 0; i < this.list.getWrappedData().size(); i++){
            if (this.list.getWrappedData().get(i).getCod().equals(item.getCod())){
                this.list.getWrappedData().set(i, item);
                break;
            }
        }
        this.selected = item;
    }
    
    private void observeDbDelete(@Observes(notifyObserver = Reception.IF_EXISTS) @DbDeleted Vendor item){
        this.list.getWrappedData().removeIf(x -> x.getCod().equals(item.getCod()));
        this.list.setRowCount(this.list.getRowCount() - 1);
        this.selected = null;
    }
    
    public void datainit(){
        this.list = new VendorLazyDataModel(cuser.getUname(), clocale);
    }
    
    public void clear(){
        this.dialog.clear();
        this.dataLoad.clear();
        this.item.clear();
    }
    
    public void newItem(){
        this.item.setSelected(new Vendor());
        this.item.initLists();
    }
    
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new Vendor(this.selected.getJson()));
                if (initLists) this.item.initLists();
            }            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }
    
    public void export(){
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ExternalContext econtext = fcontext.getExternalContext();
        econtext.responseReset();
        econtext.setResponseCharacterEncoding(StandardCharsets.UTF_8.name());
        econtext.setResponseContentType(Utils.MEDIA_EXCEL);
        econtext.setResponseHeader("Content-Disposition", "attachment; filename=raport.xlsx");
        econtext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<>()); //setare cookie pentru PrimeFaces.monitorDownload
        
        try(OutputStream stream = econtext.getResponseOutputStream();){
            VendorServ.toXlsx(Optional.ofNullable(this.list.getFilter()), cuser.getUname(), stream);            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.lazy.down", clocale), ex.getMessage()));
        } finally {
            fcontext.responseComplete();
        }
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
                VendorServ.mergeLoad(uuid, cuser.getUname());
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
    
    //*********************************************
    
    public String getInitError() {
        return null;
    }
    
    public VendorLazyDataModel getList() {
        return list;
    }

    public Vendor getSelected() {
        return selected;
    }

    public void setSelected(Vendor selected) {
        this.selected = selected;
    }
}
