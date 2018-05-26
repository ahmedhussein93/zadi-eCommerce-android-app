package com.cafateria.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.rest.RestHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthConfirmAccount extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AuthConfirmAccount";
    public static AuthConfirmAccount mThis;
    //######## ------------- vars --------------#########3
    EditText mail;
    private String _mail = "";
    private boolean isRecoverPassword = false;
    AppCompatButton sendCodeBtn, enterCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_create_account);
        mThis = this;
        AppSettings.setLang(this, AppSettings.getLang(this));
        isRecoverPassword = getIntent().getExtras() != null && getIntent().getExtras().getBoolean(AuthHelper.IS_RECOVER_PASSWORD, false);
        _mail = getIntent().getExtras() != null ? getIntent().getExtras().getString("mail", "") : "";
        init();
        mail.setText(_mail);
    }

    @Override
    public void onClick(View view) {
        AuthHelper.hideKeyboard(this);
        switch (view.getId()) {

            case R.id.sendcode:
                Log.e(TAG, "sendcode");
                if (mail.getText() == null || mail.getText().length() < 8) {
                    mail.setError(getResources().getString(R.string.email_not_vaid));
                } else {
                    mail.setError(null);
                    _mail = mail.getText().toString();
                    checkAvailability(_mail);
                }
                break;
            case R.id.enter_code:
                showCodeDialog();
                break;
        }
    }

    private void checkAvailability(final String mail) {
        AuthHelper.startDialog(this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<AuthUsers> call = RestHelper.API_SERVICE.getUser(mail);
                    call.enqueue(new Callback<AuthUsers>() {
                        @Override
                        public void onResponse(Call<AuthUsers> call, Response<AuthUsers> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                AuthUsers user = response.body();
                                if (user != null) {//mail exist on db
                                    if (isRecoverPassword) {
                                        //send v c
                                        if (user.getState() == AuthUsers.REJECTED) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.account_deleted), Toast.LENGTH_SHORT).show();
                                        } else
                                            sendMailCode(mail);
                                    } else {//create account
                                        //mail already registered
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.mail_is_registred_use_another_or_login), Toast.LENGTH_SHORT).show();
                                    }
                                } else {//mail not exist
                                    if (isRecoverPassword) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrong_mail), Toast.LENGTH_SHORT).show();
                                    } else {//create account
                                        //send v c
                                        sendMailCode(mail);
                                    }
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, response.message());
                                Log.e(TAG, response.code() + "");
                                Log.e(TAG, response.toString());
                            }

                        }

                        @Override
                        public void onFailure(Call<AuthUsers> call, Throwable t) {

                        }
                    });
                } else {
                    AuthHelper.stopDialog();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void sendMailCode(final String mail) {
        AccountVerificationMethods.confirmMail(this, mail, new Interfaces.callback() {
            @Override
            public void call(boolean is) {
                if (is) {
                    mailAuthenticated(mail);
                }
            }
        });
    }

    //

    private void init() {
        mail = (EditText) findViewById(R.id.mail);

        sendCodeBtn = (AppCompatButton) findViewById(R.id.sendcode);
        sendCodeBtn.setOnClickListener(this);

        enterCodeBtn = (AppCompatButton) findViewById(R.id.enter_code);
        enterCodeBtn.setOnClickListener(this);
    }

    private void mailAuthenticated(String email) {
        if (AuthHelper.TEMP_USER == null)
            AuthHelper.TEMP_USER = new AuthUsers();

        AuthHelper.TEMP_USER.setEmail(email);//// TODO: 20/02/2018  
        finish();
        if (!isRecoverPassword) {
            startActivity(new Intent(this, AuthCompleteUserInfo.class));
        } else {
            startActivity(new Intent(this, AuthRecoverPassword.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, AuthLog.class));
        super.onBackPressed();
    }

    public void showCodeDialog() {
        final Dialog dialog = new Dialog(mThis);
        final View view = View.inflate(mThis, R.layout.code_view, null);

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView verify = (TextView) view.findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText code = (EditText) view.findViewById(R.id.code);
                String _code = code.getText().toString();
                if (_mail != null) {
                    if (Global.RANDOM == Integer.parseInt(_code)) {
                        mailAuthenticated(_mail);
                    } else
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.auth_invalid_code), Toast.LENGTH_SHORT).show();
                } else
                    dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }
}
