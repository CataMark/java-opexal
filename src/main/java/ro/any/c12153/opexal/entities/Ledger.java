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
public class Ledger implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table fields name
    private static final String ID = "id";
    private static final String LEDGER = "ledger";
    private static final String GRUP = "grup";
    private static final String STANDARD = "stndrd";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    private String id;
    private String ledger;
    private String grup;
    private String standard;
    private String mod_de;
    private Date mod_timp;

    public Ledger(){
    }
    
    public Ledger(Map<String, Object> inreg){
        this.id = (String) inreg.get(ID);
        this.ledger = (String) inreg.get(LEDGER);
        this.grup = (String) inreg.get(GRUP);
        this.standard = (String) inreg.get(STANDARD);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public Ledger(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    public Ledger(JsonObject json){
        try {
            this.parseJson(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(LEDGER) && ! jsonO.isNull(LEDGER)) this.ledger = jsonO.getString(LEDGER);
        if (jsonO.containsKey(GRUP) && !jsonO.isNull(GRUP)) this.grup = jsonO.getString(GRUP);
        if (jsonO.containsKey(STANDARD) && !jsonO.isNull(STANDARD)) this.standard = jsonO.getString(STANDARD);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));        
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);
        if (this.ledger != null) jsonb.add(LEDGER, this.ledger);
        if (this.grup != null) jsonb.add(GRUP, this.grup);
        if (this.standard != null) jsonb.add(STANDARD, this.standard);
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

    public String getLedger() {
        return ledger;
    }

    public void setLedger(String ledger) {
        this.ledger = ledger;
    }

    public String getGrup() {
        return grup;
    }

    public void setGrup(String grup) {
        this.grup = grup;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
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
