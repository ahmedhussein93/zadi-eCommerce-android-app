package com.cafateria.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.helper.Global;
import com.google.gson.Gson;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created  on 23/12/2017.
 */
public class AuthHelper {
    private static final String PREF_NAME = "pref_name";
    private static final String USER_INFO = "user_info";
    private static final String LAST_PHONE_NUMBER = "last_p_n";
    public static final boolean IS_RELEASED = false;
    public static final String USERS_TABLE_NAME = "auth_users";
    static String IS_RECOVER_PASSWORD = "is_rec_pass";
    static AuthUsers TEMP_USER;
    private static ProgressDialog mProgressDialog;


    public static String getLastPhoneNumber(Context context) {
        return context.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("LAST_PHONE_NUMBER", null);
    }

    public static void startDialog(Context context) {
        hideKeyboard((Activity) context);
        mProgressDialog = new ProgressDialog(context, R.style.auth_dialog_style);
        mProgressDialog.setMessage("");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mProgressDialog.show();
    }

    public static void hideKeyboard(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = activity.getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(activity);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    public static void stopDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            System.out.println("Global.stop");
            mProgressDialog.dismiss();
        }
    }



   /* public static void startHome(Activity activity) {
        //Toast.makeText(activity, "start home - AuthHelper - startHome", Toast.LENGTH_LONG).show();
        activity.startActivity(new Intent(activity, Home.class));
        //Toast.makeText(activity, "start home", Toast.LENGTH_LONG).show();
    }*/

    public static void noCon(Context context) {
        Toast.makeText(context, context.getResources().getString(R.string.auth_no_internet_con), Toast.LENGTH_SHORT).show();
    }

    public static AuthUsers getLoggedUser(Context context) {
        String user_string = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(USER_INFO, null);
        return new Gson().fromJson(user_string, AuthUsers.class);
    }

    public static void saveLoggedUser(Context context, AuthUsers userLogged) {

        if (userLogged == null) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                    .remove(USER_INFO)
                    .remove(LAST_PHONE_NUMBER)
                    .apply();
        } else {
            AuthHelper.TEMP_USER = userLogged;
            String user_string = new Gson().toJson(userLogged, AuthUsers.class);
            System.out.println(user_string);
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(USER_INFO, user_string).apply();
        }

        String user_string = new Gson().toJson(userLogged, AuthUsers.class);
        Log.e("user_string", user_string);
    }

    public static void lockOrientation(Activity activity) {
        int currentOrientation = activity.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    public static void unLockOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }


    public static void logout(Context context) {
        Global.reset();
        saveLoggedUser(context, null);
        ((Activity) context).finish();
        context.startActivity(new Intent(context, AuthLog.class));
    }


    public static Bitmap bitmapFromImageView(ImageView iv) {
        iv.setDrawingCacheEnabled(true);
        return iv.getDrawingCache();
    }

    public static int getScreenWdith(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static boolean isHostAvailable(final String host, final int port, final int timeout) {
        try {
            final Socket socket = new Socket();
            final InetAddress inetAddress = InetAddress.getByName(host);
            final InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);

            socket.connect(inetSocketAddress, timeout);
            return true;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
