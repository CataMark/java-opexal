package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.Flag;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class FlagServ{    
    
    public static Optional<Flag> getById(String id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.NVARCHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_flags_get_by_id(?);", Optional.of(parametri)).stream()
                .map(Flag::new)
                .findFirst();
    }
    
    public static List<Flag> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_flags_get_all}", Optional.empty()).stream()
                .map(Flag::new)
                .collect(Collectors.toList());
    }
    
    public static List<Flag> getByTip(String tipFlag, String userId) throws Exception{        
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("tip", new ParamSql(tipFlag, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_flags_get_by_tip(?)}", parametri).stream()
                .map(Flag::new)
                .collect(Collectors.toList());
    }
    
    public static List<Flag> getByTipAndKid(String tipFlag, String userId) throws Exception{        
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("tip", new ParamSql(tipFlag, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_flags_get_by_tip_and_kid(?,?)}", parametri).stream()
                .map(Flag::new)
                .collect(Collectors.toList());
    }
    
    public static List<Flag> getByTipAndCoarea(String tipFlag, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("tip", new ParamSql(tipFlag, Types.VARCHAR));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_flags_get_by_tip_and_coarea(?,?)}", parametri).stream()
                .map(Flag::new)
                .collect(Collectors.toList());
    }
    
    @SuppressWarnings({"unchecked"})
    public static Optional<Flag> insert(Flag inreg, String userId) throws Exception{        
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uuid", new ParamSql(inreg.getGuid(), Types.NVARCHAR));
        parametri.put("tip", new ParamSql(inreg.getTip(), Types.VARCHAR));
        parametri.put("coarea", new ParamSql(inreg.getCoarea(), Types.CHAR));
        parametri.put("sap_tranz", new ParamSql(inreg.getTranz(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_flags_insert_return(?,?,?,?,?)}", parametri).stream()
                .map(Flag::new)
                .findFirst();
    }
    
    public static boolean delete(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.NVARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_flags_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void delete(JsonArray uuids, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uuidArray", new ParamSql(uuids.toString(), Types.NVARCHAR));
        
        App.getConn(userId).executeCallableStatement("{call oxal1.prc_flags_delete_by_list(?)}", parametri);
    }
}
