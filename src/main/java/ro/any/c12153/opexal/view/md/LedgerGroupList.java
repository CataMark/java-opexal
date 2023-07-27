package ro.any.c12153.opexal.view.md;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.entities.Ledger;
import ro.any.c12153.opexal.services.LedgerServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "ledgergrouplist")
@RequestScoped
public class LedgerGroupList implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LedgerGroupList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private String initError;
    private List<Ledger> list;
    
    @PostConstruct
    private void init(){
        try {
            this.list = LedgerServ.getGroups(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }

    public String getInitError() {
        return initError;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<Ledger> getList() {
        return list;
    }
}
