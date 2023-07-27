package ro.any.c12153.opexal.entities;

import java.io.Serializable;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author catalin
 */
public class SharedFileData implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private static final String STATUS_OK = "succes";
    private static final String ATTR_DELIM = "_";
    private static final String STAT_DELIM = ":";
    
    private String filename;
    private String cocode;
    private String sap_tranz;
    private Short an;
    private boolean ok;

    public SharedFileData(String fileLine) {
        if (Utils.stringNotEmpty(fileLine)){
            this.filename = fileLine.split(STAT_DELIM)[0];
            
            String[] attrs = fileLine.split(ATTR_DELIM);
            this.cocode = attrs[0];
            this.sap_tranz = attrs[1];
            this.an = Short.parseShort(attrs[2]);            
            this.ok = attrs[3].split(STAT_DELIM)[1].equals(STATUS_OK);  
        }
    }

    public String getFilename() {
        return filename;
    }

    public String getCocode() {
        return cocode;
    }

    public String getSap_tranz() {
        return sap_tranz;
    }

    public Short getAn() {
        return an;
    }

    public boolean isOk() {
        return ok;
    }
}
