package ro.any.c12153.opexal.services;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.json.JsonArray;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.bkg.AppSingleton;
import static ro.any.c12153.opexal.bkg.AppSingleton.FLAG_PROCESS;
import static ro.any.c12153.opexal.bkg.AppSingleton.NUMERIC_PATTERN;
import ro.any.c12153.shared.App;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffLoader;

/**
 *
 * @author catalin
 */
public class ProcessServ {
    
    private static int _classify(String coarea, Optional<JsonArray> uuids, Optional<Perioada> perioada,String userId) throws Exception{
        int rezultat = 0;
        final String ARFF_PATH  = AppSingleton.getArffFilePath(coarea);
        
        try(Connection conn1 = App.getConn(userId).getConnection();
            Connection conn2 = App.getConn(userId).getConnection();
            CallableStatement dataStmt = conn1.prepareCall("{call oxal1.prc_clasificator_get_classify_data(?,?,?,?,?)}");){
            FLAG_PROCESS.put(coarea, userId);
            
            //pregatire data statement
            //***************************************
            dataStmt.setObject("optUuidArray", uuids.isPresent()? uuids.get().toString() : null, Types.NVARCHAR);
            dataStmt.setObject("optAn", perioada.isPresent() ? perioada.get().getAn() : null, Types.SMALLINT);
            dataStmt.setObject("optLuna", perioada.isPresent() ? perioada.get().getLuna() : null, Types.TINYINT);
            dataStmt.setObject("coarea", coarea, Types.CHAR);
            dataStmt.setObject("kid", userId, Types.VARCHAR);
            
            //pregatire obiecte pentru clasificare
            //****************************************
            File arffFile = new File(ARFF_PATH);
            if (!arffFile.exists()) throw new Exception("NO_ARFF_FILE: " + coarea);
            ArffLoader loader = new ArffLoader();
            loader.setSource(arffFile);
            Instances dataStructure = loader.getStructure();
            if (dataStructure.classIndex() == -1) dataStructure.setClassIndex(dataStructure.numAttributes() - 1);
            List<String> attNames = new ArrayList<>();
            for (Enumeration<Attribute> e = dataStructure.enumerateAttributes(); e.hasMoreElements();)
                attNames.add(e.nextElement().name());
            
            Classifier classifier = ClasificatorServ.getLastModel(coarea, userId);        
            
            try(ResultSet regs = dataStmt.executeQuery();
                CallableStatement updStmt = conn2.prepareCall("{call oxal1.prc_clasificator_update_clasificare(?,?,?,?)}");){
                conn2.setAutoCommit(false);                
                ResultSetMetaData sqlMeta = regs.getMetaData();
                
                int pozitii = 0;
                while (regs.next()){
                    //rulare clasificare
                    //******************************
                    double[] values = new double[dataStructure.numAttributes()];
                    Arrays.fill(values, Utils.missingValue());
                    for (int i = 1; i <= sqlMeta.getColumnCount(); i++){
                        int attIndex = attNames.indexOf(sqlMeta.getColumnName(i));
                        if (attIndex < 0) continue;
                        
                        switch (sqlMeta.getColumnType(i)){
                            case Types.VARCHAR:
                            case Types.NVARCHAR:
                            case Types.CHAR:
                            case Types.NCHAR:{
                                    String valoare = regs.getString(i);
                                    if (valoare == null || valoare.isEmpty()){
                                        
                                    } else if (NUMERIC_PATTERN.matcher(valoare).matches()){
                                        values[attIndex] = Double.valueOf(valoare);
                                    } else {
                                        values[attIndex] = valoare.hashCode();
                                    }
                                }
                                break;                            
                            case Types.INTEGER:
                            case Types.SMALLINT:
                            case Types.TINYINT:
                            case Types.BIT:
                            case Types.NUMERIC:
                            case Types.DECIMAL:
                            case Types.FLOAT:
                            case Types.DOUBLE:
                                if (regs.getObject(i) == null){

                                } else {
                                    values[attIndex] = regs.getDouble(i);
                                }
                                break;
                            case Types.DATE:
                            case Types.TIME:
                                if (regs.getObject(i) == null){

                                } else {
                                    values[attIndex] = regs.getDate(i).hashCode();
                                }
                                break;
                            default:
                                throw new Exception("WRONG_SQL_TYPE");
                        }                
                    }
                    Instance instance = new DenseInstance(1.0, values);
                    instance.setDataset(dataStructure);
                    
                    //rezultate clasificare
                    Double predictionValue = classifier.classifyInstance(instance);
                    String prediction = instance.classAttribute().value(predictionValue.intValue());
                    double[] distribution = classifier.distributionForInstance(instance);
                    double acuratete = distribution[predictionValue.intValue()];
                    
                    //pregatire update statement
                    //************************************
                    updStmt.setObject("id", regs.getString("id"), Types.NVARCHAR);
                    updStmt.setObject("clasa", prediction, Types.INTEGER);
                    updStmt.setObject("acuratete", acuratete, Types.DECIMAL);
                    updStmt.setObject("kid", userId, Types.VARCHAR);
                    
                    updStmt.addBatch();
                    if (++pozitii % 1000 == 0) {
                        rezultat += updStmt.executeBatch().length;
                        System.out.println(pozitii);
                    }
                }
                rezultat += updStmt.executeBatch().length;
                conn2.commit();
            } catch (Exception ex) {
                if (!conn2.getAutoCommit()) conn2.rollback();
                throw ex;
            }     
        }
        return rezultat;
    }
    
