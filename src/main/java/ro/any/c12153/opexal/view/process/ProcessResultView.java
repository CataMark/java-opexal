package ro.any.c12153.opexal.view.process;

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
import ro.any.c12153.opexal.entities.Ledger;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.services.CoAreaServ;
import ro.any.c12153.opexal.services.LedgerServ;
import ro.any.c12153.opexal.services.PerioadaServ;
import ro.any.c12153.opexal.services.ProcessResultDocUpdate;
import ro.any.c12153.opexal.services.ProcessResultServ;
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
@Named(value = "presult")
@ViewScoped
public class ProcessResultView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProcessResultView.class.getName());
    
    private static final String DB_LOAD_TABLE = "oxal1.tbl_int_process_result";
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DataBaseLoadView dataLoad;    
    private @Inject DialogController dialog;
    private String initError;
    private CoArea coarea;
    private Perioada perioada;
    private Ledger ledger;
    private ProcessResultLazyDataModel list;
    
    private List<Map<String, Object>> selected;
    private ProcessResultDocUpdate updateValues;
    private String finishScript;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String pd = Optional.ofNullable(params.get("pd"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.perioada.not", clocale)));
            String co = Optional.ofNullable(params.get("co"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            String ld = Optional.ofNullable(params.get("ld"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.ledger.not", clocale)));
            
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
                            //TODO: check coarea against upload matrix (also in database for insert, update, delete)
                            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(co), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            String _ld = Utils.paramDecode(ld);
                            this.ledger = LedgerServ.getLedgers(cuser.getUname()).stream()
                                    .filter(x -> _ld.equals(x.getLedger()))
                                    .findFirst()
                                    .orElseThrow(() -> new Exception(App.getBeanMess("title.ledger.not", clocale)));
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
            if (this.ledger != null) rezultat += "&ld=" + Utils.paramEncode(this.ledger.getLedger());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        this.list = new ProcessResultLazyDataModel(this.perioada.getAn(), this.perioada.getLuna(), this.coarea.getCod(), this.ledger.getLedger(), cuser.getUname(), clocale);
    }
    
    public void clear(){
        this.dialog.clear();
        this.dataLoad.clear();
        this.updateValues = null;
        this.finishScript = null;
    }
    
    public void newUpdateValues(){
        this.updateValues = new ProcessResultDocUpdate();
    }
    
    public void delete(){
        try {
            if (!ProcessResultServ.delete(this.perioada.getAn(), this.perioada.getLuna(), this.coarea.getCod(), initError))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.data.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.data.del", clocale), ex.getMessage()));
        }
    }
    
    public void updateSelected(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.lazy.nosel", clocale));
            if (!this.updateValues.hasValues()) throw new Exception(App.getBeanMess("err.lazy.updateVals.nok", clocale));
            String rezultat = ProcessResultServ.updateById(
                    this.selected.stream().map(x -> (String) x.get("id")).collect(Collectors.toList()),
                    this.updateValues,
                    cuser.getUname());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.lazy.upd.select", clocale), rezultat));           
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.lazy.upd.select", clocale), ex.getMessage()));
        }
    }
    
    public void updateByFilter(){
        try {
            if (!this.updateValues.hasValues()) throw new Exception(App.getBeanMess("err.lazy.updateVals.nok", clocale));
            String rezultat = ProcessResultServ.updateByFilter(
                    this.perioada.getAn(),
                    this.perioada.getLuna(),
                    this.coarea.getCod(),
                    this.ledger.getLedger(),
                    Optional.ofNullable(this.list.getFilter()),
                    this.updateValues,
                    cuser.getUname());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.lazy.upd.filter", clocale), rezultat)); 
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.lazy.upd.filter", clocale), ex.getMessage()));
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
            ProcessResultServ.toXlsx(
                    this.perioada.getAn(),
                    this.perioada.getLuna(),
                    this.coarea.getCod(),
                    this.ledger.getLedger(),
                    Optional.ofNullable(this.list.getFilter()),
                    cuser.getUname(),
                    stream);            
        } catch(Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.lazy.down", clocale), ex.getMessage()));
        } finally {
            fcontext.responseComplete();
        }
    }
    
    //****************************************************************
    //upload initialization
    
    private List<FieldMetaData<?>> specificReservedFields(UUID uuid){
        FieldMetaData<String> field1 = new FieldMetaData<>();
        field1.setSqlName("load_id");
        field1.setDefaultValue(uuid.toString());
        
        return Arrays.asList(new FieldMetaData<?>[]{field1});
    }
    
    private CallbackMethod onComplete(UUID uuid){
        return () -> {
            try {
                ProcessResultServ.mergeLoad(uuid, cuser.getUname());
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex.getCause());
            }
        };
    }
    
    public void initDbLoad(String finishScript) {
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
    
    //*********************************************************

    public String getInitError() {
        return initError;
    }

    public CoArea getCoarea() {
        return coarea;
    }

    public Perioada getPerioada() {
        return perioada;
    }

    public Ledger getLedger() {
        return ledger;
    }

    public ProcessResultLazyDataModel getList() {
        return list;
    }

    public ProcessResultDocUpdate getUpdateValues() {
        return updateValues;
    }

    public List<Map<String, Object>> getSelected() {
        return selected;
    }

    public void setSelected(List<Map<String, Object>> selected) {
        this.selected = selected;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }    
}
