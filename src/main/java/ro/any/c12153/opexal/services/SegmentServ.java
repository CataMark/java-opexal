package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.Segment;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class SegmentServ {
    
    public static Optional<Segment> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.VARCHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_segment_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(Segment::new)
                .findFirst();
    }
    
    public static List<Segment> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_segment_get_all}", Optional.empty()).stream()
                .map(Segment::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<Segment> insert(Segment inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_segment_insert_return(?,?)}", parametri).stream()
                .map(Segment::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.VARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_segment_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
