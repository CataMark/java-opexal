package ro.any.c12153.opexal.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ro.any.c12153.dbutils.JsfLazyDataModel.LazyDataModelRecords;
import static ro.any.c12153.dbutils.JsfLazyDataModel.LazyRecordsUtils.getFilterSql;
import static ro.any.c12153.dbutils.JsfLazyDataModel.LazyRecordsUtils.getSortSql;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.Wbs;
import ro.any.c12153.shared.App;

/**
 *
 * @author catalin
 */
public class WbsServ {
    
    public static Optional<Wbs> getByCod (String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.VARCHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_wbs_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(Wbs::new)
                .findFirst();
    }
    
    public static LazyDataModelRecords<Wbs> getLazyRecords(String coarea, int after, int size,
            Optional<Map<String, String>> sort, Optional<Map<String, String>> filter, String userId) throws Exception{
        
        //pregatire baza sql
        String sqlBase = String.format(App.getSql("wbs_lazy_base"),
                (sort.isPresent() ? getSortSql(sort.get()) : ""), (filter.isPresent() ? getFilterSql(filter.get()) : "")
        );
        ParamSql[] paramBase = new ParamSql[]{new ParamSql(coarea, Types.CHAR)};
        
        //obtine inregistrari
        String sqlRecs = String.format(App.getSql("recs_lazy_get_list"), size, sqlBase);
        ParamSql[] paramRecs = Stream.concat(Arrays.stream(paramBase), Arrays.stream(new ParamSql[]{new ParamSql(after, Types.INTEGER)}))
                .toArray(ParamSql[]::new);
        CompletableFuture<List<Wbs>> fRecords = CompletableFuture.supplyAsync(() -> {
            List<Wbs> result = new ArrayList<>();
            try {
                result = App.getConn(userId).getFromPreparedStatement(sqlRecs, Optional.of(paramRecs)).stream()
                        .map(Wbs::new)
                        .collect(Collectors.toList());
            } catch (Exception ex) {
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }
            return result;
        });
        
        //obtine numar pozitii
        String sqlPoz = String.format(App.getSql("recs_lazy_get_count"), sqlBase);
        CompletableFuture<Integer> fPozitii = CompletableFuture.supplyAsync(() -> {
            int result = 0;
            try {
                result = (int) App.getConn(userId)
                        .getFromPreparedStatement(sqlPoz, Optional.of(paramBase)).stream()
                        .flatMap(x -> x.values().stream())
                        .findFirst()
                        .orElse(0);
            } catch (Exception ex) {
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }
            return result;
        });
        
        LazyDataModelRecords<Wbs> rezultat = new LazyDataModelRecords<>();
        rezultat.setRecords(fRecords.get(30, TimeUnit.SECONDS));
        rezultat.setPozitii(fPozitii.get(30, TimeUnit.SECONDS));
        return rezultat;        
    }
    
    public static Optional<Wbs> insert(Wbs inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));
        parametri.put("definitie", new ParamSql(inreg.getDefinitie(), Types.CHAR));
        parametri.put("coarea", new ParamSql(inreg.getCoarea(), Types.CHAR));
        parametri.put("cocode", new ParamSql(inreg.getCocode(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("profit_center", new ParamSql(inreg.getPrctr(), Types.CHAR));
        parametri.put("cost_center_resp", new ParamSql(inreg.getCstctr(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_wbs_insert_return(?,?,?,?,?,?,?,?)}", parametri).stream()
                .map(Wbs::new)
                .findFirst();
    }
    
    public static Optional<Wbs> update(Wbs inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));
        parametri.put("definitie", new ParamSql(inreg.getDefinitie(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("profit_center", new ParamSql(inreg.getPrctr(), Types.CHAR));
        parametri.put("cost_center_resp", new ParamSql(inreg.getCstctr(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_wbs_update_return(?,?,?,?,?,?)}", parametri).stream()
                .map(Wbs::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.VARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_wbs_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void mergeLoad(UUID load_uuid, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("load_id", new ParamSql(load_uuid.toString(), Types.NVARCHAR));
        
        App.getConn(userId)
                .executeCallableStatement("{call oxal1.prc_wbs_merge_load(?)}", parametri);
    }
    
    public static void toXlsx(String coarea, Optional<Map<String, String>> filter, String userId, OutputStream out) throws Exception{
        String sql = String.format(App.getSql("wbs_lazy_get_all_by_filter"), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(coarea, Types.CHAR);
        
        App.getConn(userId).downloadFromPreparedStmtToXLSX(sql, Optional.of(parametri), out);
    }
}
