package ro.any.c12153.shared.converter;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@FacesConverter(value = "stringParamUrlEncodeConverter")
public class StringParamUrlEncodeConverter implements Converter<String>{
    private static final Logger LOG = Logger.getLogger(StringParamUrlEncodeConverter.class.getName());
    private @Inject @CurrentUser User cuser;

    @Override
    public String getAsObject(FacesContext context, UIComponent component, String value) {
        String rezultat = null;
        try {
            if(Utils.stringNotEmpty(value)) rezultat = Utils.paramDecode(value);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, String value) {
        String rezultat = null;
        try {
            if (value != null) rezultat = Utils.paramEncode(value);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
}
