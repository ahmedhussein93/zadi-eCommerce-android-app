package com.cafateria.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.cafateria.R;

import java.io.File;
import java.util.List;
import java.util.Locale;


public class AppSettings {
    private static final String TAG = "APP_SETTINGS";

    public static void setLang(Context context, String lang) {
        lang = lang.equalsIgnoreCase("ar") ? "ar" : "en";
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.lang), lang).apply();
        Configuration newConfig = new Configuration(context.getResources().getConfiguration());
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        newConfig.locale = locale;
        newConfig.setLayoutDirection(locale);
        context.getResources().updateConfiguration(newConfig, null);
    }


    public static String getLang(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.lang), "ar");
    }

}
