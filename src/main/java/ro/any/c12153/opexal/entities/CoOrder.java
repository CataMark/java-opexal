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
public class CoOrder implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String COD = "cod";
    private static final String COAREA = "coarea";
    private static final String COCODE = "cocode";
    private static final String NUME = "nume";
    private static final String PRCTR = "profit_center";
    private static final String CSTCTR = "cost_center_resp";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //non-table filed names
    private static final String COCODE_NUME = "cocode_nume";
    private static final String SEGMENT = "segment";
    private static final String CSTCTR_NUME = "cost_center_resp_nume";
    
    private String cod;
    private String coarea;
    private String cocode;
    private String cocode_nume;
    private String nume;
    private String prctr;
    private String segment;
    private String cstctr;
    private String cstctr_nume;
    private String mod_de;
    private Date mod_timp;

    public CoOrder() {
    }
    
    
    public CoOrder(Map<String, Object> inreg) {
        this.cod = (String) inreg.get(COD);
        this.coarea = (String) inreg.get(COAREA);
        this.cocode = (String) inreg.get(COCODE);
        this.cocode_nume = (String) inreg.get(COCODE_NUME);
        this.nume = (String) inreg.get(NUME);
        this.prctr = (String) inreg.get(PRCTR);
        this.segment = (String) inreg.get(SEGMENT);
        this.cstctr = (String) inreg.get(CSTCTR);
        this.cstctr_nume = (String) inreg.get(CSTCTR_NUME);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public CoOrder(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    public CoOrder(JsonObject json){
        try {
            this.parseJson(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(COD) && !jsonO.isNull(COD)) this.cod = jsonO.getString(COD);
        if (jsonO.containsKey(COAREA) && !jsonO.isNull(COAREA)) this.coarea = jsonO.getString(COAREA);
        if (jsonO.containsKey(COCODE) && !jsonO.isNull(COCODE)) this.cocode = jsonO.getString(COCODE);
        if (jsonO.containsKey(COCODE_NUME) && !jsonO.isNull(COCODE_NUME)) this.cocode_nume = jsonO.getString(COCODE_NUME);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(PRCTR) && !jsonO.isNull(PRCTR)) this.prctr = jsonO.getString(PRCTR);        
        if (jsonO.containsKey(SEGMENT) && !jsonO.isNull(SEGMENT)) this.segment = jsonO.getString(SEGMENT);
        if (jsonO.containsKey(CSTCTR) && !jsonO.isNull(CSTCTR)) this.cstctr = jsonO.getString(CSTCTR);
        if (jsonO.containsKey(CSTCTR_NUME) && !jsonO.isNull(CSTCTR_NUME)) this.cstctr_nume = jsonO.getString(CSTCTR_NUME);        
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.cod != null) jsonb.add(COD, this.cod);
        if (this.coarea != null) jsonb.add(COAREA, this.coarea);
        if (this.cocode != null) jsonb.add(COCODE, this.cocode);
        if (this.cocode_nume != null) jsonb.add(COCODE_NUME, this.cocode_nume);
        if (this.nume != null) jsonb.add(NUME, this.nume);
        if (this.prctr != null) jsonb.add(PRCTR, this.prctr);
        if (this.segment != null) jsonb.add(SEGMENT, this.segment);
        if (this.cstctr != null) jsonb.add(CSTCTR, this.cstctr);
        if (this.cstctr_nume != null) jsonb.add(CSTCTR_NUME, this.cstctr_nume);
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

    public String getCoarea() {
        return coarea;
    }

    public void setCoarea(String coarea) {
        this.coarea = coarea;
    }

    public String getCocode() {
        return cocode;
    }

    public void setCocode(String cocode) {
        this.cocode = cocode;
    }

    public String getCocode_nume() {
        return cocode_nume;
    }

    public void setCocode_nume(String cocode_nume) {
        this.cocode_nume = cocode_nume;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrctr() {
        return prctr;
    }

    public void setPrctr(String prctr) {
        this.prctr = prctr;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getCstctr() {
        return cstctr;
    }

    public void setCstctr(String cstctr) {
        this.cstctr = cstctr;
    }

    public String getCstctr_nume() {
        return cstctr_nume;
    }

    public void setCstctr_nume(String cstctr_nume) {
        this.cstctr_nume = cstctr_nume;
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
