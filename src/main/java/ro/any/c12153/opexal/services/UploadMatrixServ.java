package ro.any.c12153.opexal.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.UploadMatrix;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class UploadMatrixServ {
    
    public static Optional<UploadMatrix> getById(String id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.VARCHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_upload_matrix_get_by_id(?);", Optional.of(parametri)).stream()
                .map(UploadMatrix::new)
                .findFirst();
    }
    
    public static List<UploadMatrix> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_upload_matrix_get_all}", Optional.empty()).stream()
                .map(UploadMatrix::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<UploadMatrix> insert(UploadMatrix inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cocode", new ParamSql(inreg.getCocode(), Types.CHAR));
        parametri.put("sap_tranz", new ParamSql(inreg.getTranz(), Types.VARCHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_upload_matrix_insert_return(?,?,?,?)}", parametri).stream()
                .map(UploadMatrix::new)
                .findFirst();
    }
    
    public static Optional<UploadMatrix> update(UploadMatrix inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.VARCHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_upload_matrix_update_return(?,?,?)}", parametri).stream()
                .map(UploadMatrix::new)
                .findFirst();
    }
    
    public static boolean delete(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_upload_matrix_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
