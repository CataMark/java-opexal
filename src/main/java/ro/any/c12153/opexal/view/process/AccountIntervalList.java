package ro.any.c12153.opexal.view.process;

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
import ro.any.c12153.opexal.entities.AccountInterval;
import ro.any.c12153.opexal.services.AccountIntervalServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "accintvlist")
@ViewScoped
public class AccountIntervalList implements Serializable, SelectTableView<AccountInterval>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AccountIntervalList.class.getName());
    
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<AccountInterval> item;
    private List<AccountInterval> list;
    private AccountInterval selected;
    
    public void datainit(){
        try {
            this.list = AccountIntervalServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.accintv.listinit", clocale), ex.getMessage()));
        }
    }

    @Override
    public void newItem() {
        this.item.setSelected(new AccountInterval());
        this.item.initLists();
    }

    @Override
    public void passSelected(boolean initLists) {
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new AccountInterval(this.selected.getJson()));
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
        this.dialog.clear();
        this.item.clear();
    }

    @Override
    public String getInitError() {
        return null;
    }

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<AccountInterval> getList() {
        return list;
    }

    public AccountInterval getSelected() {
        return selected;
    }

    @Override
    public void setSelected(AccountInterval selected) {
        this.selected = selected;
    }
    
}
