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
public class AccountInterval implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table fields name
    private static final String ID = "id";
    private static final String START = "acc_start";
    private static final String END = "acc_end";
    private static final String PROCES = "process";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //non table field names
    private static final String START_NUME = "acc_start_nume";
    private static final String END_NUME = "acc_end_nume";
    
    private String id;
    private String start;
    private String start_nume;
    private String end;
    private String end_nume;
    private Boolean proces;
    private String mod_de;
    private Date mod_timp;

    public AccountInterval(){
    }
    
    public AccountInterval(Map<String, Object> inreg){
        this.id = (String) inreg.get(ID);
        this.start = (String) inreg.get(START);
        this.start_nume = (String) inreg.get(START_NUME);
        this.end = (String) inreg.get(END);
        this.end_nume = (String) inreg.get(END_NUME);
        this.proces = (Boolean) inreg.get(PROCES);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public AccountInterval(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    public AccountInterval(JsonObject json){
        try {
            this.parseJson(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(START) && !jsonO.isNull(START)) this.start = jsonO.getString(START);
        if (jsonO.containsKey(START_NUME) && !jsonO.isNull(START_NUME)) this.start = jsonO.getString(START_NUME);
        if (jsonO.containsKey(END) && !jsonO.isNull(END)) this.end = jsonO.getString(END);
        if (jsonO.containsKey(END_NUME) && !jsonO.isNull(END_NUME)) this.end = jsonO.getString(END_NUME);
        if (jsonO.containsKey(PROCES) && !jsonO.isNull(PROCES)) this.proces = jsonO.getBoolean(PROCES);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));        
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);
        if (this.start != null) jsonb.add(START, this.start);
        if (this.start_nume != null) jsonb.add(START_NUME, this.start_nume);
        if (this.end != null) jsonb.add(END, this.end);
        if (this.end_nume != null) jsonb.add(END_NUME, this.end_nume);
        if (this.proces != null) jsonb.add(PROCES, this.proces);
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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStart_nume() {
        return start_nume;
    }

    public void setStart_nume(String start_nume) {
        this.start_nume = start_nume;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getEnd_nume() {
        return end_nume;
    }

    public void setEnd_nume(String end_nume) {
        this.end_nume = end_nume;
    }

    public Boolean getProces() {
        return proces;
    }

    public void setProces(Boolean proces) {
        this.proces = proces;
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
