package com.cafateria.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.activities.AdminSettings;
import com.cafateria.activities.LessQuantity;
import com.cafateria.database.PrefManager;
import com.cafateria.model.Food;

import java.util.List;

/**
 * Created by ahmed on 3/21/2018.
 * *  *
 */

public class Dialogs {
    private static Dialog dialog;

    public static void showLessQuantityAlert(Context context, final List<Food> lessQuantityItems) {
        if (Global.CURRENT_ACTIVITY == null) {
            Log.e("Dialogs", "App Not Shown activity=null");
            return;
        }
        //context = Global.CURRENT_ACTIVITY;
        Log.e("Dialogs", "showLessQuantityAlert");
        // if (!Global.isAppSown(context)) return;


        if (Global.CURRENT_ACTIVITY.isDestroyed()) { // or call isFinishing() if min sdk version < 17
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }


        Log.e("Dialogs", "onFinish");
        dialog = new Dialog(Global.CURRENT_ACTIVITY);
        final View view = View.inflate(Global.CURRENT_ACTIVITY, R.layout.less_quantity_alert_dialog, null);
        CheckBox dontshow = (CheckBox) view.findViewById(R.id.dontshowagaintodaychbx);
        dontshow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PrefManager.setAlertDontShowToday(Global.CURRENT_ACTIVITY);
            }
        });

        TextView show_details = (TextView) view.findViewById(R.id.dir_to_less_quantity_activity);
        show_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Global.CURRENT_ACTIVITY.startActivity(new Intent(Global.CURRENT_ACTIVITY, LessQuantity.class));
            }
        });
        TextView count = (TextView) view.findViewById(R.id.count);
        count.setText(lessQuantityItems.size() + "");
        TextView close = (TextView) view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ImageView settings = view.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.CURRENT_ACTIVITY.finish();
                Intent intent = new Intent(Global.CURRENT_ACTIVITY, AdminSettings.class);
                Global.CLASS_TO_BACK = ((Activity) Global.CURRENT_ACTIVITY).getClass();
                Global.CURRENT_ACTIVITY.startActivity(intent);
            }
        });
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();

        //android.view.WindowManager$BadTokenException: Unable to add window -- token null is not for an application
    }
}
