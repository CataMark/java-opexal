package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.Keyword;
import ro.any.c12153.shared.App;

/**
 *
 * @author catalin
 */
public class KeywordServ {
    
    public static Optional<Keyword> getById(Integer id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.INTEGER);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_key_word_get_by_id(?);", Optional.of(parametri)).stream()
                .map(Keyword::new)
                .findFirst();
    }
    
    public static List<Keyword> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_key_word_get_all}", Optional.empty()).stream()
                .map(Keyword::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<Keyword> insert(Keyword inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("key_word", new ParamSql(inreg.getKword(), Types.NVARCHAR));
        parametri.put("acronim", new ParamSql(inreg.getAcronim(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_key_word_insert_return(?,?,?)}", parametri).stream()
                .map(Keyword::new)
                .findFirst();
    }
    
    public static boolean delete(Integer id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.INTEGER));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_key_word_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
