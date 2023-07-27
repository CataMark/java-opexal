package ro.any.c12153.opexal.helpers;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexal.bkg.AppSingleton;

/**
 *
 * @author catalin
 */
@Named(value = "planappurl")
@RequestScoped
public class PlanAppUrl{
    
    private @Inject ExternalContext context;

    public String getValoare() {        
        return this.context.getInitParameter(
                AppSingleton.CHILD_APP ? "ro.any.c12153.CHILD_OPEX_PLAN_APP_URL" : "ro.any.c12153.PARENT_OPEX_PLAN_APP_URL"
        );
    }
}
