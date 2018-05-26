package com.cafateria.auth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.activities.LauncherApp;
import com.cafateria.database.PrefManager;
import com.cafateria.helper.AppSettings;
import com.cafateria.rest.RestHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthLog extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AUTH_LOG";
    private static final int GET_ACCOUNTS_REQUEST = 1;
    EditText mail, password;
    TextView forget_password;
    AppCompatButton create_account, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSettings.setLang(this, AppSettings.getLang(this));
        setContentView(R.layout.activity_auth_log);
        init();
    }

    private void init() {

        password = (EditText) findViewById(R.id.password);
        mail = (EditText) findViewById(R.id.phone);
        mail.setText(PrefManager.getLastLog(this));

        login = (AppCompatButton) findViewById(R.id.login_btn);
        create_account = (AppCompatButton) findViewById(R.id.create_account);

        forget_password = (TextView) findViewById(R.id.forget_password);
        forget_password.setOnClickListener(this);

        login.setOnClickListener(this);
        create_account.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String email, pass;
        switch (v.getId()) {
            case R.id.login_btn:
                email = mail.getText().toString();
                pass = password.getText().toString();
                if (email.length() < 5) {
                    mail.setError(getResources().getString(R.string.email_not_vaid));
                } else if (pass.length() < 8) {
                    password.setError(getResources().getString(R.string.auth_pass_less));
                } else {
                    email = email.replaceAll(" ", ""); //remove any spaces
                    login(email, pass);
                }
                break;
            case R.id.create_account:
                finish();
                startActivity(new Intent(this, AuthConfirmAccount.class));
                break;
            case R.id.forget_password:
                Intent i = new Intent(this, AuthConfirmAccount.class);
                i.putExtra(AuthHelper.IS_RECOVER_PASSWORD, true);
                i.putExtra("mail", mail.getText().toString());
                startActivity(i);
                break;
        }
    }

    private void login(final String email, final String password) {
        AuthHelper.startDialog(this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<AuthUsers> call = RestHelper.API_SERVICE.getUser(email, password);
                    call.enqueue(new Callback<AuthUsers>() {
                        @Override
                        public void onResponse(Call<AuthUsers> call, Response<AuthUsers> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                AuthUsers user = response.body();
                                if (user != null) {
                                    saveUserAndDirToHome(user);
                                } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.mailorpassinvalid), Toast.LENGTH_SHORT).show();
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


    private void saveUserAndDirToHome(AuthUsers userLogged) {
        finish();
        if (userLogged.getState() == AuthUsers.REJECTED) {
            Toast.makeText(this, getResources().getString(R.string.account_deleted), Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("login result", userLogged.toString());
        PrefManager.setLastLog(this, userLogged.getEmail());
        AuthHelper.saveLoggedUser(this, userLogged);
        LauncherApp.start(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("onRequestPermissionsResult");
        boolean allPermissionsAllowed = true;
        if (requestCode == GET_ACCOUNTS_REQUEST) {
            for (int grantResult : grantResults)
                if (grantResult != PackageManager.PERMISSION_GRANTED)
                    allPermissionsAllowed = false;

            if (allPermissionsAllowed) {
                Toast.makeText(this, "get accounts", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.agin_to_exist), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
