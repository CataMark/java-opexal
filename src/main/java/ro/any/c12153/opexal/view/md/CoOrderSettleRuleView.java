package ro.any.c12153.opexal.view.md;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.util.Constants;
import ro.any.c12153.dbutils.helpers.CallbackMethod;
import ro.any.c12153.dbutils.helpers.FieldMetaData;
import ro.any.c12153.opexal.entities.CoArea;
import ro.any.c12153.opexal.entities.CoOrderSettleRule;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.services.CoAreaServ;
import ro.any.c12153.opexal.services.CoOrderSettleRuleServ;
import ro.any.c12153.opexal.services.PerioadaServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DataBaseLoadView;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author catalin
 */
@Named(value = "cosetrule")
@ViewScoped
public class CoOrderSettleRuleView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CoOrderSettleRuleView.class.getName());    
    private static final String DB_LOAD_TABLE = "oxal1.tbl_int_co_order_settle_rule_load";
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DataBaseLoadView dataLoad;    
    private @Inject DialogController dialog;
    private String initError;
    private CoArea coarea;
    private Perioada perioada;
    private CoOrderSettleRuleLazyDataModel list;
    private List<CoOrderSettleRule> selected;
    private String finishScript;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String pd = Optional.ofNullable(params.get("pd"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.perioada.not", clocale)));
            String co = Optional.ofNullable(params.get("co"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.perioada = PerioadaServ.getById(Utils.paramDecode(pd), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("title.perioada.not", clocale)));
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(co), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    })
            ).get(30, TimeUnit.SECONDS);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            if (this.perioada != null){ 
                rezultat += "&an=" + Utils.paramEncode(this.perioada.getAn().toString());
                rezultat += "&pd=" + Utils.paramEncode(this.perioada.getId());
            }
            if (this.coarea != null) rezultat += "&co=" + Utils.paramEncode(this.coarea.getCod());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        this.list = new CoOrderSettleRuleLazyDataModel(this.coarea.getCod(), this.perioada.getAn(), this.perioada.getLuna(), cuser.getUname(), clocale);
    }
    
    public void clear(){
        this.dialog.clear();
        this.dataLoad.clear();
        this.finishScript = null;
    }
    
    public void deleteSelected(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.lazy.nosel", clocale));
            
            String rezultat = CoOrderSettleRuleServ.deleteById(
                    this.selected.stream().map(CoOrderSettleRule::getId).collect(Collectors.toList()),
                    cuser.getUname());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.coordersetrule.del.select", clocale), rezultat));           
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.coordersetrule.del.select", clocale), ex.getMessage()));
        }
    }
    
    public void deleteByFilter(){
        try {
            String rezultat = CoOrderSettleRuleServ.deleteByFilter(
                    this.perioada.getAn(),
                    this.perioada.getLuna(),
                    this.coarea.getCod(),
                    Optional.ofNullable(this.list.getFilter()),
                    cuser.getUname());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.coordersetrule.del.filter", clocale), rezultat)); 
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.coordersetrule.del.filter", clocale), ex.getMessage()));
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
            CoOrderSettleRuleServ.toXlsx(this.perioada.getAn(), this.perioada.getLuna(), this.coarea.getCod(),
                    Optional.ofNullable(this.list.getFilter()), cuser.getUname(), stream);
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
        
        FieldMetaData<Short> field2 = new FieldMetaData<>();
        field2.setSqlName("an");
        field2.setDefaultValue(this.perioada.getAn());
        
        FieldMetaData<Short> field3 = new FieldMetaData<>();
        field3.setSqlName("luna");
        field3.setDefaultValue(this.perioada.getLuna());

        FieldMetaData<String> field4 = new FieldMetaData<>();
        field4.setSqlName("coarea");
        field4.setDefaultValue(this.coarea.getCod());
        
        return Arrays.asList(new FieldMetaData<?>[]{field1, field2, field3, field4});
    }
    
    private CallbackMethod onComplete(UUID uuid){
        return () -> {
            try {
                CoOrderSettleRuleServ.mergeLoad(uuid, cuser.getUname());
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
    
    //**********************************************

    public String getInitError() {
        return initError;
    }

    public CoArea getCoarea() {
        return coarea;
    }

    public Perioada getPerioada() {
        return perioada;
    }

    public CoOrderSettleRuleLazyDataModel getList() {
        return list;
    }

    public List<CoOrderSettleRule> getSelected() {
        return selected;
    }

    public void setSelected(List<CoOrderSettleRule> selected) {
        this.selected = selected;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
