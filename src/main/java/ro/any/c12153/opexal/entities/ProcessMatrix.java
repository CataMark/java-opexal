package ro.any.c12153.opexal.entities;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Date;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
public class ProcessMatrix implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String ID = "id";
    private static final String SAP_OPER = "sap_oper";
    private static final String SAP_TRANZ = "sap_tranz";
    private static final String DOC_TIP = "doc_tip";
    private static final String SYST_LOGIC = "syst_logic";
    private static final String APP_OPER = "app_oper";
    private static final String BLOCAT = "blocat";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //non-table fields
    private static final String SAP_OPER_NUME = "sap_oper_nume";
    private static final String SAP_TRANZ_NUME = "sap_tranz_nume";
    private static final String DOC_TIP_NUME = "doc_tip_nume";
    private static final String APP_OPER_NUME = "app_oper_nume";
    private static final String ORDINE = "ordine";
    
    private String id;
    private String sap_oper;
    private String sap_oper_nume;
    private String sap_tranz;
    private String sap_tranz_nume;
    private String doc_tip;
    private String doc_tip_nume;
    private String syst_logic;
    private String app_oper;
    private String app_oper_nume;
    private Boolean blocat;
    private Short ordine;
    private String mod_de;
    private Date mod_timp;

    public ProcessMatrix(){
    }
    
    public ProcessMatrix(Map<String, Object> inreg){
        this.id = (String) inreg.get(ID);
        this.sap_oper = (String) inreg.get(SAP_OPER);
        this.sap_oper_nume = (String) inreg.get(SAP_OPER_NUME);
        this.sap_tranz = (String) inreg.get(SAP_TRANZ);
        this.sap_tranz_nume = (String) inreg.get(SAP_TRANZ_NUME);
        this.doc_tip = (String) inreg.get(DOC_TIP);
        this.doc_tip_nume = (String) inreg.get(DOC_TIP_NUME);
        this.syst_logic = (String) inreg.get(SYST_LOGIC);
        this.app_oper = (String) inreg.get(APP_OPER);
        this.app_oper_nume = (String) inreg.get(APP_OPER_NUME);
        this.blocat = (Boolean) inreg.get(BLOCAT);
        this.ordine = (Short) inreg.get(ORDINE);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public ProcessMatrix(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    public ProcessMatrix(JsonObject json){
        try {
            this.parseJson(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(SAP_OPER) && !jsonO.isNull(SAP_OPER)) this.sap_oper = jsonO.getString(SAP_OPER);
        if (jsonO.containsKey(SAP_OPER_NUME) && !jsonO.isNull(SAP_OPER_NUME)) this.sap_oper_nume = jsonO.getString(SAP_OPER_NUME);
        if (jsonO.containsKey(SAP_TRANZ) && !jsonO.isNull(SAP_TRANZ)) this.sap_tranz = jsonO.getString(SAP_TRANZ);
        if (jsonO.containsKey(SAP_TRANZ_NUME) && !jsonO.isNull(SAP_TRANZ_NUME)) this.sap_tranz_nume = jsonO.getString(SAP_TRANZ_NUME);
        if (jsonO.containsKey(DOC_TIP) && !jsonO.isNull(DOC_TIP)) this.doc_tip = jsonO.getString(DOC_TIP);
        if (jsonO.containsKey(DOC_TIP_NUME) && !jsonO.isNull(DOC_TIP_NUME)) this.doc_tip_nume = jsonO.getString(DOC_TIP_NUME);
        if (jsonO.containsKey(SYST_LOGIC) && !jsonO.isNull(SYST_LOGIC)) this.syst_logic = jsonO.getString(SYST_LOGIC);
        if (jsonO.containsKey(APP_OPER) && !jsonO.isNull(APP_OPER)) this.app_oper = jsonO.getString(APP_OPER);
        if (jsonO.containsKey(APP_OPER_NUME) && !jsonO.isNull(APP_OPER_NUME)) this.id = jsonO.getString(APP_OPER_NUME);
        if (jsonO.containsKey(BLOCAT) && !jsonO.isNull(BLOCAT)) this.blocat = jsonO.getBoolean(BLOCAT);
        if (jsonO.containsKey(ORDINE) && !jsonO.isNull(ORDINE)) this.ordine = Short.parseShort(jsonO.getString(ORDINE));
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);
        if (this.sap_oper != null) jsonb.add(SAP_OPER, this.sap_oper);
        if (this.sap_oper_nume != null) jsonb.add(SAP_OPER_NUME, this.sap_oper_nume);
        if (this.sap_tranz != null) jsonb.add(SAP_TRANZ, this.sap_tranz);
        if (this.sap_tranz_nume != null) jsonb.add(SAP_TRANZ_NUME, this.sap_tranz_nume);
        if (this.doc_tip != null) jsonb.add(DOC_TIP, this.doc_tip);
        if (this.doc_tip_nume != null) jsonb.add(DOC_TIP_NUME, this.doc_tip_nume);
        if (this.syst_logic != null) jsonb.add(SYST_LOGIC, this.syst_logic);
        if (this.app_oper != null) jsonb.add(APP_OPER, this.app_oper);
        if (this.app_oper_nume != null) jsonb.add(APP_OPER_NUME, this.app_oper_nume);
        if (this.blocat != null) jsonb.add(BLOCAT, this.blocat);
        if (this.ordine != null) jsonb.add(ORDINE, this.ordine);
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSap_oper() {
        return sap_oper;
    }

    public void setSap_oper(String sap_oper) {
        this.sap_oper = sap_oper;
    }

    public String getSap_oper_nume() {
        return sap_oper_nume;
    }

    public void setSap_oper_nume(String sap_oper_nume) {
        this.sap_oper_nume = sap_oper_nume;
    }

    public String getSap_tranz() {
        return sap_tranz;
    }

    public void setSap_tranz(String sap_tranz) {
        this.sap_tranz = sap_tranz;
    }

    public String getSap_tranz_nume() {
        return sap_tranz_nume;
    }

    public void setSap_tranz_nume(String sap_tranz_nume) {
        this.sap_tranz_nume = sap_tranz_nume;
    }

    public String getDoc_tip() {
        return doc_tip;
    }

    public void setDoc_tip(String doc_tip) {
        this.doc_tip = doc_tip;
    }

    public String getDoc_tip_nume() {
        return doc_tip_nume;
    }

    public void setDoc_tip_nume(String doc_tip_nume) {
        this.doc_tip_nume = doc_tip_nume;
    }

    public String getSyst_logic() {
        return syst_logic;
    }

    public void setSyst_logic(String syst_logic) {
        this.syst_logic = syst_logic;
    }

    public String getApp_oper() {
        return app_oper;
    }

    public void setApp_oper(String app_oper) {
        this.app_oper = app_oper;
    }

    public String getApp_oper_nume() {
        return app_oper_nume;
    }

    public void setApp_oper_nume(String app_oper_nume) {
        this.app_oper_nume = app_oper_nume;
    }

    public Boolean getBlocat() {
        return blocat;
    }

    public void setBlocat(Boolean blocat) {
        this.blocat = blocat;
    }

    public Short getOrdine() {
        return ordine;
    }

    public void setOrdine(Short ordine) {
        this.ordine = ordine;
    }

    public String getMod_de() {
        return mod_de;
    }

    public void setMod_de(String mod_de) {
        this.mod_de = mod_de;
    }

    @SuppressWarnings("ReturnOfDateField")
    public Date getMod_timp() {
        return mod_timp;
    }

    @SuppressWarnings("AssignmentToDateFieldFromParameter")
    public void setMod_timp(Date mod_timp) {
        this.mod_timp = mod_timp;
    }
}
