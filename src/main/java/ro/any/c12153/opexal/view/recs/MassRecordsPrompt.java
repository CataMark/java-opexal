package ro.any.c12153.opexal.view.recs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.entities.CoArea;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.entities.SapTransaction;
import ro.any.c12153.opexal.services.CoAreaServ;
import ro.any.c12153.opexal.services.PerioadaServ;
import ro.any.c12153.opexal.services.SapTransactionServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "mrecsprompt")
@ViewScoped
public class MassRecordsPrompt implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MassRecordsPrompt.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private List<Short> ani;
    private List<Perioada> perioade;
    private List<CoArea> arii;
    private List<SapTransaction> tranzactii;
    private String selAn;
    private String selPeriod;
    private String selCoarea;
    private String selTranzactie;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            
            Optional<String> an = Optional.ofNullable(params.get("an"));
            if (an.isPresent()) this.selAn = Utils.paramDecode(an.get());
            
            Optional<String> pd = Optional.ofNullable(params.get("pd"));
            if (pd.isPresent()) this.selPeriod = Utils.paramDecode(pd.get());
            
            Optional<String> co = Optional.ofNullable(params.get("co"));
            if (co.isPresent()) this.selCoarea = Utils.paramDecode(co.get());
            
            Optional<String> tr = Optional.ofNullable(params.get("tr"));
            if (tr.isPresent()) this.selTranzactie = Utils.paramDecode(tr.get());
            
            CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
                        try {
                            this.ani = PerioadaServ.getAni(cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            if (Utils.stringNotEmpty(this.selAn))
                                this.perioade = PerioadaServ.getByAn(Short.parseShort(this.selAn), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            //TODO: implement get CoAreas by upload matrix
                            this.arii = CoAreaServ.getAll(cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            //TODO: implement get transactions by upload matrix
                            this.tranzactii = SapTransactionServ.getAll(cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex.getMessage(), ex.getCause());
                        }
                    })
            ).get(60, TimeUnit.SECONDS);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            if (this.selAn != null) rezultat += "&an=" + Utils.paramEncode(this.selAn);
            if (this.selPeriod != null) rezultat += "&pd=" + Utils.paramEncode(this.selPeriod);
            if (this.selCoarea != null) rezultat += "&co=" + Utils.paramEncode(this.selCoarea);
            if (this.selTranzactie != null) rezultat += "&tr=" + Utils.paramEncode(this.selTranzactie);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void onAnChange(){
        try {
            if (Utils.stringNotEmpty(this.selAn)){
                this.perioade = PerioadaServ.getByAn(Short.parseShort(this.selAn), cuser.getUname());
            } else {
                this.perioade = new ArrayList<>();
            }
            this.selPeriod = null;
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.perioada.get", clocale), ex.getMessage()));
        }
    }

    public String getInitError() {
        return initError;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<Short> getAni() {
        return ani;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<Perioada> getPerioade() {
        return perioade;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<CoArea> getArii() {
        return arii;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public List<SapTransaction> getTranzactii() {
        return tranzactii;
    }

    public String getSelAn() {
        return selAn;
    }

    public void setSelAn(String selAn) {
        this.selAn = selAn;
    }

    public String getSelPeriod() {
        return selPeriod;
    }

    public void setSelPeriod(String selPeriod) {
        this.selPeriod = selPeriod;
    }

    public String getSelCoarea() {
        return selCoarea;
    }

    public void setSelCoarea(String selCoarea) {
        this.selCoarea = selCoarea;
    }

    public String getSelTranzactie() {
        return selTranzactie;
    }

    public void setSelTranzactie(String selTranzactie) {
        this.selTranzactie = (selTranzactie == null ? selTranzactie : selTranzactie.toLowerCase());
    }
}
