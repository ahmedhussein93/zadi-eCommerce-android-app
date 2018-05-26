package com.cafateria.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.cafateria.R;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthLog;
import com.cafateria.auth.AuthUsers;
import com.cafateria.notification.NotificationHelper;
import com.cafateria.rate.RateUs;


public class LauncherApp {
    private static final String TAG = "LauncherApp";

    public static void start(Context context) {
        Intent intent;
        AuthUsers user = AuthHelper.getLoggedUser(context);
        if (user == null)
            intent = new Intent(context, AuthLog.class);
        else {
            intent = new Intent(context, Home.class);

            //start token generator service
            NotificationHelper.startServices(context);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
