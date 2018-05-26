package com.cafateria.model;

import android.content.Context;

import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.rest.RestHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Cats {
    int id;
    String name_ar, name_en;
    public String num_of_items = "";

    public Cats() {
    }

    public Cats(int id, String name_ar, String name_en) {
        this.id = id;
        this.name_ar = name_ar;
        this.name_en = name_en;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_ar() {
        return name_ar;
    }

    public void setName_ar(String name_ar) {
        this.name_ar = name_ar;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName(Context context) {
        return AppSettings.getLang(context).equalsIgnoreCase("ar") ? name_ar : name_en;
    }

    public static void updateCats(final com.cafateria.helper.Callback callback) {
        Call<List<Cats>> call = RestHelper.API_SERVICE.getCats();
        call.enqueue(new Callback<List<Cats>>() {
            @Override
            public void onResponse(Call<List<Cats>> call, Response<List<Cats>> response) {
                if (response.isSuccessful()) {
                    Global.CATS.clear();
                    Global.CATS.addAll(response.body());
                    callback.onComplete(true);
                } else {
                    callback.onComplete(false);
                }
            }

            @Override
            public void onFailure(Call<List<Cats>> call, Throwable t) {
                t.printStackTrace();
                callback.onComplete(false);
            }
        });
    }
}
