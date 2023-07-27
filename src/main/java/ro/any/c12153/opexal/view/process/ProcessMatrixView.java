package ro.any.c12153.opexal.view.process;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.entities.ProcessMatrix;
import ro.any.c12153.opexal.services.ProcessMatrixServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "prsmtrxlist")
@RequestScoped
public class ProcessMatrixView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProcessMatrixView.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private String initError;
    private List<ProcessMatrix> list;
    
    @PostConstruct
    private void init(){
        try {
            this.list = ProcessMatrixServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }

    public String getInitError() {
        return initError;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<ProcessMatrix> getList() {
        return list;
    }
}
