package ro.any.c12153.opexal.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.OpexCategory;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class OpexCategoryServ {
    
    public static List<OpexCategory> getByCdriver(String cdriver, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cdriver", new ParamSql(cdriver, Types.CHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_opex_categ_get_by_cdriver(?)}", parametri).stream()
                .map(OpexCategory::new)
                .collect(Collectors.toList());
    }
    
    public static void listByCdriverToXlsx(String cdriver, String userId, OutputStream writer) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cdriver, Types.CHAR);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxal1.prc_opex_categ_get_by_cdriver @cdriver = ?;", Optional.of(parametri), writer);
    }
    
    public static void allToXlsx(String userId, OutputStream writer) throws Exception{
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("select * from oxal1.vw_opex_categ;", Optional.empty(), writer);
    }
}
