package com.cafateria.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cafateria.auth.AuthHelper;
import com.cafateria.helper.Global;
import com.cafateria.rest.RestHelper;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationHelper {
    public static void startServices(Context context) {
        updateToken(context);

        if (!Global.isServiceRunning(context, MyFirebaseMessagingService.class)) {
            Log.e("NotificationHelper", "MyFirebaseMessagingService not running");
            context.startService(new Intent(context, MyFirebaseMessagingService.class));
        } else Log.e("NotificationHelper", "MyFirebaseMessagingService  running");


        if (!Global.isServiceRunning(context, RegistrationIntentService.class)) {
            Log.e("NotificationHelper", "RegistrationIntentService not running");
            context.startService(new Intent(context, RegistrationIntentService.class));
        } else Log.e("NotificationHelper", "RegistrationIntentService running");
    }

    public static void updateToken(Context context) {
        if (AuthHelper.getLoggedUser(context) == null)
            return;
        final String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("NotificationHelper", "token to send: " + token);
        Call<String> call = RestHelper.API_SERVICE.updateTokenFCM(AuthHelper.getLoggedUser(context).getId(), token);//
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("NotificationHelper", "updateTokenFCM->res->" + response.body());
                if (response.isSuccessful()) {
                    Log.e("NotificationHelper", "token sent: " + token);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
