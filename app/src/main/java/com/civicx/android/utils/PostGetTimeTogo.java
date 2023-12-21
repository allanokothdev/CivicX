package com.civicx.android.utils;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class PostGetTimeTogo extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String postGetTimeAgo(long deadline) {
        if (deadline < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            deadline *= 1000;
        }

        long now = System.currentTimeMillis();
        if (deadline < now || deadline <= 0) {
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.setTimeInMillis(deadline);
            return DateFormat.format("EEEE, dd MMMM yyyy", cal).toString();
        }

        final long diff = deadline - now;
        if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hrs to go";
        } else {
            return diff / DAY_MILLIS + " days to go";
        }

    }
}
