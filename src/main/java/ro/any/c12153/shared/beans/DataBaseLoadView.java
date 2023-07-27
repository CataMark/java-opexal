package ro.any.c12153.shared.beans;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import ro.any.c12153.dbutils.helpers.CallbackMethod;
import ro.any.c12153.dbutils.helpers.FieldCheckResult;
import ro.any.c12153.dbutils.helpers.FieldMetaData;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "dbload")
@ViewScoped
public class DataBaseLoadView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(DataBaseLoadView.class.getName());    
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String tabela; //init from parent view controller
    private String delimitator; //use in load view xhtml
    private String quote; //use in load view xhtml
    private String sheetName; //use in load view xhtml 
    private long maxFileSize; // file maximum size in bytes
    private String textLoad; //use in load view xhtml
    private Map<String, Function<Object, FieldCheckResult>> fieldCheck; //init from parent view controller
    private List<FieldMetaData<?>> specificReservedFields; //init from parent view controller
    private Map<String, String> fieldsNameMapping; //init from parent view controller
    private CallbackMethod onStart; //init from parent view controller
    private CallbackMethod onComplete; //init from parent view controller
    private String initError;
    private String finishScript; //init from parent view xhtml
    
    public void clear(){
        this.tabela = null;
        this.delimitator = null;
        this.sheetName = null;
        this.quote = null;        
        this.maxFileSize = 0;
        this.textLoad = null;
        this.fieldCheck = null;
        this.specificReservedFields= null;
        this.fieldsNameMapping = null;
        this.onStart = null;
        this.onComplete = null;
        this.initError = null;
        this.finishScript = null;
    }
    
    public void loadFile(FileUploadEvent event){
        try {
            if (!Utils.stringNotEmpty(this.delimitator)) throw new Exception(App.getBeanMess("err.dload.delim.nok", clocale));            
            UploadedFile fisier = event.getFile();
            if (fisier == null) throw new Exception(App.getBeanMess("err.dload.file.nok", clocale));
            
            String rezultat;
            try(InputStreamReader iReader = new InputStreamReader(fisier.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader bReader = new BufferedReader(iReader);){
                rezultat = App.getConn(cuser.getUname())
                        .loadText(this.tabela, bReader, this.delimitator, Optional.ofNullable(this.quote), Optional.ofNullable(this.fieldCheck),
                                Optional.ofNullable(this.fieldsNameMapping), Optional.ofNullable(this.specificReservedFields),
                                Optional.ofNullable(this.onStart), Optional.ofNullable(this.onComplete));
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.dload.file", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dload.file", clocale), ex.getMessage()));
        }
    }
    
    public void loadExcel(FileUploadEvent event){
        try {         
            UploadedFile fisier = event.getFile();
            if (fisier == null) throw new Exception(App.getBeanMess("err.dload.file.nok", clocale));
            
            String rezultat;
            Path target = Files.createTempFile(cuser.getUname().concat("_"), ".xlsx");
            try(InputStream input = fisier.getInputStream();) {
                Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
                rezultat = App.getConn(cuser.getUname())
                        .loadExcel(this.tabela, target.toString(), Optional.ofNullable(this.sheetName), Optional.ofNullable(this.fieldCheck),
                                Optional.ofNullable(this.fieldsNameMapping), Optional.ofNullable(this.specificReservedFields),
                                Optional.ofNullable(this.onStart), Optional.ofNullable(onComplete));
            } finally {
                Files.deleteIfExists(target);
            }
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.dload.excel", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dload.excel", clocale), ex.getMessage()));
        }
    }
    
    public void loadText(){
        try {
            if (!Utils.stringNotEmpty(this.delimitator)) throw new Exception(App.getBeanMess("err.dload.delim.nok", clocale));
            if (!Utils.stringNotEmpty(this.textLoad)) throw new Exception("err.dload.text.nok");
            
            String rezultat;
            try(StringReader sReader = new StringReader(this.textLoad);
                BufferedReader bReader = new BufferedReader(sReader);){
                rezultat = App.getConn(cuser.getUname())
                        .loadText(this.tabela, bReader, this.delimitator, Optional.ofNullable(this.quote), Optional.ofNullable(this.fieldCheck),
                                Optional.ofNullable(this.fieldsNameMapping), Optional.ofNullable(this.specificReservedFields),
                                Optional.ofNullable(this.onStart), Optional.ofNullable(this.onComplete));
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.dload.text", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dload.text", clocale), ex.getMessage()));
        }
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getDelimitator() {
        return delimitator;
    }

    public void setDelimitator(String delimitator) {
        this.delimitator = delimitator;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getTextLoad() {
        return textLoad;
    }

    public void setTextLoad(String textLoad) {
        this.textLoad = textLoad;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Map<String, Function<Object, FieldCheckResult>> getFieldCheck() {
        return fieldCheck;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFieldCheck(Map<String, Function<Object, FieldCheckResult>> fieldCheck) {
        this.fieldCheck = fieldCheck;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<FieldMetaData<?>> getSpecificReservedFields() {
        return specificReservedFields;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setSpecificReservedFields(List<FieldMetaData<?>> specificReservedFields) {
        this.specificReservedFields = specificReservedFields;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Map<String, String> getFieldsNameMapping() {
        return fieldsNameMapping;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFieldsNameMapping(Map<String, String> fieldsNameMapping) {
        this.fieldsNameMapping = fieldsNameMapping;
    }

    public CallbackMethod getOnStart() {
        return onStart;
    }

    public void setOnStart(CallbackMethod onStart) {
        this.onStart = onStart;
    }

    public CallbackMethod getOnComplete() {
        return onComplete;
    }

    public void setOnComplete(CallbackMethod onComplete) {
        this.onComplete = onComplete;
    }

    public String getErrorFileLoadInit() {
        if (Utils.stringNotEmpty(this.initError)) return this.initError;
        try {
            if (!Utils.stringNotEmpty(this.tabela)) throw new Exception(App.getBeanMess("err.dload.tbl.nok", clocale));
            if (this.maxFileSize <= 0) throw new Exception(App.getBeanMess("err.dload.size.nok", clocale));
        } catch (Exception ex) {
            this.initError = ex.getMessage();
        }
        return this.initError;
    }

    public String getErrorTextLoadInit() {
        if (Utils.stringNotEmpty(this.initError)) return this.initError;
        try {
            if (!Utils.stringNotEmpty(this.tabela)) throw new Exception(App.getBeanMess("err.dload.tbl.nok", clocale));
        } catch (Exception ex) {
            this.initError = ex.getMessage();
        }
        return this.initError;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
