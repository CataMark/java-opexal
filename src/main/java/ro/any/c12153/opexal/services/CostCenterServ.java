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
import ro.any.c12153.opexal.entities.CostCenter;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class CostCenterServ {
    
    public static Optional<CostCenter> getByCod(String coarea, String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(coarea, Types.CHAR);
        parametri[1] = new ParamSql(cod, Types.CHAR);
        
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.fnc_cost_center_get_by_cod(?,?);", Optional.of(parametri)).stream()
                .map(CostCenter::new)
                .findFirst();
    }
    
    public static LazyDataModelRecords<CostCenter> getLazyRecords(String coarea, int after, int size,
            Optional<Map<String, String>> sort, Optional<Map<String, String>> filter, String userId) throws Exception{
        
        //pregatire baza sql
        String sqlBase = String.format(App.getSql("cost_center_lazy_base"),
                (sort.isPresent() ? getSortSql(sort.get()) : ""), (filter.isPresent() ? getFilterSql(filter.get()) : "")
        );
        ParamSql[] paramBase = new ParamSql[]{new ParamSql(coarea, Types.CHAR)};
        
        //obtine inregistrari
        String sqlRecs = String.format(App.getSql("recs_lazy_get_list"), size, sqlBase);
        ParamSql[] paramRecs = Stream.concat(Arrays.stream(paramBase), Arrays.stream(new ParamSql[]{new ParamSql(after, Types.INTEGER)}))
                .toArray(ParamSql[]::new);
        CompletableFuture<List<CostCenter>> fRecords = CompletableFuture.supplyAsync(() -> {
            List<CostCenter> result = new ArrayList<>();
            try {
                result = App.getConn(userId).getFromPreparedStatement(sqlRecs, Optional.of(paramRecs)).stream()
                        .map(CostCenter::new)
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
        
        LazyDataModelRecords<CostCenter> rezultat = new LazyDataModelRecords<>();
        rezultat.setRecords(fRecords.get(30, TimeUnit.SECONDS));
        rezultat.setPozitii(fPozitii.get(30, TimeUnit.SECONDS));
        return rezultat;
    }
    
    public static List<CostCenter> getLazyList(String coarea, int after, int size, Optional<Map<String, String>> filter, String userId) throws Exception{
        
        //pregatire baza sql
        String sqlBase = String.format(App.getSql("cost_center_lazy_base"), "", (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] paramBase = new ParamSql[]{new ParamSql(coarea, Types.CHAR)};
        
        //obtine inregistrari
        String sqlRecs = String.format(App.getSql("recs_lazy_get_list"), size, sqlBase);
        ParamSql[] paramRecs = Stream.concat(Arrays.stream(paramBase), Arrays.stream(new ParamSql[]{new ParamSql(after, Types.INTEGER)}))
                .toArray(ParamSql[]::new);
        return App.getConn(userId).getFromPreparedStatement(sqlRecs, Optional.of(paramRecs)).stream()
                        .map(CostCenter::new)
                        .collect(Collectors.toList());
    }
    
    public static Optional<CostCenter> insert(CostCenter inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("coarea", new ParamSql(inreg.getCoarea(), Types.CHAR));
        parametri.put("cocode", new ParamSql(inreg.getCocode(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("profit_center", new ParamSql(inreg.getPrctr(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_cost_center_insert_return(?,?,?,?,?,?)}", parametri).stream()
                .map(CostCenter::new)
                .findFirst();
    }
    
    public static Optional<CostCenter> update(CostCenter inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("coarea", new ParamSql(inreg.getCoarea(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("profit_center", new ParamSql(inreg.getPrctr(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_cost_center_update_return(?,?,?,?,?)}", parametri).stream()
                .map(CostCenter::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_cost_center_delete_return(?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void mergeLoad(UUID load_uuid, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("load_id", new ParamSql(load_uuid.toString(), Types.NVARCHAR));
        
        App.getConn(userId)
                .executeCallableStatement("{call oxal1.prc_cost_center_merge_load(?)}", parametri);
    }
    
    public static void toXlsx(String coarea, Optional<Map<String, String>> filter, String userId, OutputStream out) throws Exception{
        String sql = String.format(App.getSql("cost_center_lazy_get_all_by_filter"), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(coarea, Types.CHAR);
        
        App.getConn(userId).downloadFromPreparedStmtToXLSX(sql, Optional.of(parametri), out);
    }
}
