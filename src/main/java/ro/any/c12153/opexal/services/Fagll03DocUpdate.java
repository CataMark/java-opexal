package ro.any.c12153.opexal.services;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static ro.any.c12153.dbutils.JsfLazyDataModel.ColumnUpdateUtils.sqlFieldParametru;
import static ro.any.c12153.dbutils.JsfLazyDataModel.ColumnUpdateUtils.sqlFieldUpdate;
import ro.any.c12153.dbutils.JsfLazyDataModel.ColumnUpdateValueHolder;
import ro.any.c12153.dbutils.helpers.ParamSql;

/**
 *
 * @author C12153
 */
public class Fagll03DocUpdate implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private final ColumnUpdateValueHolder<String> profit_center;
    private final ColumnUpdateValueHolder<String> cost_center;
    private final ColumnUpdateValueHolder<String> order;
    private final ColumnUpdateValueHolder<String> wbs;
    private final ColumnUpdateValueHolder<String> ic_part;
    private final ColumnUpdateValueHolder<String> buss_tranz;
    private final ColumnUpdateValueHolder<String> sap_tranz;
    private final ColumnUpdateValueHolder<String> doc_tip;
    private final ColumnUpdateValueHolder<String> cont_ccoa;
    private final ColumnUpdateValueHolder<String> doc_text;
    private final ColumnUpdateValueHolder<String> head_text;
    private final ColumnUpdateValueHolder<String> material;
    private final ColumnUpdateValueHolder<String> vendor;

    public Fagll03DocUpdate() {
        this.profit_center = new ColumnUpdateValueHolder<>("q.prctr", null, true, Types.CHAR);
        this.cost_center = new ColumnUpdateValueHolder<>("q.kostl", null, false, Types.CHAR);
        this.order = new ColumnUpdateValueHolder<>("q.aufnr", null, false, Types.VARCHAR);
        this.wbs = new ColumnUpdateValueHolder<>("q.projk", null, false, Types.VARCHAR);
        this.ic_part = new ColumnUpdateValueHolder<>("q.vbund", null, false, Types.VARCHAR);
        this.buss_tranz = new ColumnUpdateValueHolder<>("q.glvor", null, true, Types.CHAR);
        this.sap_tranz = new ColumnUpdateValueHolder<>("q.u_tcode", null, false, Types.VARCHAR);
        this.doc_tip = new ColumnUpdateValueHolder<>("q.blart", null, true, Types.CHAR);
        this.cont_ccoa = new ColumnUpdateValueHolder<>("q.konto", null, true, Types.CHAR);
        this.doc_text = new ColumnUpdateValueHolder<>("q.sgtxt", null, false, Types.NVARCHAR);
        this.head_text = new ColumnUpdateValueHolder<>("q.u_bktxt", null, false, Types.NVARCHAR);
        this.material = new ColumnUpdateValueHolder<>("q.u_matnr", null, false, Types.CHAR);
        this.vendor = new ColumnUpdateValueHolder<>("q.u_lifnr", null, false, Types.CHAR);
    }
    
    public boolean hasValues() throws Exception{
        boolean rezultat = false;
        
        for(Field x : this.getClass().getDeclaredFields()){
            if (x.get(this) == null || !ColumnUpdateValueHolder.class.isInstance(x.get(this))) continue;
            ColumnUpdateValueHolder<?> camp = (ColumnUpdateValueHolder) x.get(this);
            
            rezultat = camp.isGoleste() || (camp.getValue() != null && (String.class.isInstance(camp.getValue()) ? !((String) camp.getValue()).isEmpty() : true));
            if (rezultat) break;          
        }
        return rezultat;
    }
    
    public String sqlUpdate() throws Exception{
        List<Optional<String>> rezultat = new ArrayList<>();
        
        for(Field x : this.getClass().getDeclaredFields()){
            if (x.get(this) == null || !ColumnUpdateValueHolder.class.isInstance(x.get(this))) continue;
            ColumnUpdateValueHolder<?> camp = (ColumnUpdateValueHolder) x.get(this);
            
            rezultat.add(sqlFieldUpdate(camp));
        }        
        return rezultat.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(", "));
    }
    
    public List<ParamSql> sqlParametri() throws Exception{
        List<Optional<ParamSql>> rezultat = new ArrayList<>();
        
        for(Field x : this.getClass().getDeclaredFields()){
            if (x.get(this) == null || !ColumnUpdateValueHolder.class.isInstance(x.get(this))) continue;
            ColumnUpdateValueHolder<?> camp = (ColumnUpdateValueHolder) x.get(this);
            
            rezultat.add(sqlFieldParametru(camp));
        }
        return rezultat.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public ColumnUpdateValueHolder<String> getProfit_center() {
        return profit_center;
    }

    public ColumnUpdateValueHolder<String> getCost_center() {
        return cost_center;
    }

    public ColumnUpdateValueHolder<String> getOrder() {
        return order;
    }

    public ColumnUpdateValueHolder<String> getWbs() {
        return wbs;
    }

    public ColumnUpdateValueHolder<String> getIc_part() {
        return ic_part;
    }

    public ColumnUpdateValueHolder<String> getBuss_tranz() {
        return buss_tranz;
    }

    public ColumnUpdateValueHolder<String> getSap_tranz() {
        return sap_tranz;
    }

    public ColumnUpdateValueHolder<String> getDoc_tip() {
        return doc_tip;
    }

    public ColumnUpdateValueHolder<String> getCont_ccoa() {
        return cont_ccoa;
    }

    public ColumnUpdateValueHolder<String> getDoc_text() {
        return doc_text;
    }

    public ColumnUpdateValueHolder<String> getHead_text() {
        return head_text;
    }

    public ColumnUpdateValueHolder<String> getMaterial() {
        return material;
    }

    public ColumnUpdateValueHolder<String> getVendor() {
        return vendor;
    }
}
