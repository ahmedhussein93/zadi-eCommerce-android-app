package com.cafateria.helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cafateria.activities.DialogLessActivity;
import com.cafateria.database.PrefManager;
import com.cafateria.model.Food;
import com.cafateria.rest.RestHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ahmed on 3/21/2018.
 * *  *
 */

public class AdminHelper {

    public static void checkLessQuantity(final Context context) {
        Log.e("AdminHelper", "checkLessQuantity");
        int q = PrefManager.getLessQuantity(context);
        Log.e("---", "quantity :" + q);
        Call<List<Food>> call = RestHelper.API_SERVICE.getLessQuantity(q);
        call.enqueue(new retrofit2.Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        Log.e("AdminHelper", "checkLessQuantity size:" + response.body().size());
                        Intent i = new Intent(context, DialogLessActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("q", response.body().size());
                        context.startActivity(i);

                        //Dialogs.showLessQuantityAlert(context, response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
