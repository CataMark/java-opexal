package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.JobSwitch;
import ro.any.c12153.shared.App;

/**
 *
 * @author catalin
 */
public class JobSwitchServ {
    
    public static Optional<JobSwitch> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.VARCHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_job_switch_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(JobSwitch::new)
                .findFirst();
    }
    
    public static List<JobSwitch> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_job_switch_get_all}", Optional.empty()).stream()
                .map(JobSwitch::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<JobSwitch> update(JobSwitch inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_job_switch_update_return(?,?,?)}", parametri).stream()
                .map(JobSwitch::new)
                .findFirst();
    }
}
