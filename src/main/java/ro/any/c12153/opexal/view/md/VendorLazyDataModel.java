package ro.any.c12153.opexal.view.md;

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
import ro.any.c12153.opexal.entities.Vendor;
import ro.any.c12153.opexal.services.VendorServ;
import ro.any.c12153.shared.App;

/**
 *
 * @author catalin
 */
public class VendorLazyDataModel extends LazyDataModel<Vendor> implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(VendorLazyDataModel.class.getName());
    
    private final String userId;
    private final Locale clocale;
    private Map<String, String> filter;

    public VendorLazyDataModel(String userId, Locale clocale) {
        this.userId = userId;
        this.clocale = clocale;
    }
    
    @Override
    public Object getRowKey(Vendor object) {
        return object.getCod();
    }
    
    @Override
    public Vendor getRowData(String rowKey) {
        return this.getWrappedData().stream()
                .filter(x -> rowKey.equals(x.getCod()))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Vendor> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<Vendor> rezultat = new ArrayList<>();
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
            
            LazyDataModelRecords<Vendor> inregs = VendorServ.getLazyRecords(first, pageSize, Optional.ofNullable(sort), Optional.ofNullable(filter), this.userId);
            rezultat = inregs.getRecords();
            this.setRowCount(inregs.getPozitii());
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, this.userId, ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.vendor.listinit", this.clocale), ex.getMessage()));
        }
        return rezultat;
    }
    
    public Map<String, String> getFilter() {
        return filter;
    }
}
