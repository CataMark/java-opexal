package ro.any.c12153.opexal.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.opexal.entities.IcPartener;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class IcPartenerServ {
    
    public static List<IcPartener> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxal1.vw_ic_partner;", Optional.empty()).stream()
                .map(IcPartener::new)
                .collect(Collectors.toList());
    }    
}
