package com.cafateria.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.database.PrefManager;
import com.cafateria.handler.Paths;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Callback;
import com.cafateria.helper.Global;
import com.cafateria.model.Cats;

public class Splash extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "splash";
    private ImageView logo, country_flag, start;
    //TextView splash_state;
    private RelativeLayout root;
    private LinearLayout wrapper;
    private ObjectAnimator anim;
    private Animation anim_logo;
    private boolean doubleBackToExitPressedOnce = false;
    //private Spinner countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // splash_state = (TextView) findViewById(R.id.splash_state);
        logo = (ImageView) findViewById(R.id.logo);
        AppSettings.setLang(this, AppSettings.getLang(this));
        anim_logo = AnimationUtils.loadAnimation(this, R.anim.rev_fade);
        logo.startAnimation(anim_logo);
        wrapper = (LinearLayout) findViewById(R.id.wrapper);
        wrapper.setVisibility(View.INVISIBLE);

        start = (ImageView) findViewById(R.id.start);
        start.setOnClickListener(this);
        country_flag = (ImageView) findViewById(R.id.country_flag);
        country_flag.setOnClickListener(this);
        country_flag.setTag(R.string.TAG_COUNTRY_CODE, "+966");//default value
        country_flag.setTag(R.string.TAG_COUNTRY_POSITION, 0);

        //start backgrond animation
        root = (RelativeLayout) findViewById(R.id.root);
        anim = ObjectAnimator.ofInt(root, "backgroundColor", getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent3));
        anim.setDuration(3000);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.start();
        requestForSpecificPermission();
    }


    private void requestForSpecificPermission() {
        //splash_state.setText(getResources().getString(R.string.check_permissions));
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("onRequestPermissionsResult");
        boolean allPermissionsAllowed = true;
        for (int grantResult : grantResults)
            if (grantResult != PackageManager.PERMISSION_GRANTED)
                allPermissionsAllowed = false;
        if (allPermissionsAllowed) {
            startApp();
           /* splash_state.setText(getResources().getString(R.string.check_internet));
            AuthInternet.action(this, new AuthNetworking() {
                @Override
                public void accessInternet(boolean isConnected) {
                    if (isConnected) {
                        splash_state.setText(getResources().getString(R.string.internet_connected));
                        Log.e(TAG, "accessInternet");
                        getOnlineInfo();
                    } else {
                        Log.e(TAG, "no accessInternet");
                        splash_state.setText(getResources().getString(R.string.auth_no_internet_con));
                        startApp();
                    }
                }
            });*/
        } else
            requestForSpecificPermission();

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startApp() {
        if (PrefManager.isFirstLaunch(this)) {
            Paths.createPaths();
            //splash_state.setText(getResources().getString(R.string.selecting_language));
            wrapper.setVisibility(View.VISIBLE);
            //logo.sto
            if (anim != null)
                anim.cancel();
            //if (anim_logo != null)
            //    anim_logo.cancel();
        } else {
            start();
        }
    }

    public void lang(final View view) {
        AppSettings.setLang(Splash.this, view.getId() == R.id.ar ? "ar" : "en");
        Log.e(TAG, "selected language is:" + AppSettings.getLang(this));
        start();
    }


    private void start() {
        PrefManager.firstLaunch(this);
        Cats.updateCats(new Callback() {
            @Override
            public void onComplete(boolean b) {

            }
        });
        finish();
        Paths.createPaths();
        LauncherApp.start(Splash.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.country_flag) {
            //showCountryPicker(view);
        } else if (view.getId() == R.id.start) {
            start();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.agin_to_exist), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        Global.CURRENT_ACTIVITY = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        Global.CURRENT_ACTIVITY = null;
        super.onPause();
    }
}
