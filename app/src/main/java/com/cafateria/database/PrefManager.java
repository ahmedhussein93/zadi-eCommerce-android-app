package com.cafateria.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cafateria.R;
import com.cafateria.auth.AuthHelper;
import com.cafateria.model.Food;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 13/11/2017.
 */

public class PrefManager {
    private static final String TAG = "prefManger";
    private static Gson gson = new Gson();

    public static void setCurrentCatId(Context context, int id) {
        context.getSharedPreferences("CATS", Context.MODE_PRIVATE).edit().putInt("current_cat_id", id).apply();
    }

    public static int getCurrentCatId(Context context) {
        return context.getSharedPreferences("CATS", Context.MODE_PRIVATE).getInt("current_cat_id", -1);
    }

    public static boolean isFirstLaunch(Context context) {
        return context.getSharedPreferences("SET", Context.MODE_PRIVATE).getBoolean("first_lanuch", true);
    }

    public static void firstLaunch(Context context) {
        context.getSharedPreferences("SET", Context.MODE_PRIVATE).edit().putBoolean("first_lanuch", false).apply();
    }

    public static void setLastLog(Context context, String phone_or_mail) {
        context.getSharedPreferences("SET", Context.MODE_PRIVATE).edit().putString("last_log", phone_or_mail).apply();
    }

    public static String getLastLog(Context context) {
        return context.getSharedPreferences("SET", Context.MODE_PRIVATE).getString("last_log", "");
    }

    public static void addToFav(Context context, Food o) {
        SharedPreferences mPrefs = context.getSharedPreferences("fav__", Context.MODE_PRIVATE);
        List<Food> favs = getFavList(context);
        favs.add(o);
        Log.e(TAG, "id to add fav:" + o.id);
        mPrefs.edit().putString(AuthHelper.getLoggedUser(context).getId() + "_fav_objects", gson.toJson(favs)).apply();
    }

    public static void RemoveFromFav(Context context, Food o) {
        SharedPreferences mPrefs = context.getSharedPreferences("fav__", Context.MODE_PRIVATE);
        List<Food> favs = getFavList(context);
        Food ff = null;
        for (Food f : favs)
            if (f.id == o.id)
                ff = f;
        if (ff != null)
            favs.remove(ff);
        Log.e(TAG, "id to remove fav:" + o.id);
        mPrefs.edit().putString(AuthHelper.getLoggedUser(context).getId() + "_fav_objects", gson.toJson(favs)).apply();
    }

    public static List<Food> getFavList(Context context) {
        List<Food> list = new ArrayList<>();
        SharedPreferences mPrefs = context.getSharedPreferences("fav__", Context.MODE_PRIVATE);
        List<Food> s = gson.fromJson(mPrefs.getString(AuthHelper.getLoggedUser(context).getId() + "_fav_objects", null), new TypeToken<List<Food>>() {
        }.getType());
        if (s != null)
            list.addAll(s);
        for (Food o : list) {
            Log.e(TAG, "ids in fav:" + o.id);
        }
        return list;
    }

    public static boolean isInFav(Context context, Food fff) {
        List<Food> list = new ArrayList<>();
        SharedPreferences mPrefs = context.getSharedPreferences("fav__", Context.MODE_PRIVATE);
        List<Food> s = gson.fromJson(mPrefs.getString(AuthHelper.getLoggedUser(context).getId() + "_fav_objects", null), new TypeToken<List<Food>>() {
        }.getType());
        if (s != null)
            list.addAll(s);
        boolean state = false;
        for (Food f : list)
            if (f.id == fff.id)
                state = true;
        return state;
    }

    public static void setAlertDontShowToday(Context context) {
        //int today = GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH);
        context.getSharedPreferences("admin_alert", Context.MODE_PRIVATE).edit().putLong("current_ts", System.currentTimeMillis()).apply();
    }

    public static boolean isAlertLessQuantityEnabled(Context context) {
        long today = System.currentTimeMillis();
        Long day = context.getSharedPreferences("admin_alert", Context.MODE_PRIVATE).getLong("current_ts", 0L);
        return ((today - day) > (1000 * 60 * 60 * 24));
    }

    public static int getLessQuantity(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.alert_less_amount_settings_key), "10"));
    }

    public static int getLessQuantityInterval(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.alert_every_key), "10"));
    }

}
