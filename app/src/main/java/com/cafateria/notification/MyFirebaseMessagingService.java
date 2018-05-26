package com.cafateria.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.cafateria.R;
import com.cafateria.activities.ShowOrderDetails;
import com.cafateria.helper.AppSettings;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM_RECEIVE_MSG";

    public MyFirebaseMessagingService() {
        Log.i("FCMR", "MyFirebaseMessagingService started");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "MyFirebaseMessagingService started");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "MyFirebaseMessagingService started");
        super.onStart(intent, startId);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        AppSettings.setLang(this,AppSettings.getLang(this));
        Log.i(TAG, "onMessageReceived" + remoteMessage.getData());
        if (remoteMessage.getData().size() >= 3)
            showNotification(remoteMessage.getData().get("name"), Integer.parseInt(remoteMessage.getData().get("id")), remoteMessage.getData().get("time"));
        //super.onMessageReceived(remoteMessage);
    }

    private void showNotification(String name, int order_id, String timestamp) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(getResources().getString(R.string.new_order));
        mBuilder.setContentText(name + getResources().getString(R.string.have_req));

        Intent resultIntent = new Intent(this, ShowOrderDetails.class);
        resultIntent.putExtra("order_id", order_id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ShowOrderDetails.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notificationID allows you to update the notification later on.
        //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //mBuilder.setSound(alarmSound);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setVibrate(new long[]{1000, 1000});
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(order_id, mBuilder.build());
    }

    @Override
    public void onDeletedMessages() {
        Log.i(TAG, "onDeletedMessages");
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        Log.i(TAG, "onMessageSent:" + s);
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        Log.i(TAG, "onMessageSent:" + s);
        e.printStackTrace();
        super.onSendError(s, e);
    }

}
