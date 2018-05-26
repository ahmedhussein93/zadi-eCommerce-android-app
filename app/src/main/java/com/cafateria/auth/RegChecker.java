package com.cafateria.auth;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

/**
 * Created by pc on 16/02/2018.
 */

public class RegChecker {
    public static boolean isEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isPhone(String target) {
        return !TextUtils.isEmpty(target) && PhoneNumberUtils.isGlobalPhoneNumber(target);
    }
}
