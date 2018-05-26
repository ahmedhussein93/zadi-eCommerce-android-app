package com.cafateria.auth;

import android.content.Context;
import android.os.AsyncTask;


public class AuthInternet {
    static AsyncTask<Void, Void, Boolean> tt;
    static AuthNetworking network;
    public static void action(Context context, AuthNetworking networking) {
        //network = networking;
        tt = new AuthCheckCon(networking, context).execute();
        //startCounter();
    }
}
