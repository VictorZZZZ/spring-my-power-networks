package net.energo.grodno.pes.sms.utils.dates;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Component
public class ZvvDates {
    public static final String FIRST_DAY = "first_day";
    public static final String LAST_DAY = "last_day";

    /**
     * Название месяца с разницей от текущего
     * @param diff Разница от текущего. Может быть как положительным, так и отрицательным
     * @return String ex.: "август 2020"
     */
    public static String monthNameFromNow(int diff){
        Calendar calendar = Calendar.getInstance(); // this takes current date
        calendar.add(Calendar.MONTH, diff);
        return new SimpleDateFormat("LLLL yyyy").format(calendar.getTime());
    }

    /**
     * День месяца с разницей diff от текущего (первая секунда дня)
     * @param diff может быть как положительным, так и отрицательным
     * @param day FIRST_DAY или LAST_DAY
     * @return TimeStamp
     */
    public static Timestamp dayOfMonthFromNow(int diff, String day){
        Calendar calendar = Calendar.getInstance(); // this takes current date
        calendar.add(Calendar.MONTH, diff);
        if(day.equals(FIRST_DAY)) {
            getBeginningOfCalendarMonth(calendar);
        }
        if(day.equals(LAST_DAY)) {
            getEndingOfCalendarMonth(calendar);
        }

        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * Первый день этого месяца
     * @return TimeStamp
     */
    public static Timestamp firstDayofThisMonth(){
        Calendar calendar = Calendar.getInstance();   // this takes current date
        getBeginningOfCalendarMonth(calendar);
        long firstDateOfThisMonth = calendar.getTimeInMillis();

        return new Timestamp(firstDateOfThisMonth);
    }

    /**
     * Последний день этого месяца
     * @return TimeStamp
     */
    public static Timestamp lastDayofThisMonth(){
        Calendar calendar = Calendar.getInstance();   // this takes current date
        getEndingOfCalendarMonth(calendar);
        long lastDateOfThisMonth = calendar.getTimeInMillis();

        return new Timestamp(lastDateOfThisMonth);

    }

    public static Timestamp firstDayOfPreviousMonth(){
        Calendar calendar = Calendar.getInstance();   // this takes current date
        calendar.add(Calendar.MONTH, -1);
        getBeginningOfCalendarMonth(calendar);
        long firstDateOfPreviousMonth = calendar.getTimeInMillis();

        return new Timestamp(firstDateOfPreviousMonth);
    }

    public static Timestamp lastDayOfPreviousMonth(){
        Calendar calendar = Calendar.getInstance();   // this takes current date
        calendar.add(Calendar.MONTH, -1);
        getEndingOfCalendarMonth(calendar);
        long lastDateOfPreviousMonth = calendar.getTimeInMillis();

        return new Timestamp(lastDateOfPreviousMonth);
    }

    private static void getBeginningOfCalendarMonth(Calendar calendar) {
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE)); // changed calendar to cal
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND,calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND,calendar.getActualMinimum(Calendar.MILLISECOND));
    }

    private static void getEndingOfCalendarMonth(Calendar calendar) {
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE)); // changed calendar to cal
        calendar.set(Calendar.HOUR,calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND,calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND,calendar.getActualMaximum(Calendar.MILLISECOND));
    }
}
