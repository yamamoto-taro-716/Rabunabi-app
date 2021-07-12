package com.rabunabi.friends.utils;

import android.content.Context;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import com.rabunabi.friends.R;
import com.rabunabi.friends.model.friends.FriendListModel;

public class TimeUtil {
    public static String convertTimeToLocalTime(String timesServer, boolean isChat) {
        String format = "yyyy-MM-dd HH:mm:ss";
        //SimpleDateFormat dtf = new SimpleDateFormat(format);
        Date date = null;
        try {
            SimpleDateFormat sdfgmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfgmt.setTimeZone(TimeZone.getTimeZone("GMT"));

            SimpleDateFormat sdfmad = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfmad.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));

            Date inptdate = null;
            try {
                inptdate = sdfgmt.parse(timesServer);
            } catch (ParseException e) {
             //   e.printStackTrace();
            }

            String timeOK = sdfmad.format(inptdate);
            //System.out.println("GMT:\t\t" + sdfgmt.format(inptdate));
            //System.out.println("Europe/Madrid:\t" + timeOK);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
            try {
                date = dateFormatter.parse(timeOK);
            } catch (Exception e) {
            }
            return timeDisplay(date, isChat);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return timesServer;
        }
        //return timeDisplay(date);
    }

    public static String timeDisplay(Date date, boolean isChat) {
        try {
            String format = "MM/dd HH:mm";
           /* if (isChat) {
                format = "HH:mm";
            }*/
            SimpleDateFormat timeFormatter = new SimpleDateFormat(format);
            String displayValue = timeFormatter.format(date);
            return displayValue;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * convert time iso to time update
     *
     * @param dateIso format ISO "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'". Exp : 2019-07-23T07:00:13.908Z
     * @return exp : 30 phut truoc
     */
    public static String convertISOToTime(Context context, String dateIso) {
        String result;
        if (TextUtils.isEmpty(dateIso)) {
            result = "";
        } else {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat dfFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dfFrom.setTimeZone(timeZone);
            try {
                Date date = dfFrom.parse(dateIso);
                result = getLastUpdate(context, date, true);
            } catch (ParseException e) {
                result = "";
            }
        }
        return result;
    }

    public static String getLastUpdate(Context context, Date lastUpdate, boolean showDetail) {
        if (lastUpdate == null) {
            return "";
        }
        Date now = new Date();
        long timeDiff = now.getTime() - lastUpdate.getTime();
        long deltaSecond = timeDiff / 1000;
        if (deltaSecond < 120) {
            return "1 " + context.getResources().getString(R.string.minutes_ago);
        }
        long deltaMinute = timeDiff / (60 * 1000);
        if (deltaMinute < 60) {
            return deltaMinute + " " + context.getResources().getString(R.string.minutes_ago);
        }
        if (deltaMinute < 120) {
            return "1 " + context.getResources().getString(R.string.hours_ago);
        }
        long deltaHour = timeDiff / (60 * 60 * 1000);
        if (deltaHour < 24) {
            return deltaHour + " " + context.getResources().getString(R.string.hours_ago);
        }
        long deltaDay = timeDiff / (24 * 60 * 60 * 1000);
        if (deltaDay < 2) {
            return "1 " + context.getResources().getString(R.string.days_ago);
        }

        if (deltaDay < 100) {
            return deltaDay + " " + context.getResources().getString(R.string.days_ago);
        } else {
            SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE");
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
            /*if (showDetail) {
                return sdfDay.format(lastUpdate) + ", " + sdfDate.format(lastUpdate);
            } else {
                return sdfDate.format(lastUpdate);
            }*/
            return sdfDate.format(lastUpdate);
        }
    }

    ///////////////////////////////////
    public static void sortByDate(Context context, ArrayList<FriendListModel> listUser) {
        Collections.sort(listUser, new Comparator<FriendListModel>() {
            @Override
            public int compare(FriendListModel o1, FriendListModel o2) {
                Date a = convertStringToDate(context, o1.getLogin_time());
                Date b = convertStringToDate(context, o2.getLogin_time());
                if (a == null || b == null)
                    return 0;
                return b.compareTo(a);
            }
        });
       /* Collections.sort(listUser, new Comparator<FriendListModel> {
            public int compare (FriendListModel o1, FriendListModel o2){
                DateTime a = o1.getDateTime();
                DateTime b = o2.getDateTime();
                if (a.lt(b))
                    return -1;
                else if (a.lteq(b)) // it's equals
                    return 0;
                else
                    return 1;
            }
        });*/
    }

    public static Date convertStringToDate(Context context, String dateIso) {
        try {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat dfFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dfFrom.setTimeZone(timeZone);
            return dfFrom.parse(dateIso);
        } catch (ParseException e) {
            return null;
        }
    }
}
