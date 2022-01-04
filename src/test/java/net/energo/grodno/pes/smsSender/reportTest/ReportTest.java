package net.energo.grodno.pes.smsSender.reportTest;

import net.energo.grodno.pes.smsSender.AbstractClass;
import net.energo.grodno.pes.sms.repositories.OrderItemRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;

public class ReportTest extends AbstractClass {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @Ignore
    public void testReport() throws ParseException {
        Calendar calendar = Calendar.getInstance();   // this takes current date
        calendar.add(Calendar.MONTH, -2);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE)); // changed calendar to cal
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND,calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND,calendar.getActualMinimum(Calendar.MILLISECOND));
        long firstDateOfPreviousMonth = calendar.getTimeInMillis();

        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE)); // changed calendar to cal
        calendar.set(Calendar.HOUR,calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND,calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND,calendar.getActualMaximum(Calendar.MILLISECOND));
        long lastDateOfPreviousMonth = calendar.getTimeInMillis();


        Timestamp date_from = new Timestamp(firstDateOfPreviousMonth);
        Timestamp date_to = new Timestamp(lastDateOfPreviousMonth);
        System.out.println(date_from.toString());
        System.out.println(date_to.toString());
        Integer resId = 2;
        System.out.println(
                orderItemRepository.getSmsCountForPeriodAndResId( date_from, date_to,resId)
        );
    }

}
