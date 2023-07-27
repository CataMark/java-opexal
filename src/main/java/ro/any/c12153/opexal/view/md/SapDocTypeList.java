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
import ro.any.c12153.opexal.entities.SapDocType;
import ro.any.c12153.opexal.services.SapDocTypeServ;
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
@Named(value = "sapdoctiplist")
@ViewScoped
public class SapDocTypeList implements Serializable, SelectTableView<SapDocType>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(SapDocTypeList.class.getName());
    
    private static final String DB_LOAD_TABLE = "oxal1.tbl_int_sap_doc_tip";

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject DataBaseLoadView dataLoad;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<SapDocType> item;
    private List<SapDocType> list;
    private SapDocType selected;
    private String[] filterValues;
    private List<SapDocType> filtered;
    
    public void clearFilters(){
        this.filterValues = new String[]{"",""};
    }
    
    public void datainit(){
        try {
            this.clearFilters();
            this.list = SapDocTypeServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sapdoctip.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        this.item.setSelected(new SapDocType());
        this.item.initLists();
        this.dataLoad.clear();
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new SapDocType(this.selected.getJson()));
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
    public List<SapDocType> getList() {
        return list;
    }

    public SapDocType getSelected() {
        return selected;
    }

    @Override
    public void setSelected(SapDocType selected) {
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
    public List<SapDocType> getFiltered() {
        return filtered;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFiltered(List<SapDocType> filtered) {
        this.filtered = filtered;
    }
}
