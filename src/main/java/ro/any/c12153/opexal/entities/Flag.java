package ro.any.c12153.opexal.entities;

import java.io.StringReader;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
public class Flag {
    private static final long serialVersionUID = 1L;
    
    @SuppressWarnings("PublicInnerClass")
    public static class Tip{
        public static final String UPLOAD = "mrecs_upload";
    }
    
    //database table field names
    private static final String ID = "id";
    private static final String GUID = "uuid";
    private static final String TIP = "tip";
    private static final String COAREA = "coarea";
    private static final String TRANZ = "sap_tranz";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    private String id;
    private String guid;
    private String tip;
    private String coarea;
    private String tranz;
    private String mod_de;
    private Date mod_timp;

    public Flag() {
    }

    public Flag(String guid, String tip, String coarea, String tranz) {
        this.guid = guid;
        this.tip = tip;
        this.coarea = coarea;
        this.tranz = tranz;
    }
    
    public Flag(Map<String, Object> inreg) {
        this.id = (String) inreg.get(ID);
        this.guid = (String) inreg.get(GUID);
        this.tip = (String) inreg.get(TIP);
        this.coarea = (String) inreg.get(COAREA);
        this.tranz = (String) inreg.get(TRANZ);        
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public Flag(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    public Flag(JsonObject json){
        try {
            this.parseJson(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(GUID) && !jsonO.isNull(GUID)) this.guid = jsonO.getString(GUID);
        if (jsonO.containsKey(TIP) && !jsonO.isNull(TIP)) this.tip = jsonO.getString(TIP);
        if (jsonO.containsKey(COAREA) && !jsonO.isNull(COAREA)) this.coarea = jsonO.getString(COAREA);
        if (jsonO.containsKey(TRANZ) && !jsonO.isNull(TRANZ)) this.tranz = jsonO.getString(TRANZ);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);
        if (this.guid != null) jsonb.add(GUID, this.guid);
        if (this.tip != null) jsonb.add(TIP, this.tip);
        if (this.coarea != null) jsonb.add(COAREA, this.coarea);
        if (this.tranz != null) jsonb.add(TRANZ, this.tranz);
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (this.getClass().isInstance(o)) return o.hashCode() == this.hashCode();
        return false;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getCoarea() {
        return coarea;
    }

    public void setCoarea(String coarea) {
        this.coarea = coarea;
    }

    public String getTranz() {
        return tranz;
    }

    public void setTranz(String tranz) {
        this.tranz = tranz;
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
