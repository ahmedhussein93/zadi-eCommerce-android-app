package com.cafateria.mailer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.auth.Interfaces;
import com.cafateria.rest.RestHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Mailer {
    public static final String SMTP_MAIL = "ahmed.fcih93@gmail.com";
    public static final String SMTP_PASS = "Ahmed.1993";

    //compile fileTree(dir: 'libs', include: '*.jar'), 'commons-net:commons-net:3.3'
    public static void send(final Context c, String from, String password, String to, String subject, String text, final Interfaces.callback1 callback1) {
        // new Async(c, from, password, to, subject, text, callback1).execute();
        //send to websevice to use php mailer
        sendMailWS(c, from, password, to, subject, text, callback1);
    }

    private static void sendMailWS(final Context c, String from, String password, String to, String subject, String text, final Interfaces.callback1 callback1) {
        Call<String> call = RestHelper.API_SERVICE.sendMail(from, password, to, subject, text);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String res = response.body();
                Log.e("Mailer", "RES:" + res);
                if (response.isSuccessful()) {
                    callback1.onComplete(res.equalsIgnoreCase("true") || res.equalsIgnoreCase("1"));
                } else {
                    Toast.makeText(c, c.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }
}
