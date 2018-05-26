package com.cafateria.model;

import android.content.Context;

import com.cafateria.helper.AppSettings;

/**
 * Created  on 12/03/2018.
 * *  *
 */

public class Disease {
    public int id;
    public String name, name_en;

    public String get_name(Context context) {
        return AppSettings.getLang(context).equalsIgnoreCase("en") ? name_en : name;
    }
}
