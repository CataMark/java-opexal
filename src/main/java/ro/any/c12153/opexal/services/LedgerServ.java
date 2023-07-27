package ro.any.c12153.opexal.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.opexal.entities.Ledger;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class LedgerServ {
    
    public static List<Ledger> getGroups(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_acc_ledgers_get_all}", Optional.empty()).stream()
                .map(Ledger::new)
                .collect(Collectors.toList());
    }
    
    public static List<Ledger> getLedgers(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_acc_ledgers_get_list}", Optional.empty()).stream()
                .map(Ledger::new)
                .collect(Collectors.toList());
    }
}
