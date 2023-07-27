package ro.any.c12153.opexal.helpers;

import java.io.Serializable;
import java.util.Map;
import ro.any.c12153.opexal.entities.CoCode;
import ro.any.c12153.opexal.entities.UploadMatrix;

/**
 *
 * @author C12153
 */
public class UploadMatrixGroup implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private CoCode cocode;
    private Map<String, UploadMatrix> items;

    public UploadMatrixGroup() {
    }

    public UploadMatrixGroup(CoCode cocode, Map<String, UploadMatrix> items) {
        this.cocode = cocode;
        this.items = items;
    }
    
    public UploadMatrix getItemByTranz(String sapTranzName){
        return this.items.get(sapTranzName);
    }

    public CoCode getCocode() {
        return cocode;
    }

    public void setCocode(CoCode cocode) {
        this.cocode = cocode;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Map<String, UploadMatrix> getItems() {
        return items;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setItems(Map<String, UploadMatrix> items) {
        this.items = items;
    }
}
