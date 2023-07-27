package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.SapDocType;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class SapDocTypeServ {
    
    public static Optional<SapDocType> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_sap_doc_tip_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(SapDocType::new)
                .findFirst();
    }
    
    public static List<SapDocType> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_sap_doc_tip_get_list_all}", Optional.empty()).stream()
                .map(SapDocType::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<SapDocType> insert(SapDocType inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId,Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_sap_doc_tip_insert_return(?,?,?)}", parametri).stream()
                .map(SapDocType::new)
                .findFirst();
    }
    
    public static Optional<SapDocType> update(SapDocType inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_sap_doc_tip_update_return(?,?,?)}", parametri).stream()
                .map(SapDocType::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_sap_doc_tip_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