    private static void _processCOFI(String coarea, Optional<JsonArray> uuids, Optional<Perioada> perioada, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("optUuidArray", new ParamSql(uuids.isPresent() ? uuids.get().toString() : null, Types.NVARCHAR));
        parametri.put("optAn", new ParamSql(perioada.isPresent() ? perioada.get().getAn() : null, Types.SMALLINT));
        parametri.put("optLuna", new ParamSql(perioada.isPresent() ? perioada.get().getLuna() : null, Types.TINYINT));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        App.getConn(userId).executeCallableStatement("{call oxal1.prc_process_co_order_settle_rule(?,?,?,?,?)}", parametri);
        App.getConn(userId).executeCallableStatement("{call oxal1.prc_process_reconcile_co_fi(?,?,?,?,?)}", parametri);
    }
    
    public static int classify(String coarea, Optional<JsonArray> uuids, Optional<Perioada> perioada,String userId) throws Exception{
        if (FLAG_PROCESS.containsKey(coarea))
            throw new Exception("CLASSIFICATION_STARTED_BY: ".concat(FLAG_PROCESS.get(coarea)));
        
        try {
            return _classify(coarea, uuids, perioada, userId);
        } finally {
            FLAG_PROCESS.remove(coarea);
        }
    }
    
    public static void processCOFI(String coarea, Optional<JsonArray> uuids, Optional<Perioada> perioada, String userId) throws Exception{
        if (FLAG_PROCESS.containsKey(coarea))
            throw new Exception("PROCESSING_STARTED_BY: ".concat(FLAG_PROCESS.get(coarea)));
        
        try {
            _processCOFI(coarea, uuids, perioada, userId);
        } finally {
            FLAG_PROCESS.remove(coarea);
        }
    }
    
    public static int processAllSteps(String coarea, Optional<JsonArray> uuids, Optional<Perioada> perioada,String userId) throws Exception{
        if (FLAG_PROCESS.containsKey(coarea))
            throw new Exception("PROCESSING_STARTED_BY: ".concat(FLAG_PROCESS.get(coarea)));
        int rezultat;
        
        try {
            _processCOFI(coarea, uuids, perioada, userId);
            rezultat = _classify(coarea, uuids, perioada, userId);
            if (uuids.isPresent()) FlagServ.delete(uuids.get(), userId);
        } finally {
            FLAG_PROCESS.remove(coarea);
        }
        return rezultat;
    }
}
