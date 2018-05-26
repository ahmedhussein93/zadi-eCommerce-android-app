package com.cafateria.auth;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.cafateria.R;
import com.cafateria.helper.Global;
import com.cafateria.mailer.Mailer;

import java.util.Random;

/**
 * Created  on 06/03/2018.
 * *  *
 */

public class AccountVerificationMethods {
   /* private static FirebaseAuth mAuth;
    private static PhoneAuthProvider.ForceResendingToken mResendToken = null;
    private static String mVerificationId = null;*/

    public static void confirmMail(final Activity activity, String email, final Interfaces.callback callback) {
        //1-send code
        Global.RANDOM = 100000 + new Random().nextInt(900000);
        Mailer.send(activity, Mailer.SMTP_MAIL, Mailer.SMTP_PASS, email, "Zadi Application - mail confirmation", "plz use this code to conform your email :" + Global.RANDOM, new Interfaces.callback1() {
            @Override
            public void onComplete(boolean is) {
                if (is) {
                    Toast.makeText(activity, R.string.mail_sent, Toast.LENGTH_SHORT).show();
                    showMailCodeDialog(activity, callback);
                } else {
                    Toast.makeText(activity, R.string.email_verf_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void showMailCodeDialog(final Activity activity, final Interfaces.callback callback) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        final View view = View.inflate(activity, R.layout.code_view, null);

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.call(false);
                dialog.dismiss();
            }
        });
        TextView verify = (TextView) view.findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText code = (EditText) view.findViewById(R.id.code);
                String _code = code.getText().toString();

                if (Global.RANDOM == Integer.parseInt(_code)) {
                    dialog.dismiss();
                    callback.call(true);
                } else
                    Toast.makeText(activity, activity.getString(R.string.auth_invalid_code), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }


    //--------phone
/*
    public static void confirmPhone(final Activity activity, String phone, final Interfaces.callback callback) {

        AuthHelper.startDialog(activity);
        if (PrefManager.getCountryCode(activity).endsWith("0") && phone.startsWith("0"))
            phone = phone.substring(1);
        phone = PrefManager.getCountryCode(activity) + phone;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
// TODO: 06/03/2018
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // TODO: 06/03/2018
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        AuthHelper.stopDialog();
                        showPhoneCodeDialog(activity, callback);
                        Toast.makeText(activity, R.string.code_sent_phone, Toast.LENGTH_SHORT).show();
                        mResendToken = forceResendingToken;
                        mVerificationId = s;
                        super.onCodeSent(s, forceResendingToken);
                    }
                },
                mResendToken);
    }


    public static void showPhoneCodeDialog(final Activity activity, final Interfaces.callback callback) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        final View view = View.inflate(activity, R.layout.code_view, null);

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.call(false);
                dialog.dismiss();
            }
        });
        TextView verify = (TextView) view.findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText code = (EditText) view.findViewById(R.id.code);
                final String _code = code.getText().toString();

                if (mVerificationId != null && !_code.isEmpty()) {
                    AuthHelper.startDialog(activity);
                    AuthInternet.action(activity, new AuthNetworking() {
                        @Override
                        public void accessInternet(boolean isConnected) {
                            if (isConnected) {
                                mAuth = FirebaseAuth.getInstance();
                                mAuth.signInWithCredential(PhoneAuthProvider.getCredential(mVerificationId, _code)).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        AuthHelper.stopDialog();
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            callback.call(true);
                                        } else {
                                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                                Toast.makeText(activity, activity.getString(R.string.auth_invalid_code), Toast.LENGTH_SHORT).show();
                                            } else {
                                                dialog.dismiss();
                                                callback.call(false);
                                            }
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(activity, activity.getString(R.string.auth_no_internet_con), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }*/
}
