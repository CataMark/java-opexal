package ro.any.c12153.opexal.view.recs;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.entities.SapTransaction;
import ro.any.c12153.opexal.services.SapTransactionServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "coldictprompt")
@ViewScoped
public class ColDictPrompt implements Serializable{    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ColDictPrompt.class.getName());
    
    private @Inject @CurrentUser User cuser;
    
    private String initError;
    private List<SapTransaction> list;
    private String selected;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            Optional<String> tr = Optional.ofNullable(params.get("tr"));
            if (tr.isPresent()) this.selected = Utils.paramDecode(tr.get());
            
            this.list = SapTransactionServ.getAll(cuser.getUname());            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            if (this.selected != null) rezultat += "&tr=" + Utils.paramEncode(this.selected);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public String getInitError() {
        return initError;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<SapTransaction> getList() {
        return list;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
