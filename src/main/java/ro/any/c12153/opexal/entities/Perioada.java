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
public class Perioada implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String ID = "id";
    private static final String AN = "an";
    private static final String LUNA = "luna";
    private static final String INCHIS = "inchis";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    private String id;
    private Short an;
    private Short luna;
    private Boolean inchis;
    private String mod_de;
    private Date mod_timp;

    public Perioada(){
    }
    
    public Perioada(Map<String, Object> inreg){
        this.id = (String) inreg.get(ID);
        this.an = (Short) inreg.get(AN);
        this.luna = (Short) inreg.get(LUNA);
        this.inchis = (Boolean) inreg.get(INCHIS);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public Perioada(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    public Perioada(JsonObject json){
        try {
            this.parseJson(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(AN) && !jsonO.isNull(AN)) this.an = Short.parseShort(jsonO.get(AN).toString());
        if (jsonO.containsKey(LUNA) && !jsonO.isNull(LUNA)) this.luna = Short.parseShort(jsonO.get(LUNA).toString());
        if (jsonO.containsKey(INCHIS) && !jsonO.isNull(INCHIS)) this.inchis = jsonO.getBoolean(INCHIS);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);
        if (this.an != null) jsonb.add(AN, this.an);
        if (this.luna != null) jsonb.add(LUNA, this.luna);
        if (this.inchis != null) jsonb.add(INCHIS, this.inchis);
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

    public Short getAn() {
        return an;
    }

    public void setAn(Short an) {
        this.an = an;
    }

    public Short getLuna() {
        return luna;
    }

    public void setLuna(Short luna) {
        this.luna = luna;
    }

    public Boolean getInchis() {
        return inchis;
    }

    public void setInchis(Boolean inchis) {
        this.inchis = inchis;
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
