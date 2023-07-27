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
public class Ksb1DocUpdate implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private final ColumnUpdateValueHolder<String> cost_center;
    private final ColumnUpdateValueHolder<String> cost_center_nume;
    private final ColumnUpdateValueHolder<String> ic_partener;
    private final ColumnUpdateValueHolder<String> obj_part_tip;
    private final ColumnUpdateValueHolder<String> obj_part_cod;
    private final ColumnUpdateValueHolder<String> obj_part_nume;
    private final ColumnUpdateValueHolder<String> aux_obj;
    private final ColumnUpdateValueHolder<String> buss_tranz;
    private final ColumnUpdateValueHolder<String> orig_tranz;
    private final ColumnUpdateValueHolder<String> doc_tip;
    private final ColumnUpdateValueHolder<String> cont;
    private final ColumnUpdateValueHolder<String> cont_nume;
    private final ColumnUpdateValueHolder<String> doc_header;
    private final ColumnUpdateValueHolder<String> doc_nume;
    private final ColumnUpdateValueHolder<String> material;
    private final ColumnUpdateValueHolder<String> material_nume;

    public Ksb1DocUpdate() {
        this.cost_center = new ColumnUpdateValueHolder<>("q.kostl", null, true, Types.CHAR);
        this.cost_center_nume = new ColumnUpdateValueHolder<>("q.obj_txt", null, false, Types.NVARCHAR);
        this.ic_partener = new ColumnUpdateValueHolder<>("q.vbund", null, false, Types.VARCHAR);
        this.obj_part_tip = new ColumnUpdateValueHolder<>("q.pobart", null, false, Types.CHAR);
        this.obj_part_cod = new ColumnUpdateValueHolder<>("q.pobid", null, false, Types.VARCHAR);
        this.obj_part_nume = new ColumnUpdateValueHolder<>("q.pob_txt", null, false, Types.NVARCHAR);
        this.aux_obj = new ColumnUpdateValueHolder<>("q.objnr_n1", null, false, Types.VARCHAR);
        this.buss_tranz = new ColumnUpdateValueHolder<>("q.vrgng", null, true, Types.CHAR);
        this.orig_tranz = new ColumnUpdateValueHolder<>("q.orgvg", null, false, Types.CHAR);
        this.doc_tip = new ColumnUpdateValueHolder<>("q.blart", null, false, Types.CHAR);
        this.cont = new ColumnUpdateValueHolder<>("q.kstar", null, true, Types.CHAR);
        this.cont_nume = new ColumnUpdateValueHolder<>("q.cel_ktxt", null, false, Types.NVARCHAR);
        this.doc_header = new ColumnUpdateValueHolder<>("q.bltxt", null, false, Types.NVARCHAR);
        this.doc_nume = new ColumnUpdateValueHolder<>("q.sgtxt", null, false, Types.NVARCHAR);
        this.material = new ColumnUpdateValueHolder<>("q.matnr", null, false, Types.CHAR);
        this.material_nume = new ColumnUpdateValueHolder<>("q.mat_txt", null, false, Types.NVARCHAR);
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

    public ColumnUpdateValueHolder<String> getCost_center() {
        return cost_center;
    }

    public ColumnUpdateValueHolder<String> getCost_center_nume() {
        return cost_center_nume;
    }

    public ColumnUpdateValueHolder<String> getIc_partener() {
        return ic_partener;
    }

    public ColumnUpdateValueHolder<String> getObj_part_tip() {
        return obj_part_tip;
    }

    public ColumnUpdateValueHolder<String> getObj_part_cod() {
        return obj_part_cod;
    }

    public ColumnUpdateValueHolder<String> getObj_part_nume() {
        return obj_part_nume;
    }

    public ColumnUpdateValueHolder<String> getAux_obj() {
        return aux_obj;
    }

    public ColumnUpdateValueHolder<String> getBuss_tranz() {
        return buss_tranz;
    }

    public ColumnUpdateValueHolder<String> getOrig_tranz() {
        return orig_tranz;
    }

    public ColumnUpdateValueHolder<String> getDoc_tip() {
        return doc_tip;
    }

    public ColumnUpdateValueHolder<String> getCont() {
        return cont;
    }

    public ColumnUpdateValueHolder<String> getCont_nume() {
        return cont_nume;
    }

    public ColumnUpdateValueHolder<String> getDoc_header() {
        return doc_header;
    }

    public ColumnUpdateValueHolder<String> getDoc_nume() {
        return doc_nume;
    }

    public ColumnUpdateValueHolder<String> getMaterial() {
        return material;
    }

    public ColumnUpdateValueHolder<String> getMaterial_nume() {
        return material_nume;
    }
}
