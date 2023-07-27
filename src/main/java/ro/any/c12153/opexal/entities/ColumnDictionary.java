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
public class ColumnDictionary implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String ID = "id";
    private static final String TRANZ = "sap_tranz";
    private static final String LANG = "lang";
    private static final String COD = "cod";
    private static final String NUME = "nume";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    private String id;
    private String tranz;
    private String lang;
    private String cod;
    private String nume;
    private String mod_de;
    private Date mod_timp;

    public ColumnDictionary() {
    }
    
    public ColumnDictionary(Map<String, Object> inreg) {
        this.id = (String) inreg.get(ID);
        this.tranz = (String) inreg.get(TRANZ);
        this.lang = (String) inreg.get(LANG);
        this.cod = (String) inreg.get(COD);
        this.nume = (String) inreg.get(NUME);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public ColumnDictionary(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    public ColumnDictionary(JsonObject json){
        try {
            this.parseJson(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(TRANZ) && !jsonO.isNull(TRANZ)) this.tranz = jsonO.getString(TRANZ);
        if (jsonO.containsKey(LANG) && !jsonO.isNull(LANG)) this.lang = jsonO.getString(LANG);
        if (jsonO.containsKey(COD) && !jsonO.isNull(COD)) this.cod = jsonO.getString(COD);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);
        if (this.tranz != null) jsonb.add(TRANZ, this.tranz);
        if (this.lang != null) jsonb.add(LANG, this.lang);
        if (this.cod != null) jsonb.add(COD, this.cod);        
        if (this.nume != null) jsonb.add(NUME, this.nume);           
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

    public String getTranz() {
        return tranz;
    }

    public void setTranz(String tranz) {
        this.tranz = tranz;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
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
