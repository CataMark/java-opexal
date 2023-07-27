package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.ColumnDictionary;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class ColumnDictionaryServ {
    
    public static Optional<ColumnDictionary> getById(String id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.NVARCHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_mrecs_col_get_by_id(?);", Optional.of(parametri)).stream()
                .map(ColumnDictionary::new)
                .findFirst();
    }
    
    public static List<ColumnDictionary> getByTranz(String tranz, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("sap_tranz", new ParamSql(tranz, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_mrecs_col_get_by_tranz(?)}", parametri).stream()
                .map(ColumnDictionary::new)
                .collect(Collectors.toList());
    }
    
    public static List<ColumnDictionary> getByTranzAndLang(String tranz, String lang, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("sap_tranz", new ParamSql(tranz, Types.VARCHAR));
        parametri.put("lang", new ParamSql(lang, Types.CHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_mrecs_col_get_by_tranz_and_lang(?,?)}", parametri).stream()
                .map(ColumnDictionary::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<ColumnDictionary> insert(ColumnDictionary inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("sap_tranz", new ParamSql(inreg.getTranz(), Types.VARCHAR));
        parametri.put("lang", new ParamSql(inreg.getLang(), Types.CHAR));
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_mrecs_col_insert_return(?,?,?,?,?)}", parametri).stream()
                .map(ColumnDictionary::new)
                .findFirst();
    }
    
    public static Optional<ColumnDictionary> update(ColumnDictionary inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.NVARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_mrecs_col_update_return(?,?,?)}", parametri).stream()
                .map(ColumnDictionary::new)
                .findFirst();
    }
    
    public static boolean delete(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.NVARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_mrecs_col_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void mergeLoad(UUID load_uuid, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("load_id", new ParamSql(load_uuid.toString(), Types.NVARCHAR));
        
        App.getConn(userId)
                .executeCallableStatement("{call oxal1.prc_mrecs_col_merge_load(?)}", parametri);
    }
}
