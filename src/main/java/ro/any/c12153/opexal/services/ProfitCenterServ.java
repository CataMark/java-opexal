package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.ProfitCenter;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class ProfitCenterServ {
    
    public static Optional<ProfitCenter> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_profit_center_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(ProfitCenter::new)
                .findFirst();
    }
    
    public static List<ProfitCenter> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_profit_center_get_all}", Optional.empty()).stream()
                .map(ProfitCenter::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<ProfitCenter> insert(ProfitCenter inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("segment", new ParamSql(inreg.getSegment(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_profit_center_insert_return(?,?,?)}", parametri).stream()
                .map(ProfitCenter::new)
                .findFirst();
    }
    
    public static Optional<ProfitCenter> update(ProfitCenter inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("segment", new ParamSql(inreg.getSegment(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_profit_center_update_return(?,?,?)}", parametri).stream()
                .map(ProfitCenter::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws  Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_profit_center_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void mergeLoad(UUID load_uuid, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("load_id", new ParamSql(load_uuid.toString(), Types.NVARCHAR));
        
        App.getConn(userId)
                .executeCallableStatement("{call oxal1.prc_profit_center_merge_load(?)}", parametri);
    }
}
