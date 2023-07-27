package ro.any.c12153.opexal.view.recs;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
import ro.any.c12153.dbutils.helpers.FieldCheckResult;
import ro.any.c12153.dbutils.helpers.FieldMetaData;
import ro.any.c12153.opexal.entities.CoArea;
import ro.any.c12153.opexal.entities.ColumnDictionary;
import ro.any.c12153.opexal.entities.Flag;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.services.CoAreaServ;
import ro.any.c12153.opexal.services.ColumnDictionaryServ;
import ro.any.c12153.opexal.services.Fagll03DocUpdate;
import ro.any.c12153.opexal.services.Fagll03Serv;
import ro.any.c12153.opexal.services.FlagServ;
import ro.any.c12153.opexal.services.MailService;
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
 * @author C12153
 */
@Named(value = "fagll03")
@ViewScoped
public class Fagll03View implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(Fagll03View.class.getName());
    
    private static final String DB_LOAD_TABLE = "oxal1.tbl_int_fagll03";
    private static final String SAP_TRANZ = "FAGLL03";
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DataBaseLoadView dataLoad;    
    private @Inject DialogController dialog;
    private String initError;
    private CoArea coarea;
    private Perioada perioada;
    private Fagll03LazyDataModel list;
    
    private List<Map<String, Object>> selected;
    private Fagll03DocUpdate updateValues;
    private String finishScript;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String pd = Optional.ofNullable(params.get("pd"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.perioada.not", clocale)));
            String co = Optional.ofNullable(params.get("co"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            
            //TODO: check transaction agains upload matrix and throw error
            
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
            rezultat += "&tr=" + Utils.paramEncode(SAP_TRANZ);            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        this.list = new Fagll03LazyDataModel(this.perioada.getAn(), this.perioada.getLuna(), this.coarea.getCod(), cuser.getUname(), clocale);
    }
    
    public void clear(){
        this.dialog.clear();
        this.dataLoad.clear();
        this.updateValues = null;
        this.finishScript = null;
    }
    
    public void newUpdateValues(){
        this.updateValues = new Fagll03DocUpdate();
    }
    
    public void deleteSelected(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.lazy.nosel", clocale));
            
            String rezultat = Fagll03Serv.deleteById(
                    this.selected.stream().map(x -> (String) x.get("id")).collect(Collectors.toList()),
                    cuser.getUname());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.lazy.del.select", clocale), rezultat));           
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.lazy.del.select", clocale), ex.getMessage()));
        }
    }
    
    public void deleteByFilter(){
        try {
            String rezultat = Fagll03Serv.deleteByFilter(
                    this.perioada.getAn(),
                    this.perioada.getLuna(),
                    this.coarea.getCod(),
                    Optional.ofNullable(this.list.getFilter()),
                    cuser.getUname());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.lazy.del.filter", clocale), rezultat)); 
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.lazy.del.filter", clocale), ex.getMessage()));
        }
    }
    
    public void updateSelected(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.lazy.nosel", clocale));
            if (!this.updateValues.hasValues()) throw new Exception(App.getBeanMess("err.lazy.updateVals.nok", clocale));
            String rezultat = Fagll03Serv.updateById(
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
            String rezultat = Fagll03Serv.updateByFilter(
                    this.perioada.getAn(),
                    this.perioada.getLuna(),
                    this.coarea.getCod(),
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
            Fagll03Serv.toXlsx(
                    this.perioada.getAn(),
                    this.perioada.getLuna(),
                    this.coarea.getCod(),
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
    
    private Map<String, Function<Object, FieldCheckResult>> fieldCheck(){        
        Function<Object, FieldCheckResult> coareaCheck = valoare -> {
            if (valoare == null)
                return new FieldCheckResult(false, App.getBeanMess("title.coarea.not", clocale));
            if (String.class.isInstance(valoare)) {
                return new FieldCheckResult(this.coarea.getCod().equals(valoare), App.getBeanMess("err.coarea.nok", clocale));
            } else {
                return new FieldCheckResult(Short.parseShort(this.coarea.getCod()) == ((Number) valoare).shortValue(), App.getBeanMess("err.coarea.nok", clocale));
            }
        };
        
        Function<Object, FieldCheckResult> anCheck = valoare -> {
            if (valoare == null)
                return new FieldCheckResult(false, App.getBeanMess("err.perioada.not", clocale));
            if (String.class.isInstance(valoare)){
                return new FieldCheckResult(this.perioada.getAn().toString().equals(valoare), App.getBeanMess("err.perioada.nok", clocale));
            } else {
                return new FieldCheckResult(this.perioada.getAn() == ((Number) valoare).shortValue(), App.getBeanMess("err.perioada.nok", clocale));
            }
        };
        
        Function<Object, FieldCheckResult> lunaCheck = valoare -> {
            if (valoare == null)
                return new FieldCheckResult(false, App.getBeanMess("err.perioada.not", clocale));
            if (String.class.isInstance(valoare)){
                return new FieldCheckResult(this.perioada.getLuna().toString().equals(valoare), App.getBeanMess("err.perioada.nok", clocale));
            } else {
                return new FieldCheckResult(this.perioada.getLuna() == ((Number) valoare).shortValue(), App.getBeanMess("err.perioada.nok", clocale));
            }
        };
        
        Map<String, Function<Object, FieldCheckResult>> rezultat = new HashMap<>();
        rezultat.put("kokrs", coareaCheck);
        rezultat.put("gjahr", anCheck);
        rezultat.put("monat", lunaCheck);
        return rezultat;
    }
    
    private List<FieldMetaData<?>> specificReservedFields(UUID uuid){
        FieldMetaData<String> field1 = new FieldMetaData<>();
        field1.setSqlName("load_uuid");
        field1.setDefaultValue(uuid.toString());
        return Arrays.asList(field1);
    }
    
    private Map<String, String> fieldsNameMapping() throws Exception{
        return ColumnDictionaryServ.getByTranzAndLang(SAP_TRANZ, "en", cuser.getUname()).stream()
                .collect(Collectors.toMap(ColumnDictionary::getNume, ColumnDictionary::getCod));
    }
    
    private void sendErrorMail(String subject, Throwable ex){
        MailService.sendHtmlError(
                Optional.of(cuser.getEmail()),
                new StringBuilder(SAP_TRANZ)
                    .append(": ")
                    .append(subject)
                    .toString(),
                ex.getMessage(),
                Optional.of(clocale.getLanguage()),
                cuser.getUname()
        );
    }
    
    private static CompletableFuture<Void> callback(String coarea, UUID uuid, Date startTime, String userId){
        return CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    try {
                        Fagll03Serv.collectMasterData(uuid, coarea, Optional.of(startTime), userId);
                    } catch (Exception ex) {
                        throw new CompletionException(ex.getMessage(), ex.getCause());
                    }
                }),
                CompletableFuture.runAsync(() -> {
                    try {                       
                        FlagServ.insert(new Flag(uuid.toString(), Flag.Tip.UPLOAD, coarea, SAP_TRANZ), userId)
                                .orElseThrow(() -> new Exception("FLAG_INSERT_ERROR"));
                    } catch (Exception ex) {
                        throw new CompletionException(ex.getMessage(), ex.getCause());
                    }
                })
        );
    }
    
    private CallbackMethod onComplete(UUID uuid, Date startTime){
        return () -> CompletableFuture.runAsync(() -> {
            try {
                callback(this.coarea.getCod(), uuid, startTime, cuser.getUname())
                        .get(2, TimeUnit.MINUTES);
            } catch (Exception ex) {
                App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
                this.sendErrorMail(App.getBeanMess("title.dload.data", clocale), ex);
            }
        });
    }
    
    public void initDbLoad(String finishScript) {
        try {
            UUID load_uuid = UUID.randomUUID();
            Date startTime = new Date();

            this.dataLoad.setTabela(DB_LOAD_TABLE);
            this.dataLoad.setFieldCheck(this.fieldCheck());
            this.dataLoad.setSpecificReservedFields(this.specificReservedFields(load_uuid));
            this.dataLoad.setFieldsNameMapping(this.fieldsNameMapping());
            this.dataLoad.setOnComplete(this.onComplete(load_uuid, startTime));
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

    public Fagll03LazyDataModel getList() {
        return list;
    }

    public Fagll03DocUpdate getUpdateValues() {
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
