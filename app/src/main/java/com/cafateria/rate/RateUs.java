package com.cafateria.rate;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.auth.AuthHelper;
import com.cafateria.rest.RestHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//call this belw line in main activity on create
//RateUs.app_launched(this);
public class RateUs {
    private static final String TAG = "RATE_US";
    private static final boolean IS_TEST = false;
    // Insert your Application Title
    // private static String TITLE;

    // Day until the Rate Us Dialog Prompt(Default 2 Days)
    private final static int DAYS_UNTIL_PROMPT = 1;

    // App launches until Rate Us Dialog Prompt(Default 5 Launches)
    private final static int LAUNCHES_UNTIL_PROMPT = 2;

    //show prompet after some minutes of starting (Default 10 SECONDS)
    private final static int DELAY_AFTER_START = 5;

    public static void app_launched(final Context mContext) {
        Log.e(TAG, "RateUs --app_launched");
        uploadRateRes(mContext);
        //set data
        // TITLE = mContext.getResources().getString(R.string.rateus);
        SharedPreferences prefs = mContext.getSharedPreferences("rateus", 0);
        if (prefs.getBoolean("dontshowagain", false) && !IS_TEST) {
            return;
        }

        final SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if ((launch_count >= LAUNCHES_UNTIL_PROMPT) || IS_TEST) {
            Log.e(TAG, "(launch_count >= LAUNCHES_UNTIL_PROMPT) || IS_TEST");
            Log.e(TAG, launch_count + "");
            Log.e(TAG, LAUNCHES_UNTIL_PROMPT + "");
            if ((System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) || IS_TEST) {
                Log.e(TAG, "(System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) || IS_TEST");
                Log.e(TAG, System.currentTimeMillis() + "");
                Log.e(TAG, date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000) + "");
                long delay = (IS_TEST ? 1 : DELAY_AFTER_START) * 1000;
                Log.e(TAG, "delay:" + delay);
                new CountDownTimer(delay, delay) {

                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        Log.e(TAG, "onFinish");
                        showRateDialog(mContext, editor);
                    }
                }.start();

            }
        }

        editor.apply();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        final View view = View.inflate(mContext, R.layout.rate_view, null);
        CheckBox dontshow = (CheckBox) view.findViewById(R.id.dontshowcb);
        dontshow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", b);
                    editor.apply();
                }
            }
        });

        TextView later = (TextView) view.findViewById(R.id.later);
        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView rate = (TextView) view.findViewById(R.id.rate);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rate app
                RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rateBar);
                //System.out.println(ratingBar+"");
                float rat = ratingBar.getRating();
                EditText editText = (EditText) view.findViewById(R.id.comment);
                String comment = editText.getText().toString();

                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.putFloat("rateValue", rat);
                    editor.putString("rateComment", comment);
                    editor.putBoolean("ratedDone", true);
                    editor.apply();
                }
                Log.e(TAG, "RATE :" + rat + " ,comment:" + comment);
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    public static void uploadRateRes(Context mContext) {
        final SharedPreferences prefs = mContext.getSharedPreferences("rateus", 0);
        if (prefs.getBoolean("rateUploaded", false) || !prefs.getBoolean("ratedDone", false))
            return;

        String comment = prefs.getString("rateComment", "");
        float rate = prefs.getFloat("rateValue", 0);
        String dev_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);


        Call<String> call = RestHelper.API_SERVICE.rate(AuthHelper.getLoggedUser(mContext).getId(), comment, dev_id, rate);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    //data uploaded => dont ask for rating again
                    prefs.edit().putBoolean("rateUploaded", true).apply();
                    Log.e(TAG, "rate uploaded successfully");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    static class RateInfo {
        public String id;
        public String comment;
        public float rate;
        public String deviceId;
    }
}