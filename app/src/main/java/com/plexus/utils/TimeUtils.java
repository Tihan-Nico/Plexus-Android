package com.plexus.utils;

import android.content.Context;

import com.plexus.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/

public class TimeUtils {

    public static boolean isDateToday(long milliSeconds) {
        Calendar toCheck = Calendar.getInstance();
        toCheck.setTimeInMillis(milliSeconds);

        Date toCheckDate = toCheck.getTime();

        toCheck.setTimeInMillis(System.currentTimeMillis());
        toCheck.set(Calendar.HOUR_OF_DAY, 0);
        toCheck.set(Calendar.MINUTE, 0);
        toCheck.set(Calendar.SECOND, 0);

        Date startDate = toCheck.getTime();

        return toCheckDate.compareTo(startDate) > 0;
    }

    public static boolean isYesterday(long milliSeconds) {

        Calendar toCheck = Calendar.getInstance();
        toCheck.setTimeInMillis(milliSeconds); // your date

        Calendar yesterday = Calendar.getInstance(); // today
        yesterday.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        boolean isYesterday = yesterday.get(Calendar.YEAR) == toCheck.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == toCheck.get(Calendar.DAY_OF_YEAR);

        return isYesterday;
    }

    public static boolean isDateInCurrentWeek(long milliSeconds) {
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(milliSeconds);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);

        return week == targetWeek && year == targetYear;
    }

    // https://www.ibm.com/support/knowledgecenter/en/SSMKHH_9.0.0/com.ibm.etools.mft.doc/ak05616_.htm
    public static String getFormattedTimestamp(Context context, long milliSeconds) {
        if (isDateToday(milliSeconds)) {
            return formatTimestamp(milliSeconds, "'Last seen at' HH:mm");
        } else if (isYesterday(milliSeconds)) {
            return context.getString(R.string.yesterday);
        } else if (isDateInCurrentWeek(milliSeconds)) {
            return formatTimestamp(milliSeconds, "'Last seen' EEEE");
        } else {
            return formatTimestamp(milliSeconds, "'Last seen' dd/MM/yy 'at' HH:mm");
        }
    }

    public static String formatTimestamp(Long timestamp, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(timestamp);
    }

    public static String getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }

    public static String msToString(long ms) {
        long totalSecs = ms / 1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        long secs = totalSecs % 60;
        String minsString = (mins == 0)
                ? "00"
                : ((mins < 10)
                ? "0" + mins
                : "" + mins);
        String secsString = (secs == 0)
                ? "00"
                : ((secs < 10)
                ? "0" + secs
                : "" + secs);
        if (hours > 0)
            return hours + ":" + minsString + ":" + secsString;
        else if (mins > 0)
            return mins + ":" + secsString;
        else return ":" + secsString;
    }
}
