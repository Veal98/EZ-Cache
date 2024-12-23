package cn.itmtx.ezcache.starter.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final ThreadLocal<SimpleDateFormat> FORMATTER = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

    };

    public static String formatDate (long time) {
        if (time < 100000) {
            return "";
        }
        Date date = new Date(time);
        return FORMATTER.get().format(date);
    }
}
