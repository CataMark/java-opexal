package ro.any.c12153.opexal.entities;

import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
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
public class CoOrderSettleRule implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String ID = "id";
    private static final String COAREA = "coarea";
    private static final String COCODE = "cocode";
    private static final String AN = "an";
    private static final String LUNA = "luna";
    private static final String ORDER = "comanda";
    private static final String CSTCTR = "cost_center";
    private static final String PROCENT = "procent";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //non-table field names
    private static final String COCODE_NUME = "cocode_nume";
    private static final String ORDER_NUME = "comanda_nume";
    private static final String SEGMENT = "segment";
    private static final String CSTCTR_NUME = "cost_center_nume";
    
    private String id;
    private String coarea;
    private String cocode;
    private String cocode_nume;
    private Short an;
    private Short luna;
    private String order;
    private String order_nume;
    private String segment;
    private String cstctr;
    private String cstctr_nume;
    private BigDecimal procent;
    private String mod_de;
    private Date mod_timp;

    public CoOrderSettleRule() {
    }
    
    public CoOrderSettleRule(Map<String, Object> inreg) {
        this.id = (String) inreg.get(ID);
        this.coarea = (String) inreg.get(COAREA);
        this.cocode = (String) inreg.get(COCODE);
        this.cocode_nume = (String) inreg.get(COCODE_NUME);
        this.an = (Short) inreg.get(AN);
        this.luna = (Short) inreg.get(LUNA);
        this.order = (String) inreg.get(ORDER);
        this.order_nume = (String) inreg.get(ORDER_NUME);
        this.segment = (String) inreg.get(SEGMENT);
        this.cstctr = (String) inreg.get(CSTCTR);
        this.cstctr_nume = (String) inreg.get(CSTCTR_NUME);        
        this.procent = (BigDecimal) inreg.get(PROCENT);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public CoOrderSettleRule(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    public CoOrderSettleRule(JsonObject json){
        try {
            this.parseJson(json);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(COAREA) && !jsonO.isNull(COAREA)) this.coarea = jsonO.getString(COAREA);
        if (jsonO.containsKey(COCODE) && !jsonO.isNull(COCODE)) this.cocode = jsonO.getString(COCODE);
        if (jsonO.containsKey(COCODE_NUME) && !jsonO.isNull(COCODE_NUME)) this.cocode_nume = jsonO.getString(COCODE_NUME);
        if (jsonO.containsKey(AN) && !jsonO.isNull(AN)) this.an = Short.parseShort(jsonO.getString(AN));
        if (jsonO.containsKey(LUNA) && !jsonO.isNull(LUNA)) this.luna = Short.parseShort(jsonO.getString(LUNA));
        if (jsonO.containsKey(ORDER) && !jsonO.isNull(ORDER)) this.order = jsonO.getString(ORDER);
        if (jsonO.containsKey(ORDER_NUME) && !jsonO.isNull(ORDER_NUME)) this.order_nume = jsonO.getString(ORDER_NUME);
        if (jsonO.containsKey(SEGMENT) && !jsonO.isNull(SEGMENT)) this.segment = jsonO.getString(SEGMENT);
        if (jsonO.containsKey(CSTCTR) && !jsonO.isNull(CSTCTR)) this.cstctr = jsonO.getString(CSTCTR);
        if (jsonO.containsKey(CSTCTR_NUME) && !jsonO.isNull(CSTCTR_NUME)) this.cstctr_nume = jsonO.getString(CSTCTR_NUME);
        if (jsonO.containsKey(PROCENT) && !jsonO.isNull(PROCENT)) this.procent = new BigDecimal(jsonO.getString(PROCENT));
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);
        if (this.coarea != null) jsonb.add(COAREA, this.coarea);
        if (this.cocode != null) jsonb.add(COCODE, this.cocode);
        if (this.cocode_nume != null) jsonb.add(COCODE_NUME, this.cocode_nume);
        if (this.an != null) jsonb.add(AN, this.an);
        if (this.luna != null) jsonb.add(LUNA, this.luna);
        if (this.order != null) jsonb.add(ORDER, this.order);
        if (this.order_nume != null) jsonb.add(ORDER_NUME, this.order_nume);
        if (this.segment != null) jsonb.add(SEGMENT, this.segment);
        if (this.cstctr != null) jsonb.add(CSTCTR, this.cstctr);
        if (this.cstctr_nume != null) jsonb.add(CSTCTR_NUME, this.cstctr_nume);
        if (this.procent != null) jsonb.add(PROCENT, this.procent);
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

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrder_nume() {
        return order_nume;
    }

    public void setOrder_nume(String order_nume) {
        this.order_nume = order_nume;
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

    public BigDecimal getProcent() {
        return procent;
    }

    public void setProcent(BigDecimal procent) {
        this.procent = procent;
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
