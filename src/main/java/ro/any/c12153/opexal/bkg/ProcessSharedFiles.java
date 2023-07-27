package ro.any.c12153.opexal.bkg;

import com.nimbusds.jose.util.StandardCharset;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.json.Json;
import ro.any.c12153.dbutils.helpers.CallbackMethod;
import ro.any.c12153.dbutils.helpers.FieldMetaData;
import ro.any.c12153.opexal.entities.CoCode;
import ro.any.c12153.opexal.entities.ColumnDictionary;
import ro.any.c12153.opexal.entities.Flag;
import ro.any.c12153.opexal.entities.SharedFileData;
import ro.any.c12153.opexal.services.Cji3Serv;
import ro.any.c12153.opexal.services.CoCodeServ;
import ro.any.c12153.opexal.services.ColumnDictionaryServ;
import ro.any.c12153.opexal.services.Fagll03Serv;
import ro.any.c12153.opexal.services.FlagServ;
import ro.any.c12153.opexal.services.Kob1Serv;
import ro.any.c12153.opexal.services.Ksb1Serv;
import ro.any.c12153.opexal.services.MailService;
import ro.any.c12153.opexal.services.ProcessServ;
import ro.any.c12153.shared.App;

/**
 *
 * @author catalin
 */
public class ProcessSharedFiles {    
    final private String homeDir;
    
    private final List<SharedFileData> files;
    private final List<CoCode> cocodes;
    private final String userId;
    private final File logFile;
    private final UUID uuid;
    private final Set<String> processCoareas; 

    public ProcessSharedFiles(File logFile, String userId) throws Exception {
        this.logFile = logFile;
        this.userId = userId;
        this.files = new ArrayList<>();
        this.cocodes = CoCodeServ.getAll(this.userId);
        
        this.homeDir = AppSingleton.getRpaHomeDir();
        this.uuid = UUID.randomUUID();
        this.processCoareas = new HashSet<>();
    }
    
    private void checkFiles() throws Exception{        
        try(InputStream istream = new FileInputStream(this.logFile);
            InputStreamReader ireader = new InputStreamReader(istream, StandardCharset.UTF_8);
            BufferedReader breader = new BufferedReader(ireader);){
            
            String line = breader.readLine();
            if (line == null) throw new Exception("EMPTY_LOG_FILE: " + this.logFile.getAbsolutePath());
            
            while (line != null){
                if (line.isEmpty()) throw new Exception("EMPTY_LINE_IN_LOG_FILE: " + this.logFile.getAbsolutePath());
                
                SharedFileData file_line = new SharedFileData(line);
                if (this.cocodes.stream().anyMatch(x -> x.getCod().equals(file_line.getCocode()))){
                    if (!file_line.isOk()) throw new Exception("FILE_NOT_SUCCESS: " + file_line.getFilename());

                    File lfile = new File(this.homeDir.concat("/").concat(file_line.getFilename()));
                    if (!lfile.exists()) throw new Exception("FILE_NOT_EXISTS: " + file_line.getFilename());

                    this.files.add(file_line);
                }
                line = breader.readLine();
            }
        }
    }
    
    private void loadFiles() throws Exception{
        this.files.sort((x,y) -> x.getSap_tranz().compareTo(y.getSap_tranz()));
        
        String tranz = null;
        String tabela = null;
        Map<String, String> fieldsMapping = null;

        //specific reserved fields
        FieldMetaData<String> field1 = new FieldMetaData<>();
        field1.setSqlName("load_uuid");
        field1.setDefaultValue(this.uuid.toString());
        List<FieldMetaData<?>> specificReservedField = Arrays.asList(field1);
        
        for (SharedFileData file : this.files){
            //collumn name mappings
            if (!file.getSap_tranz().equals(tranz)){
                tranz = file.getSap_tranz();
                fieldsMapping = ColumnDictionaryServ.getByTranzAndLang(tranz, "en", this.userId).stream()
                        .collect(Collectors.toMap(ColumnDictionary::getNume, ColumnDictionary::getCod));
                
                switch(tranz.toUpperCase()){
                    case "FAGLL03":
                        tabela = "oxal1.tbl_int_fagll03";
                        break;
                    case "KSB1":
                        tabela = "oxal1.tbl_int_ksb1";
                        break;
                    case "KOB1":
                        tabela = "oxal1.tbl_int_kob1";
                        break;
                    case "CJI3":
                        tabela = "oxal1.tbl_int_cji3";
                        break;
                    default:
                        throw new Exception("SAP_TRANZ_CODE_NOT_FOUND: " + file.getFilename());
                }
            }
            
            Date startTime = new Date();
            String coarea = this.cocodes.stream()
                    .filter(x -> x.getCod().equals(file.getCocode()))
                    .map(y -> y.getCoarea().getCod())
                    .findFirst()
                    .orElseThrow(() -> new Exception("NO_COAREA_CODE_FOUND: " + file.getFilename()));
            
            CallbackMethod onComplete = () -> {
                try {
                    switch(file.getSap_tranz().toUpperCase()){
                        case "FAGLL03":
                            Fagll03Serv.collectMasterData(this.uuid, coarea, Optional.of(startTime), this.userId);
                            break;
                        case "KSB1":
                            Ksb1Serv.collectMasterData(this.uuid, coarea, Optional.of(startTime), this.userId);
                            break;
                        case "KOB1":
                            Kob1Serv.collectMasterData(this.uuid, coarea, Optional.of(startTime), this.userId);
                            break;
                        case "CJI3":
                            Cji3Serv.collectMasterData(this.uuid, coarea, Optional.of(startTime), this.userId);
                            break;
                        default:
                            throw new Exception("SAP_TRANZ_CODE_NOT_FOUND: " + file.getFilename());
                    };
                    FlagServ.insert(new Flag(this.uuid.toString(), Flag.Tip.UPLOAD, coarea, file.getSap_tranz()), this.userId)
                            .orElseThrow(() -> new Exception("FLAG_INSERT_ERROR: " + file.getFilename()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex.getMessage(), ex.getCause());
                }
            };
            
            App.getConn(this.userId)
                    .loadExcel(tabela, this.homeDir.concat("/").concat(file.getFilename()), Optional.of("Sheet1"), Optional.empty(), Optional.of(fieldsMapping),
                            Optional.of(specificReservedField), Optional.empty(), Optional.of(onComplete));
            
            this.processCoareas.add(coarea);
            
            //TODO: insert schedule for file deletion in 30 days
        }
    }
    
    private void processData() throws Exception{
        if (this.processCoareas.isEmpty()) throw new Exception("NO_COAREAS_FOR_PROCESS: " + this.logFile.getAbsolutePath());
        
        List<String> uuids = Arrays.asList(this.uuid.toString());
        for (String coarea : this.processCoareas){
            ProcessServ.processAllSteps(coarea, Optional.of(Json.createArrayBuilder(uuids).build()), Optional.empty(), this.userId);
        }
    }
    
    public void run() throws Exception{
        this.checkFiles();
        this.loadFiles();
        this.processData();
        MailService.sendHtmlInfo(Optional.empty(), "RPA_LOAD: " + this.logFile.getName(), "LOADED FILES: " + this.files.size(), Optional.of("en"), this.userId);
    }
}
