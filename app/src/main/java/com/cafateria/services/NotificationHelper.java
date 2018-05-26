package com.cafateria.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.cafateria.R;
import com.cafateria.activities.ShowOrdrs;

/**
 * Created by ahmed on 3/21/2018.
 * *  *
 */

public class NotificationHelper {
    private static String TAG = "NotificationHelper";

    public static void showNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setAutoCancel(true)
                .setContentTitle("title")
                .setContentText("test text");

        Intent notificationIntent = new Intent(context, ShowOrdrs.class);
        // notificationIntent.putExtra("book_id", b.getKey());
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        //Intent intent = new Intent(context, MyBroadcastReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        //builder.setDeleteIntent(pendingIntent);
        //sound and vibration
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
            //builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        } else if (audio.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

        //sound(context);
    }
}
