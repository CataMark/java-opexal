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
public class Keyword implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table filed names
    private static final String ID = "id";
    private static final String KWORD = "key_word";
    private static final String ACRONIM = "acronim";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    private Integer id;
    private String kword;
    private Boolean acronim;
    private String mod_de;
    private Date mod_timp;

    public Keyword() {
    }
    
    public Keyword(Map<String, Object> inreg) {
        this.id = (Integer) inreg.get(ID);
        this.kword = (String) inreg.get(KWORD);
        this.acronim = (Boolean) inreg.get(ACRONIM);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public Keyword(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    public Keyword(JsonObject json){
        try {
            this.parseJson(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getInt(ID);
        if (jsonO.containsKey(KWORD) && !jsonO.isNull(KWORD)) this.kword = jsonO.getString(KWORD);
        if (jsonO.containsKey(ACRONIM) && !jsonO.isNull(ACRONIM)) this.acronim = jsonO.getBoolean(ACRONIM);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);
        if (this.kword != null) jsonb.add(KWORD, this.kword);
        if (this.acronim != null) jsonb.add(ACRONIM, this.acronim);
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKword() {
        return kword;
    }

    public void setKword(String kword) {
        this.kword = kword;
    }

    public Boolean getAcronim() {
        return acronim;
    }

    public void setAcronim(Boolean acronim) {
        this.acronim = acronim;
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
