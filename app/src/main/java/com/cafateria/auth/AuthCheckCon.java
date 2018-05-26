package com.cafateria.auth;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class AuthCheckCon extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "AuthCheckCon";
    AuthNetworking callback;
    Context context;

    public AuthCheckCon(AuthNetworking callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        callback.accessInternet(aBoolean);
        super.onPostExecute(aBoolean);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        //startCounter();
        return isOnline();
       /* try {
          *//*  InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            Log.e(TAG,"ipAddr:"+ipAddr);
            return !ipAddr.equals("");*//*
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }*/
    }

    public static boolean isOnline() {
        try {
            int _timeoutMs = 5000;
            Socket socket = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
            socket.connect(sockaddr, _timeoutMs);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
