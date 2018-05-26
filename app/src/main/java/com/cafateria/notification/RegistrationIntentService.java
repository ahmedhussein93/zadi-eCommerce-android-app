package com.cafateria.notification;

import android.util.Log;

import com.cafateria.auth.AuthHelper;
import com.cafateria.rest.RestHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ahmed on 3/19/2018.
 * *  *
 */

public class RegistrationIntentService extends FirebaseInstanceIdService {

    private static final String TAG = "FCM_SEND_TOKEN";

    @Override
    public void onCreate() {
        Log.i(TAG, "RegistrationIntentService started");
        super.onCreate();
    }

    public RegistrationIntentService() {
        Log.i(TAG, "RegistrationIntentService started");
    }

    @Override
    public void onTokenRefresh() {
        NotificationHelper.updateToken(this);
    }
}