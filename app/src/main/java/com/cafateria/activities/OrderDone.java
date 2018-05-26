package com.cafateria.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;

public class OrderDone extends AppCompatActivity {

    TextView order_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oerder_done);
        AppSettings.setLang(this, AppSettings.getLang(this));
        order_number = (TextView) findViewById(R.id.order_number);
        String id = getIntent().getExtras().getString("order_id", "00");
        order_number.setText(id);
    }

    public void exit(View view) {
        Global.reset();
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    public void home(View view) {
        Global.reset();
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
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
