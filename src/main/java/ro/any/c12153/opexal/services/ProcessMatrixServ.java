package ro.any.c12153.opexal.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.opexal.entities.ProcessMatrix;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class ProcessMatrixServ {
    
    public static List<ProcessMatrix> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxal1.prc_process_matrix_get_all}", Optional.empty()).stream()
                .map(ProcessMatrix::new)
                .collect(Collectors.toList());
    }
}
