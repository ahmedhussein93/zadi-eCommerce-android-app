package com.cafateria.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.database.PrefManager;
import com.cafateria.helper.Global;

public class DialogLessActivity extends AppCompatActivity {

    private int q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.less_quantity_alert_dialog);

        Bundle b = getIntent().getExtras();
        if (b == null || b.getInt("q", 0) == 0) {
            finish();
        } else {
            q = b.getInt("q", 0);

            CheckBox dontshow = (CheckBox) findViewById(R.id.dontshowagaintodaychbx);
            dontshow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    PrefManager.setAlertDontShowToday(getApplicationContext());
                }
            });

            TextView show_details = (TextView) findViewById(R.id.dir_to_less_quantity_activity);
            show_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), LessQuantity.class));
                }
            });
            TextView count = (TextView) findViewById(R.id.count);
            count.setText(q + "");
            TextView close = (TextView) findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            ImageView settings = (ImageView) findViewById(R.id.settings);
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), AdminSettings.class);
                    Global.CLASS_TO_BACK = DialogLessActivity.this.getClass();
                    startActivity(intent);
                }
            });
        }
    }
}
