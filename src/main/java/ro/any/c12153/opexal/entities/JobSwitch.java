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
 * @author catalin
 */
public class JobSwitch implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //job code enum
    public static final String JOB_CLASSIFCATION_MODEL = "CREATE_TRAINING_MODEL";
    public static final String JOB_DELETE_OLD_RPA_FILES = "DELETE_OLD_RPA_FILES";
    public static final String JOB_OPEN_CURRENT_PERIOD = "OPEN_CURRENT_PERIOD";
    public static final String JOB_CHECK_OPENED_PERIODS = "CHECK_OPENED_PERIODS";
    public static final String JOB_PROCESS_RPA_FILES = "PROCESS_RPA_FILES";
    
    //database table field names
    private static final String COD = "cod";
    private static final String NUME = "nume";
    private static final String BLOCAT = "blocat";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    private String cod;
    private String nume;
    private Boolean blocat;
    private String mod_de;
    private Date mod_timp;

    public JobSwitch() {
    }
    
    public JobSwitch(Map<String, Object> inreg) {
        this.cod = (String) inreg.get(COD);
        this.nume = (String) inreg.get(NUME);
        this.blocat = (Boolean) inreg.get(BLOCAT);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public JobSwitch(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    public JobSwitch(JsonObject json){
        try {
            this.parseJson(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(COD) && !jsonO.isNull(COD)) this.cod = jsonO.getString(COD);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(BLOCAT) && !jsonO.isNull(BLOCAT)) this.blocat = jsonO.getBoolean(BLOCAT);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.cod != null) jsonb.add(COD, this.cod);        
        if (this.nume != null) jsonb.add(NUME, this.nume);
        if (this.blocat != null) jsonb.add(BLOCAT, this.blocat);
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Boolean getBlocat() {
        return blocat;
    }

    public void setBlocat(Boolean blocat) {
        this.blocat = blocat;
    }

    public String getMod_de() {
        return mod_de;
    }

    public void setMod_de(String mod_de) {
        this.mod_de = mod_de;
    }

    public Date getMod_timp() {
        return mod_timp;
    }

    public void setMod_timp(Date mod_timp) {
        this.mod_timp = mod_timp;
    }
}
