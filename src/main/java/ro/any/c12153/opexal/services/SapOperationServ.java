package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.SapOperation;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class SapOperationServ {
    
    public static Optional<SapOperation> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_sap_oper_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(SapOperation::new)
                .findFirst();
    }
    
    public static List<SapOperation> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_sap_oper_get_list_all}", Optional.empty()).stream()
                .map(SapOperation::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<SapOperation> insert(SapOperation inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId,Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_sap_oper_insert_return(?,?,?)}", parametri).stream()
                .map(SapOperation::new)
                .findFirst();
    }
    
    public static Optional<SapOperation> update(SapOperation inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_sap_oper_update_return(?,?,?)}", parametri).stream()
                .map(SapOperation::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_sap_oper_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
