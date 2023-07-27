package ro.any.c12153.opexal.services;

import java.io.OutputStream;
import java.math.BigDecimal;
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
import ro.any.c12153.shared.App;

/**
 *
 * @author catalin
 */
public class ProcessResultServ {
    
    public static LazyDataModelRecords<Map<String, Object>> getLazyRecords(Short an, Short luna, String coarea, String ledger, int after, int size,
            Optional<Map<String, String>> sort, Optional<Map<String, String>> filter, String userId) throws Exception{
        
        //pregatire baza sql
        String sqlBase = String.format(App.getSql("process_result_lazy_base"),
                (sort.isPresent() ? getSortSql(sort.get()) : ""), (filter.isPresent() ? getFilterSql(filter.get()) : "")
        );
        ParamSql[] paramBase = new ParamSql[4];
        paramBase[0] = new ParamSql(an, Types.SMALLINT);
        paramBase[1] = new ParamSql(luna, Types.TINYINT);
        paramBase[2] = new ParamSql(coarea, Types.CHAR);
        paramBase[3] = new ParamSql(ledger, Types.CHAR);
        
        //obtine inregistrari
        String sqlRecs = String.format(App.getSql("recs_lazy_get_list"), size, sqlBase);
        ParamSql[] paramRecs = Stream.concat(Arrays.stream(paramBase), Arrays.stream(new ParamSql[]{new ParamSql(after, Types.INTEGER)}))
                .toArray(ParamSql[]::new);
        CompletableFuture<List<Map<String, Object>>> fRecords = CompletableFuture.supplyAsync(() -> {
            List<Map<String, Object>> result = new ArrayList<>();
            try {
                result = App.getConn(userId).getFromPreparedStatement(sqlRecs, Optional.of(paramRecs));
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
        
        //obtine suma
        String sqlSum = String.format(App.getSql("recs_lazy_get_sum"), "wrgbtr", sqlBase);
        CompletableFuture<BigDecimal> fSuma = CompletableFuture.supplyAsync(() -> {
            BigDecimal result = BigDecimal.valueOf(0);
            try {
                result = (BigDecimal) App.getConn(userId)
                        .getFromPreparedStatement(sqlSum, Optional.of(paramBase)).stream()
                        .flatMap(x -> x.values().stream())
                        .findFirst()
                        .orElse(0);
            } catch (Exception ex) {
                throw new CompletionException(ex.getMessage(), ex.getCause());
            }
            return result;
        });
        
        LazyDataModelRecords<Map<String, Object>> rezultat = new LazyDataModelRecords<>();
        rezultat.setRecords(fRecords.get(30, TimeUnit.SECONDS));
        rezultat.setPozitii(fPozitii.get(30, TimeUnit.SECONDS));
        rezultat.setSuma(fSuma.get(30, TimeUnit.SECONDS).doubleValue());
        return rezultat;
    }
    
    public static boolean delete(Short an, Short luna, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("an", new ParamSql(an, Types.SMALLINT));
        parametri.put("luna", new ParamSql(luna, Types.TINYINT));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_process_result_delete_return(?,?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static String updateByFilter(Short an, Short luna, String coarea, String ledger, Optional<Map<String, String>> filter,
            ProcessResultDocUpdate updateValues, String userId) throws Exception{
        
        String sqlUpdate = updateValues.sqlUpdate();
        List<ParamSql> paramsUpdateValues = updateValues.sqlParametri();
        ParamSql[] parametri;
        
        if (paramsUpdateValues.isEmpty()){
            parametri = new ParamSql[5];
            parametri[0] = new ParamSql(userId, Types.VARCHAR);
            parametri[1] = new ParamSql(an, Types.SMALLINT);
            parametri[2] = new ParamSql(luna, Types.TINYINT);
            parametri[3] = new ParamSql(coarea, Types.CHAR);
            parametri[4] = new ParamSql(ledger, Types.CHAR);
        } else {
            parametri = new ParamSql[paramsUpdateValues.size() + 5];

            parametri[0] = new ParamSql(userId, Types.VARCHAR);
            for (int i = 0; i < paramsUpdateValues.size(); i++){
                parametri[i + 1] = paramsUpdateValues.get(i);
            }
            parametri[paramsUpdateValues.size() + 1] = new ParamSql(an, Types.SMALLINT);
            parametri[paramsUpdateValues.size() + 2] = new ParamSql(luna, Types.TINYINT);
            parametri[paramsUpdateValues.size() + 3] = new ParamSql(coarea, Types.CHAR);
            parametri[paramsUpdateValues.size() + 4] = new ParamSql(ledger, Types.CHAR);
        } 
        String sql = String.format(App.getSql("process_result_lazy_update_by_filter"), sqlUpdate, (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        return App.getConn(userId).executePreparedStatement(sql, Optional.of(parametri));
    }
    
    public static String updateById(List<String> ids, ProcessResultDocUpdate updateValues, String userId) throws Exception{
        String sqlUpdate = updateValues.sqlUpdate();
        List<ParamSql> paramsUpdateValues = updateValues.sqlParametri();
        List<Optional<ParamSql[]>> parametri;
        
        if (paramsUpdateValues.isEmpty()){
            parametri = ids.stream()
                    .map(x -> Optional.of(new ParamSql[]{new ParamSql(userId, Types.VARCHAR), new ParamSql(x, Types.VARCHAR)}))
                    .collect(Collectors.toList());
        } else {
            parametri = ids.stream()
                .map(x -> {
                    ParamSql[] params = new ParamSql[paramsUpdateValues.size() + 2];

                    params[0] = new ParamSql(userId, Types.VARCHAR);
                    for (int i = 0; i < paramsUpdateValues.size(); i++){
                        params[i + 1] = paramsUpdateValues.get(i);
                    }
                    params[paramsUpdateValues.size() + 1] = new ParamSql(x, Types.VARCHAR);
                    return Optional.of(params);
                })
                .collect(Collectors.toList());
        }
        String sql = String.format(App.getSql("process_result_lazy_update_by_id"), sqlUpdate);
        return App.getConn(userId).executePreparedStatement(sql, parametri);
    }
    
    public static boolean recordsExist(Short an, Short luna, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("an", new ParamSql(an, Types.SMALLINT));
        parametri.put("luna", new ParamSql(luna, Types.TINYINT));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_process_result_values_exist(?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void mergeLoad(UUID load_uuid, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("load_id", new ParamSql(load_uuid.toString(), Types.NVARCHAR));
        
        App.getConn(userId)
                .executeCallableStatement("{oxal1.prc_process_result_merge_load(?)}", parametri);
    }
    
    public static void toXlsx(Short an, Short luna, String coarea, String ledger, Optional<Map<String, String>> filter, String userId, OutputStream out) throws Exception{
        String sql = String.format(App.getSql("process_result_lazy_get_all_by_filter"), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] parametri = new ParamSql[4];
        parametri[0] = new ParamSql(an, Types.SMALLINT);
        parametri[1] = new ParamSql(luna, Types.TINYINT);
        parametri[2] = new ParamSql(coarea, Types.CHAR);
        parametri[3] = new ParamSql(ledger, Types.CHAR);
        
        App.getConn(userId).downloadFromPreparedStmtToXLSX(sql, Optional.of(parametri), out);
    }
}

