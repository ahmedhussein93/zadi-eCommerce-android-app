package com.cafateria.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cafateria.R;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.squareup.picasso.Picasso;

public class Details extends AppCompatActivity {

    private ImageView cover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSettings.setLang(this, AppSettings.getLang(this));
        //setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Global.FOOD_TO_SHOW_DETAILS == null) {
            finish();
            return;
        }
        //cover = (ImageView)findViewById(R.id.header_logo);
        //Picasso.with(this).load(Global.images_base_yrl + movie.getPosterPath()).into(cover);
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
