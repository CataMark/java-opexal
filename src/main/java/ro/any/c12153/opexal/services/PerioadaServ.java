package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class PerioadaServ {
    
    public static Optional<Perioada> getById(String id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[]{new ParamSql(id, Types.VARCHAR)};
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_perioade_get_by_id(?);", Optional.of(parametri)).stream()
                .map(Perioada::new)
                .findFirst();
    }
    
    public static List<Short> getAni(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_perioade_get_ani}", Optional.empty()).stream()
                .flatMap(x -> x.values().stream())
                .map(x -> ((Number) x).shortValue())
                .collect(Collectors.toList());
    }
    
    public static List<Short> getOpenedAni(Date laData, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("laData", new ParamSql(laData, Types.DATE));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_perioade_get_open_years_by_date(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .map(x -> ((Number) x).shortValue())
                .collect(Collectors.toList());
    }
    
    public static List<Perioada> getByAn(Short an, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("an", new ParamSql(an, Types.SMALLINT));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_perioade_get_by_an(?)}", parametri).stream()
                .map(Perioada::new)
                .collect(Collectors.toList());
    }
    
    public static List<Perioada> getOpened(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_perioade_get_opened}", Optional.empty()).stream()
                .map(Perioada::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<Perioada> getLastClosed(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_perioade_get_last_closed}", Optional.empty()).stream()
                .map(Perioada::new)
                .findFirst();
    }
    
    public static Optional<Perioada> insert(Perioada inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("an", new ParamSql(inreg.getAn(), Types.SMALLINT));
        parametri.put("luna", new ParamSql(inreg.getLuna(), Types.TINYINT));
        parametri.put("inchis", new ParamSql(inreg.getInchis(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_perioade_insert_return(?,?,?,?)}", parametri).stream()
                .map(Perioada::new)
                .findFirst();
    }
    
    public static Optional<Perioada> update(Perioada inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.VARCHAR));
        parametri.put("inchis", new ParamSql(inreg.getInchis(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_perioade_update_return(?,?,?)}", parametri).stream()
                .map(Perioada::new)
                .findFirst();
    }
    
    public static boolean delete(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_perioade_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
