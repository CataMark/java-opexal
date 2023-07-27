package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.CostDriver;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class CostDriverServ {
    
    public static List<CostDriver> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.vw_cost_driver;", Optional.empty()).stream()
                .map(CostDriver::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<CostDriver> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_cost_driver_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(CostDriver::new)
                .findFirst();
    }
}
