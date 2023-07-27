package ro.any.c12153.opexal.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.AccountInterval;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class AccountIntervalServ {
    
    public static Optional<AccountInterval> getById(String id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.VARCHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_acc_inteval_get_by_id(?);", Optional.of(parametri)).stream()
                .map(AccountInterval::new)
                .findFirst();
    }
    
    public static List<AccountInterval> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_acc_interval_get_all}", Optional.empty()).stream()
                .map(AccountInterval::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<AccountInterval> insert(AccountInterval inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("acc_start", new ParamSql(inreg.getStart(), Types.CHAR));
        parametri.put("acc_end", new ParamSql(inreg.getEnd(), Types.CHAR));
        parametri.put("proces", new ParamSql(inreg.getProces(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_acc_interval_insert_return(?,?,?,?)}", parametri).stream()
                .map(AccountInterval::new)
                .findFirst();
    }
    
    public static Optional<AccountInterval> update(AccountInterval inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.VARCHAR));
        parametri.put("acc_start", new ParamSql(inreg.getStart(), Types.CHAR));
        parametri.put("acc_end", new ParamSql(inreg.getEnd(), Types.CHAR));
        parametri.put("proces", new ParamSql(inreg.getProces(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_acc_interval_update_return(?,?,?,?,?)}", parametri).stream()
                .map(AccountInterval::new)
                .findFirst();
    }
    
    public static boolean detele(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_acc_interval_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void intervalsToJson(String userId, OutputStream writer) throws Exception{
        App.getConn(userId)
                .downloadFromPreparedStmtToJsonArray("exec oxal1.prc_acc_interval_get_just_intervals", Optional.empty(), writer);
    }
}
