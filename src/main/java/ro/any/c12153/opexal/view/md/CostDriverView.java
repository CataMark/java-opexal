package ro.any.c12153.opexal.view.md;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.entities.CostDriver;
import ro.any.c12153.opexal.services.CostDriverServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "cdriverlist")
@RequestScoped
public class CostDriverView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CostDriverView.class.getName());
    
    private @Inject @CurrentUser User cuser;    
    private String initError;    
    private List<CostDriver> list;
    
    @PostConstruct
    private void init(){
        try {
            this.list = CostDriverServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }

    public String getInitError() {
        return initError;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<CostDriver> getList() {
        return list;
    }
}
