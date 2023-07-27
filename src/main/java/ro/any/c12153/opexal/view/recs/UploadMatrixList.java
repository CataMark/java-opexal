package ro.any.c12153.opexal.view.recs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.entities.CoCode;
import ro.any.c12153.opexal.entities.UploadMatrix;
import ro.any.c12153.opexal.services.CoCodeServ;
import ro.any.c12153.opexal.services.UploadMatrixServ;
import ro.any.c12153.opexal.entities.SapTransaction;
import ro.any.c12153.opexal.helpers.UploadMatrixGroup;
import ro.any.c12153.opexal.services.SapTransactionServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.events.DbDeleted;
import ro.any.c12153.shared.events.DbInserted;
import ro.any.c12153.shared.events.DbUpdated;

/**
 *
 * @author C12153
 */
@Named(value = "upmtrxlist")
@ViewScoped
public class UploadMatrixList implements Serializable, SelectTableView<UploadMatrix>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UploadMatrixList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<UploadMatrix> item;    
    private List<CoCode> cocodes;
    private List<SapTransaction> transactions;
    private List<UploadMatrix> list;
    private List<UploadMatrixGroup> groups;
    private UploadMatrix selected;
    
    private void prepareGroups(){
        this.groups = new ArrayList<>();
        if (this.cocodes != null && !this.cocodes.isEmpty() && this.list != null && !this.list.isEmpty()){
            this.cocodes.forEach(x -> 
                this.groups.add(
                    new UploadMatrixGroup(x,
                            this.list.stream()
                                    .filter(y -> y.getCocode().equals(x.getCod()))
                                    .collect(Collectors.toMap(UploadMatrix::getTranz, y -> y))
                    )
                )
            );                
        }
    }
    
    private void observeDbInsert(@Observes(notifyObserver = Reception.IF_EXISTS) @DbInserted UploadMatrix item){
        this.list.add(item);
        this.selected = item;
        this.prepareGroups();
    }
    
    private void observeDbUpdate(@Observes(notifyObserver = Reception.IF_EXISTS) @DbUpdated UploadMatrix item){
        for (int i = 0; i < this.list.size(); i++){
            if (this.list.get(i).getId().equals(item.getId())){
                this.list.set(i, item);
                break;
            }
        }
        this.selected = item;
        this.prepareGroups();
    }
    
    private void observesDbDelete(@Observes(notifyObserver = Reception.IF_EXISTS) @DbDeleted UploadMatrix item){
        this.list.removeIf(x -> x.getId().equals(item.getId()));
        this.selected = null;
        this.prepareGroups();
    }
    
    public void datainit(){
        try {            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.cocodes = CoCodeServ.getAll(cuser.getUname());
                            this.cocodes.sort((x, y) -> x.getCod().compareTo(y.getCod()));
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.transactions = SapTransactionServ.getAll(cuser.getUname());
                            this.transactions.sort((x, y) -> x.getCod().compareTo(y.getCod()));
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.list = UploadMatrixServ.getAll(cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    })
            ).thenRun(() -> this.prepareGroups())
            .get(30, TimeUnit.SECONDS);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.upmtrx.listinit", clocale), ex.getMessage()));
        }
    }

    @Override
    public void newItem(){
        this.item.setSelected(new UploadMatrix());
        this.item.initLists();
    }

    @Override
    public void passSelected(boolean initLists) {
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new UploadMatrix(this.selected.getJson()));
                if (initLists) this.item.initLists();
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }

    @Override
    public void clear() {
        this.dialog.clear();
        this.item.clear();
    }

    @Override
    public String getInitError() {
        return null;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<CoCode> getCocodes() {
        return cocodes;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<SapTransaction> getTransactions() {
        return transactions;
    }

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<UploadMatrix> getList() {
        return list;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<UploadMatrixGroup> getGroups() {
        return groups;
    }    
    
    public UploadMatrix getItemByTranz(String tranz, List<UploadMatrix> list){
        UploadMatrix rezultat = new UploadMatrix();
        if (list != null && !list.isEmpty() && Utils.stringNotEmpty(tranz))        
            rezultat = list.stream()
                    .filter(x -> x.getTranz().equals(tranz))
                    .findFirst()
                    .orElseGet(UploadMatrix::new);
        return rezultat;
    }

    public UploadMatrix getSelected() {
        return selected;
    }

    @Override
    public void setSelected(UploadMatrix selected) {
        this.selected = selected;
    }
}
