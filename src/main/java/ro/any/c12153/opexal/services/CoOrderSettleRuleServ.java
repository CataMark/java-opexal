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
import ro.any.c12153.opexal.entities.CoOrderSettleRule;
import ro.any.c12153.shared.App;

/**
 *
 * @author catalin
 */
public class CoOrderSettleRuleServ {
    
    public static LazyDataModelRecords<CoOrderSettleRule> getLazyRecords(Short an, Short luna, String coarea, int after, int size,
            Optional<Map<String, String>> sort, Optional<Map<String, String>> filter, String userId) throws Exception{
        
        //pregatire baza sql
        String sqlBase = String.format(App.getSql("co_order_settle_rule_lazy_base"),
                (sort.isPresent() ? getSortSql(sort.get()) : ""), (filter.isPresent() ? getFilterSql(filter.get()) : "")
        );
        ParamSql[] paramBase = new ParamSql[]{new ParamSql(coarea, Types.CHAR), new ParamSql(an, Types.SMALLINT), new ParamSql(luna, Types.TINYINT)};
        
        //obtine inregistrari
        String sqlRecs = String.format(App.getSql("recs_lazy_get_list"), size, sqlBase);
        ParamSql[] paramRecs = Stream.concat(Arrays.stream(paramBase), Arrays.stream(new ParamSql[]{new ParamSql(after, Types.INTEGER)}))
                .toArray(ParamSql[]::new);
        CompletableFuture<List<CoOrderSettleRule>> fRecords = CompletableFuture.supplyAsync(() -> {
            List<CoOrderSettleRule> result = new ArrayList<>();
            try {
                result = App.getConn(userId).getFromPreparedStatement(sqlRecs, Optional.of(paramRecs)).stream()
                        .map(CoOrderSettleRule::new)
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
        
        LazyDataModelRecords<CoOrderSettleRule> rezultat = new LazyDataModelRecords<>();
        rezultat.setRecords(fRecords.get(30, TimeUnit.SECONDS));
        rezultat.setPozitii(fPozitii.get(30, TimeUnit.SECONDS));
        return rezultat; 
    }
    
    public static String deleteByFilter(Short an, Short luna, String coarea, Optional<Map<String, String>> filter, String userId) throws Exception{
        String sql = String.format(App.getSql("co_order_settle_rule_lazy_delete_by_filter"), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] parametri = new ParamSql[4];
        parametri[0] = new ParamSql(userId, Types.VARCHAR);
        parametri[1] = new ParamSql(coarea, Types.CHAR);
        parametri[2] = new ParamSql(an, Types.SMALLINT);
        parametri[3] = new ParamSql(luna, Types.TINYINT);
        
        return App.getConn(userId).executePreparedStatement(sql, Optional.of(parametri));
    }
    
    public static String deleteById(List<String> ids, String userId) throws Exception{
        String sql = App.getSql("co_order_settle_rule_lazy_delete_by_id");
        List<Optional<ParamSql[]>> parametri = ids.stream()
                .map(x -> Optional.of(new ParamSql[]{new ParamSql(userId, Types.VARCHAR), new ParamSql(x, Types.VARCHAR)}))
                .collect(Collectors.toList());
        
        return App.getConn(userId).executePreparedStatement(sql, parametri);
    }
    
    public static void mergeLoad(UUID load_uuid, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("load_id", new ParamSql(load_uuid.toString(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        App.getConn(userId)
                .executeCallableStatement("{call oxal1.prc_co_order_set_rule_merge_load(?,?)}", parametri);
    }
    
    public static void toXlsx(Short an, Short luna, String coarea, Optional<Map<String, String>> filter, String userId, OutputStream out) throws Exception{
        String sql = String.format(App.getSql("co_order_settle_rule_lazy_get_all_by_filter"), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] parametri = new ParamSql[3];
        parametri[0] = new ParamSql(coarea, Types.CHAR);
        parametri[1] = new ParamSql(an, Types.SMALLINT);
        parametri[2] = new ParamSql(luna, Types.TINYINT);
        
        App.getConn(userId).downloadFromPreparedStmtToXLSX(sql, Optional.of(parametri), out);
    }
}
