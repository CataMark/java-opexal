package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.Operation;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class OperationServ {
    
    public static List<Operation> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_operation_get_all}", Optional.empty()).stream()
                .map(Operation::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<Operation> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_operation_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(Operation::new)
                .findFirst();
    }
}
