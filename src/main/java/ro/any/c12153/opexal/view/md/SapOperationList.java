package ro.any.c12153.opexal.view.md;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.entities.SapOperation;
import ro.any.c12153.opexal.services.SapOperationServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DataBaseLoadView;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "sapoperlist")
@ViewScoped
public class SapOperationList implements Serializable, SelectTableView<SapOperation>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(SapOperationList.class.getName());
    
    private static final String DB_LOAD_TABLE = "oxal1.tbl_int_sap_oper";

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject DataBaseLoadView dataLoad;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<SapOperation> item;
    private List<SapOperation> list;
    private SapOperation selected;
    private String[] filterValues;
    private List<SapOperation> filtered;
    
    public void clearFilters(){
        this.filterValues = new String[]{"",""};
    }
    
    public void datainit(){
        try {
            this.clearFilters();
            this.list = SapOperationServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sapoper.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        this.item.setSelected(new SapOperation());
        this.item.initLists();
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new SapOperation(this.selected.getJson()));
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
       this.dataLoad.clear();
    }
    
    public void initDbLoad(String finishScript){
        this.dataLoad.setTabela(DB_LOAD_TABLE);
        this.dataLoad.setMaxFileSize(20*1024*1024);
        this.dataLoad.setFinishScript(finishScript);
    }

    @Override
    public String getInitError() {
        return null;
    }
    
    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<SapOperation> getList() {
        return list;
    }

    public SapOperation getSelected() {
        return selected;
    }

    @Override
    public void setSelected(SapOperation selected) {
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
    public List<SapOperation> getFiltered() {
        return filtered;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFiltered(List<SapOperation> filtered) {
        this.filtered = filtered;
    }
}
