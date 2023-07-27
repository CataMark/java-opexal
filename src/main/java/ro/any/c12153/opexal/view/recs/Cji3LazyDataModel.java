package ro.any.c12153.opexal.view.recs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import ro.any.c12153.dbutils.JsfLazyDataModel.LazyDataModelRecords;
import ro.any.c12153.opexal.services.Cji3Serv;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class Cji3LazyDataModel extends LazyDataModel<Map<String, Object>> implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(Cji3LazyDataModel.class.getName());
    
    private final Short an;
    private final Short luna;
    private final String coarea;    
    private final String userId;
    private final Locale clocale;
    
    private Map<String, String> filter;
    private double suma;

    public Cji3LazyDataModel(Short an, Short luna, String coarea, String userId, Locale clocale) {
        this.an = an;
        this.luna = luna;
        this.coarea = coarea;
        this.userId = userId;
        this.clocale = clocale;
    }
    
    @Override
    public Object getRowKey(Map<String, Object> object) {
        return (String) object.get("id");
    }
    
    @Override
    public Map<String, Object> getRowData(String rowKey) {
        return this.getWrappedData().stream()
                .filter(x -> rowKey.equals(x.get("id")))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Map<String, Object>> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<Map<String, Object>> rezultat = new ArrayList<>();
        try {
            Map<String, String> sort = null;
            if (sortBy != null && !sortBy.isEmpty())
                sort = sortBy.values().stream()
                        .filter(x -> x.getSortOrder() != SortOrder.UNSORTED)
                        .collect(Collectors.toMap(
                                x -> x.getSortField().toLowerCase(),
                                x -> x.getSortOrder() == SortOrder.ASCENDING ? "asc" : "desc"
                        ));
            
            this.filter = null;
            if (filterBy != null && !filterBy.isEmpty())
                this.filter = filterBy.values().stream()
                        .filter(x -> Objects.nonNull(x.getFilterValue()))
                        .collect(Collectors.toMap(
                                x -> x.getFilterField().toLowerCase(),
                                x -> (String) x.getFilterValue()
                        ));
            
            LazyDataModelRecords<Map<String, Object>> inregs = Cji3Serv.getLazyRecords(this.an, this.luna, this.coarea, first, pageSize,
                    Optional.ofNullable(sort), Optional.ofNullable(this.filter), this.userId);
            rezultat = inregs.getRecords();
            this.setRowCount(inregs.getPozitii());
            this.suma = inregs.getSuma();
                
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, this.userId, ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.data.get", this.clocale), ex.getMessage()));
        }
        return rezultat;
    }
    
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Map<String, String> getFilter() {
        return filter;
    }

    public double getSuma() {
        return this.suma;
    }
}
