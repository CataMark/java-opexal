package ro.any.c12153.opexal.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.helpers.ParamSql;
import ro.any.c12153.opexal.entities.Clasificator;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.bkg.AppSingleton;
import static ro.any.c12153.opexal.bkg.AppSingleton.DATE_FORMAT;
import static ro.any.c12153.opexal.bkg.AppSingleton.FLAG_MODEL;
import static ro.any.c12153.opexal.bkg.AppSingleton.NUMERIC_PATTERN;
import ro.any.c12153.shared.App;
import weka.classifiers.Classifier;
import weka.classifiers.meta.Bagging;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;

/**
 *
 * @author C12153
 */
public class ClasificatorServ {
    
    private static Optional<Clasificator> insertModelLog(String coarea, Perioada startPerioada, String filePath, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("an", new ParamSql(startPerioada.getAn(), Types.SMALLINT));
        parametri.put("luna", new ParamSql(startPerioada.getLuna(), Types.TINYINT));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("file_path", new ParamSql(filePath, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_clasificator_insert_return(?,?,?,?,?)}", parametri).stream()
                .map(Clasificator::new)
                .findFirst();
    }
    
    private static boolean deleteModelLog(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.NVARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_clasificator_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static Optional<Clasificator> getLastModelLog(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_clasificator_get_last(?)}", parametri).stream()
                .map(Clasificator::new)
                .findFirst();
    }
    
    public static List<Clasificator> getAllModelLog(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_clasificator_get_all(?)}", parametri).stream()
                .map(Clasificator::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<Clasificator> getModelLogById(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.NVARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_clasificator_get_by_id(?)}", parametri).stream()
                .map(Clasificator::new)
                .findFirst();
    }
    
    @SuppressWarnings("ValueOfIncrementOrDecrementUsed")
    private static String scrieTrainingArff(String coarea, Perioada startPerioada, String userId) throws Exception{
        System.out.println("START ARFF FILE: " + ro.any.c12153.shared.Utils.castDateToString(new Date()));
        
        final String FILE_PATH  = AppSingleton.getArffFilePath(coarea);
        String dataSql = "{call oxal1.prc_clasificator_get_training_data(?,?,?)}";
        String claseSql = "select cod from oxal1.vw_opex_categ;";
        File tempFile = File.createTempFile(coarea.concat("_training"), ".arff");
        
        try(Connection conn = App.getConn(userId).getConnection();
            CallableStatement dataStmt = conn.prepareCall(dataSql);
            PreparedStatement claseStmt = conn.prepareStatement(claseSql);
            FileOutputStream fStream = new FileOutputStream(tempFile, false);
            PrintWriter writer = new PrintWriter(fStream, false);) {
            
            dataStmt.setObject("startAn", startPerioada.getAn(), Types.SMALLINT);
            dataStmt.setObject("startLuna", startPerioada.getLuna(), Types.TINYINT);
            dataStmt.setObject("coarea", coarea, Types.CHAR);
            
            //scriere header
            //********************************
            writer.println("@relation wekamodel");
            writer.println();
            writer.flush();
            
            //adugare linii pentru fiecare atribut
            ResultSetMetaData metaData = dataStmt.getMetaData();
            for (int i = 1; i < metaData.getColumnCount(); i++){
                String[] linie = null;
                switch (metaData.getColumnType(i)){
                    case Types.VARCHAR:
                    case Types.NVARCHAR:
                    case Types.CHAR:
                    case Types.NCHAR:
                    case Types.INTEGER:
                    case Types.SMALLINT:
                    case Types.TINYINT:
                    case Types.BIT:
                    case Types.NUMERIC:
                    case Types.DECIMAL:
                    case Types.FLOAT:
                        linie = new String[]{"@attribute", metaData.getColumnName(i), "numeric"};
                        break;
                    case Types.DATE:
                    case Types.TIME:
                        linie = new String[]{"@attribute", metaData.getColumnName(i), "date", DATE_FORMAT};
                        break;
                    default:
                        throw new Exception("WRONG_SQL_TYPE");
                }
                if (linie != null && linie.length > 0) writer.println(String.join(" ", linie));
            }
            writer.flush();
            
            //adaugare linie pentru clase
            writer.print("@attribute".concat(" ").concat(metaData.getColumnName(metaData.getColumnCount())).concat(" {"));
            try(ResultSet clase = claseStmt.executeQuery();){
                int i = 0;
                while (clase.next()){
                    writer.print((i == 0 ? "" : ",").concat(clase.getString(1)));
                    i++;
                }
            }
            writer.print("}");
            writer.println();
            writer.flush();
            
            //scriere valori
            //********************************            
            writer.println("@data");
            try(ResultSet data = dataStmt.executeQuery();){
                int p = 0;
                while(data.next()){
                    if (p > 0) writer.println();
                    
                    for (int i = 1; i <= metaData.getColumnCount(); i++){
                        if (i > 1) writer.print(",");
                        switch (metaData.getColumnType(i)){
                            case Types.VARCHAR:
                            case Types.NVARCHAR:
                            case Types.CHAR:
                            case Types.NCHAR:{
                                    String valoare = data.getString(i);
                                    if (valoare == null || valoare.isEmpty()){
                                        writer.print("?");
                                    } else if (NUMERIC_PATTERN.matcher(valoare).matches()){
                                        writer.print(valoare);
                                    } else {
                                        writer.print(valoare.hashCode());
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
                            case Types.DOUBLE: {
                                    Object valoare = data.getObject(i);
                                    writer.print(valoare == null ? "?" : String.valueOf(valoare));
                                }
                                break;
                            case Types.DATE:
                            case Types.TIME:{
                                    Date valoare = data.getDate(i);
                                    writer.print(valoare == null ? "?" : valoare.hashCode());
                                }
                                break;
                            default:
                                throw new Exception("WRONG_SQL_TYPE");
                        }
                    }
                    if (++p % 1000 == 0){
                        writer.flush();
                        System.out.println("ARFF pozitii: " + p);
                    }
                }
                writer.flush();
            }
        }
        Files.move(Paths.get(tempFile.getAbsolutePath()), Paths.get(FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("END ARFF FILE: " + ro.any.c12153.shared.Utils.castDateToString(new Date()));
        return FILE_PATH;
    }  
    
    public static Clasificator createModel(String coarea, Perioada startPerioada, String userId) throws Exception{
        System.out.println("START MODEL FILE: " + ro.any.c12153.shared.Utils.castDateToString(new Date()));
        
        if (FLAG_MODEL.containsKey(coarea))
            throw new Exception("TRAINING_STARTED_BY: ".concat(FLAG_MODEL.get(coarea)));
        final String FILE_PATH  = AppSingleton.getModelFilePath(coarea, startPerioada);
        String arffFilePath = scrieTrainingArff(coarea, startPerioada, userId);
        
        Clasificator rezultat = null;
        File tempFile = File.createTempFile(coarea, ".model");
        
        try(FileOutputStream fStream = new FileOutputStream(tempFile, false);){
            FLAG_MODEL.put(coarea, userId);
            File arffFile = new File(arffFilePath);
            
            ArffLoader loader = new ArffLoader();
            loader.setSource(arffFile);

            Instances data = loader.getDataSet();
            if (data.classIndex() == -1) data.setClassIndex(data.numAttributes() - 1);
            
            Classifier classifier = new Bagging();
            classifier.buildClassifier(data);
            
            /*System.out.println("Evaluation");
            Evaluation evaluation = new Evaluation(data);
            evaluation.crossValidateModel(classifier, data, 100, new Random(1));*/
            
            SerializationHelper.write(fStream, classifier);
            data.clear();
            
            rezultat = insertModelLog(coarea, startPerioada, FILE_PATH, userId)
                    .orElseThrow(() -> new Exception("NEW_MODEL_NOT_LOGGED: " + FILE_PATH));
        } finally {
            Files.move(Paths.get(tempFile.getAbsolutePath()), Paths.get(FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
            FLAG_MODEL.remove(coarea);
        }
        System.out.println("END MODEL FILE: " + ro.any.c12153.shared.Utils.castDateToString(new Date()));
        return rezultat;
    }
    
    public static Classifier getLastModel(String coarea, String userId) throws Exception{
        Clasificator modelLog = getLastModelLog(coarea, userId).orElseThrow(() -> new Exception("NO_MODEL_LOG_FOUND: " + coarea));
        
        try(FileInputStream fStream = new FileInputStream(new File(modelLog.getFile_path()));){
            return (Classifier) SerializationHelper.read(fStream);
        }
    }
    
    public static boolean deleteModel(String id, String userId) throws Exception{
        Clasificator modelLog = getModelLogById(id, userId).orElseThrow(() -> new Exception("NO_MODEL_LOG_FOUND: " + id));
        File model = new File(modelLog.getFile_path());
        model.delete();
        return deleteModelLog(id, userId);
    }
}
