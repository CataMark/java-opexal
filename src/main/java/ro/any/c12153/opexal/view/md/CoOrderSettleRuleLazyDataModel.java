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
import ro.any.c12153.opexal.entities.CoOrderSettleRule;
import ro.any.c12153.opexal.services.CoOrderSettleRuleServ;
import ro.any.c12153.shared.App;

/**
 *
 * @author catalin
 */
public class CoOrderSettleRuleLazyDataModel extends LazyDataModel<CoOrderSettleRule> implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CoOrderSettleRuleLazyDataModel.class.getName());
    
    private final String coarea;
    private final Short an;
    private final Short luna;
    private final String userId;
    private final Locale clocale;
    private Map<String, String> filter;

    public CoOrderSettleRuleLazyDataModel(String coarea, Short an, Short luna, String userId, Locale clocale) {
        this.coarea = coarea;
        this.an = an;
        this.luna = luna;
        this.userId = userId;
        this.clocale = clocale;
    }

    @Override
    public Object getRowKey(CoOrderSettleRule object) {
        return object.getId();
    }

    @Override
    public CoOrderSettleRule getRowData(String rowKey) {
        return this.getWrappedData().stream()
                .filter(x -> rowKey.equals(x.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CoOrderSettleRule> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<CoOrderSettleRule> rezultat = new ArrayList<>();
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
            
            LazyDataModelRecords<CoOrderSettleRule> inregs = CoOrderSettleRuleServ.getLazyRecords(this.an, this.luna, this.coarea, first, pageSize,
                    Optional.ofNullable(sort), Optional.ofNullable(filter), this.userId);
            rezultat = inregs.getRecords();
            this.setRowCount(inregs.getPozitii());
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, this.userId, ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.coordersetrule.listinit", this.clocale), ex.getMessage()));
        }
        return rezultat;
    }
    
    public Map<String, String> getFilter() {
        return filter;
    }   
}
