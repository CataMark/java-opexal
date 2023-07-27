package ro.any.c12153.opexal.view.md;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.util.Constants;
import ro.any.c12153.opexal.entities.CostDriver;
import ro.any.c12153.opexal.entities.OpexCategory;
import ro.any.c12153.opexal.services.CostDriverServ;
import ro.any.c12153.opexal.services.OpexCategoryServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "ocateglist")
@ViewScoped
public class OpexCategoryList implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(OpexCategoryList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private CostDriver cdriver;
    private List<OpexCategory> list;
    private OpexCategory selected;
    private String[] filterValues;
    private List<OpexCategory> filtered;
    
    public void clearFilters(){
        this.filterValues = new String[]{"","",""};
    }
    
    @PostConstruct
    private void init(){
        try {
            this.clearFilters();
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String cd = Optional.ofNullable(params.get("cd"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.cdriver.not", clocale)));
            this.cdriver = CostDriverServ.getByCod(Utils.paramDecode(cd), cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.cdriver.not", clocale)));
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.cdriver == null ? "" : "&cd=" + Utils.paramEncode(this.cdriver.getCod()));            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        try {
            this.clearFilters();
            this.list = OpexCategoryServ.getByCdriver(this.cdriver.getCod(), cuser.getUname());            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.opexcat.listinit", clocale), ex.getMessage()));
        }
    }
    
    public void exportByCdriver(){        
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ExternalContext econtext = fcontext.getExternalContext();
        econtext.responseReset();
        econtext.setResponseCharacterEncoding(StandardCharsets.UTF_8.name());
        econtext.setResponseContentType(Utils.MEDIA_EXCEL);
        econtext.setResponseHeader("Content-Disposition", "attachment; filename=raport.xlsx");
        econtext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<>()); //setare cookie pentru PrimeFaces.monitorDownload
              
        try(OutputStream stream = econtext.getResponseOutputStream();){
            OpexCategoryServ.listByCdriverToXlsx(this.cdriver.getCod(), cuser.getUname(), stream);
            
        }catch(Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.data.down", clocale), ex.getMessage()));
        } finally {
            fcontext.responseComplete();
        }
    }
    
    public void exportAll(){        
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ExternalContext econtext = fcontext.getExternalContext();
        econtext.responseReset();
        econtext.setResponseCharacterEncoding(StandardCharsets.UTF_8.name());
        econtext.setResponseContentType(Utils.MEDIA_EXCEL);
        econtext.setResponseHeader("Content-Disposition", "attachment; filename=raport.xlsx");
        econtext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<>()); //setare cookie pentru PrimeFaces.monitorDownload
                
        try(OutputStream stream = econtext.getResponseOutputStream();){
            OpexCategoryServ.allToXlsx(cuser.getUname(), stream);
            
        }catch(Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.data.down", clocale), ex.getMessage()));
        } finally {
            fcontext.responseComplete();
        }
    }
    
    public String getInitError() {
        return initError;
    }

    public CostDriver getCdriver() {
        return cdriver;
    }
    
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<OpexCategory> getList() {
        return list;
    }

    public OpexCategory getSelected() {
        return selected;
    }
    
    public void setSelected(OpexCategory selected) {
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
    public List<OpexCategory> getFiltered() {
        return filtered;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFiltered(List<OpexCategory> filtered) {
        this.filtered = filtered;
    }
}
