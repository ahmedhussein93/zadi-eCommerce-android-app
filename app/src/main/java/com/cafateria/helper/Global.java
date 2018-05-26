package com.cafateria.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.cafateria.R;
import com.cafateria.model.Cats;
import com.cafateria.model.Food;
import com.cafateria.model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created  on 19/02/2018.
 * *  *
 */
public class Global {
    public static final List<Cats> CATS = new ArrayList<>();
    public static Activity CURRENT_ACTIVITY = null;
    public static Class<? extends Activity> CLASS_TO_BACK;
    public static int LAST_ID = 0;
    public static Order LAST_ORDER = null;
    public static int LAST_FOOD_ID = 0;
    public static int LAST_DRINK_ID = 0;
    public static int ITEMS_COUNT = 4;

    public static Food FOOD_TO_SHOW_DETAILS = null;
    public static int RANDOM = 0;
    public static final List<Food> CART = new ArrayList<>();

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static Map<Integer, Integer> LAST_IDS = new HashMap<>();


    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        System.out.println("current_time:" + now + " order_time:" + time);
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String millisToLongDHMS(long duration) {
        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= SECOND_MILLIS) {
            temp = duration / DAY_MILLIS;
            if (temp > 0) {
                duration -= temp * DAY_MILLIS;
                res.append(temp).append(" day").append(temp > 1 ? "s" : "")
                        .append(duration >= MINUTE_MILLIS ? ", " : "");
            }

            temp = duration / HOUR_MILLIS;
            if (temp > 0) {
                duration -= temp * HOUR_MILLIS;
                res.append(temp).append(" hour").append(temp > 1 ? "s" : "")
                        .append(duration >= MINUTE_MILLIS ? ", " : "");
            }

            temp = duration / MINUTE_MILLIS;
            if (temp > 0) {
                duration -= temp * MINUTE_MILLIS;
                res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
            }

            if (!res.toString().equals("") && duration >= SECOND_MILLIS) {
                res.append(" and ");
            }

            temp = duration / SECOND_MILLIS;
            if (temp > 0) {
                res.append(temp).append(" second").append(temp > 1 ? "s" : "");
            }
            return res.toString();
        } else {
            return "0 second";
        }
    }

    public static void reset() {
        LAST_FOOD_ID = 0;
        LAST_DRINK_ID = 0;
        CART.clear();
        FOOD_TO_SHOW_DETAILS = null;
        //LAST_ORDER = null;
    }

    public static String getCatName(Context context, int cat_id) {
        for (Cats cat : Global.CATS) {
            if (cat.getId() == cat_id)
                return cat.getName(context);
        }
        return context.getResources().getString(R.string.home);
    }

   /* public static boolean isServiceRunning(Context context, Class adminServiceClass) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(adminServiceClass.getName())) {
                return true;
            }
        }
        return false;
    }*/

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.e("Global", "isServiceRunning:true");
                return true;
            }
        }
        Log.e("Global", "isServiceRunning:false");
        return false;
    }

    public static boolean isAppSown(Context context) {

        return false;
    }
}
