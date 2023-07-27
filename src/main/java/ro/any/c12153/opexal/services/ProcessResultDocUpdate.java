package ro.any.c12153.opexal.services;

import java.io.Serializable;
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
 * @author catalin
 */
public class ProcessResultDocUpdate implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private final ColumnUpdateValueHolder<Integer> ocateg;

    public ProcessResultDocUpdate() {
        this.ocateg = new ColumnUpdateValueHolder<>("q1.ocateg", null, true, Types.INTEGER);
    }
    
    public boolean hasValues(){
        return this.ocateg.getValue() == null;
    }
    
    public String sqlUpdate(){
        List<Optional<String>> rezultat = new ArrayList<>();
        rezultat.add(sqlFieldUpdate(this.ocateg));
        return rezultat.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(", "));
    }
    
    public List<ParamSql> sqlParametri(){
        List<Optional<ParamSql>> rezultat = new ArrayList<>();
        rezultat.add(sqlFieldParametru(this.ocateg));
        return rezultat.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public ColumnUpdateValueHolder<Integer> getOcateg() {
        return ocateg;
    }
}
