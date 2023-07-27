package ro.any.c12153.shared.beans;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.services.UserService;

/**
 *
 * @author C12153
 */
@Named(value = "usersession")
@RequestScoped
public class UserSessionView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UserSessionView.class.getName());
    
    @Inject @CurrentUser private User cuser;
    private String initError;
    private List<User> list;
    
    @PostConstruct
    public void init(){
        try {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            String path = context.getRequestServerName() + ":" + context.getRequestServerPort() + context.getApplicationContextPath();            
            this.list = UserService.getLastSessionsByApp(path, cuser.getUname());
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }

    public String getInitError() {
        return initError;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<User> getList() {
        return list;
    }
}
