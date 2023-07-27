package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.CoCode;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class CoCodeServ {
    public static List<CoCode> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_cocode_get_list_all}", Optional.empty()).stream()
                .map(CoCode::new)
                .collect(Collectors.toList());
    }
    
    public static List<CoCode> getAllByCoarea(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_cocode_get_by_coarea(?)}", parametri).stream()
                .map(CoCode::new)
                .collect(Collectors.toList());
    }

    public static Optional<CoCode> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_cocode_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(CoCode::new)
                .findFirst();
    }
    
    public static Optional<CoCode> insert(CoCode inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("coarea", new ParamSql(inreg.getCoarea().getCod(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId,Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_cocode_insert_return(?,?,?,?)}", parametri).stream()
                .map(CoCode::new)
                .findFirst();
    }
    
    public static Optional<CoCode> update(CoCode inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_cocode_update_return(?,?,?)}", parametri).stream()
                .map(CoCode::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_cocode_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}