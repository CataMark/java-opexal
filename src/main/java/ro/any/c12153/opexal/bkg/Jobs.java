package ro.any.c12153.opexal.bkg;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import ro.any.c12153.opexal.entities.Clasificator;
import ro.any.c12153.opexal.entities.CoArea;
import ro.any.c12153.opexal.entities.Perioada;
import ro.any.c12153.opexal.services.ClasificatorServ;
import ro.any.c12153.opexal.services.CoAreaServ;
import ro.any.c12153.opexal.services.PerioadaServ;

/**
 *
 * @author catalin
 */
public class Jobs {
    
    /**
     * Deletes files from RPA folder older than 90 days
     * 
     * @throws Exception 
     */
    public static void deleteOldRPAfiles() throws Exception{
        final long daysDelay = 90 * 24 * 60 * 60 * 1000; //90 de zile
        final File[] files = new File(AppSingleton.getRpaHomeDir()).listFiles();
        for (File file : files){
            long diff = new Date().getTime() - file.lastModified();
            if (diff > daysDelay && file.exists()) file.delete();
        }
    }
    
    public static void openCurrentPeriod() throws Exception{        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Short year = ((Integer) calendar.get(Calendar.YEAR)).shortValue();
        Short month = ((Integer) calendar.get(Calendar.MONTH)).shortValue();
        if (!PerioadaServ.getByAn(year, AppSingleton.SYS_USER).stream()
                .anyMatch(x -> x.getAn().equals(year) && x.getLuna().equals(month))){
            Perioada perioada = new Perioada();
            perioada.setAn(year);
            perioada.setLuna(month);
            perioada.setInchis(Boolean.FALSE);
            PerioadaServ.insert(perioada, AppSingleton.SYS_USER)
                    .orElseThrow(() -> new Exception("FAILED_TO_INSERT_PERIOD: " + year + "." + month));
        } 
    }
    
    public static void createTrainingModel() throws Exception{            
        Perioada perioada = PerioadaServ.getLastClosed(AppSingleton.SYS_USER).orElse(null);
        if (perioada == null) return;
        
        List<CoArea> coareas = CoAreaServ.getAll(AppSingleton.SYS_USER);
        for (CoArea coarea : coareas){
            boolean start = false;
            
            Clasificator clasificator = ClasificatorServ.getLastModelLog(coarea.getCod(), AppSingleton.SYS_USER).orElse(null);
            if (clasificator == null){
                start = true;
            } else {
                Calendar perCalendar = Calendar.getInstance();
                perCalendar.set(Calendar.YEAR, perioada.getAn());
                perCalendar.set(Calendar.MONTH, perioada.getLuna() > 12 ? Calendar.DECEMBER : perioada.getLuna() - 1);
                perCalendar.set(Calendar.DAY_OF_MONTH, perioada.getLuna() - 11);
                
                Calendar clsCalendar = Calendar.getInstance();
                clsCalendar.set(Calendar.YEAR, clasificator.getAn());
                clsCalendar.set(Calendar.MONTH, clasificator.getLuna() > 12 ? Calendar.DECEMBER : perioada.getLuna() - 1);
                clsCalendar.set(Calendar.DAY_OF_MONTH, clasificator.getLuna() - 11);
                
                start = perCalendar.after(clsCalendar);
            }
            
            if (start) ClasificatorServ.createModel(coarea.getCod(), perioada, AppSingleton.SYS_USER);
        }
    }
    
    public static void checkOpenedPeriods() throws Exception{
        final long daysDelay = 63 * 24 * 60 * 60 * 1000; //63 de zile        
        List<Perioada> perioade = PerioadaServ.getOpened(AppSingleton.SYS_USER);
        for (Perioada perioada: perioade){
            Calendar perCalendar = Calendar.getInstance();
            if (perioada.getLuna() > 12){
                perCalendar.setTime(perioada.getMod_timp());
            } else {            
                perCalendar.set(Calendar.YEAR, perioada.getAn());
                perCalendar.set(Calendar.MONTH, perioada.getLuna() - 1);
                perCalendar.set(Calendar.DAY_OF_MONTH, 1);
            }
            long diff = new Date().getTime() - perCalendar.getTimeInMillis();
            if (diff > daysDelay) throw new Exception("Periods not closed for more than 60 days, starting with: " + perioada.getAn() + "." + perioada.getLuna());
        }
    }
}
