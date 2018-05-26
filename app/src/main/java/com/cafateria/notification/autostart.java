package com.cafateria.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ahmed on 3/19/2018.
 * *  *
 */

public class autostart extends BroadcastReceiver {
    public void onReceive(Context context, Intent arg1) {
        NotificationHelper.startServices(context);
        Log.i("Autostart", "autostart started");
    }
}