package org.example.sharedlibrary.constaint;

import java.util.Date;

public class DateConstant {

    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private DateConstant() {
        super();
    }

    public static long convertDateToLong(Date date) {
        return date.getTime();
    }

    public static boolean isDate1BeforeDate2(Date date1, Date date2) {
        return convertDateToLong(date1) < convertDateToLong(date2);
    }

    public static boolean isDate1AfterDate2(Date date1, Date date2) {
        return convertDateToLong(date1) > convertDateToLong(date2);
    }

    public static boolean isDate1EqualsDate2(Date date1, Date date2) {
        return convertDateToLong(date1) == convertDateToLong(date2);
    }

}
