package com.cafateria.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class AuthRecoverPassword extends AppCompatActivity implements View.OnClickListener {

    TextView phone, continue_btn, logo;
    EditText pass, conf_pass;

    //user come here with full object from user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_recover_password);
        AppSettings.setLang(this, AppSettings.getLang(this));

        continue_btn = (TextView) findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(this);

        pass = (EditText) findViewById(R.id.pass);
        conf_pass = (EditText) findViewById(R.id.conf_pass);
    }

    @Override
    public void onClick(View v) {
        String password = pass.getText().toString();
        if (pass.getText().toString().isEmpty() || pass.getText().length() < 8) {
            pass.setError(getResources().getString(R.string.auth_pass_less));
        } else if (!password.equals(conf_pass.getText().toString())) {
            conf_pass.setError(getResources().getString(R.string.auth_pass_not_match));
        } else {
            //update password
            updatePass(password);
        }
    }

    private void updatePass(final String password) {
        AuthHelper.startDialog(this);
        final String email = AuthHelper.TEMP_USER.getEmail();
        AuthHelper.startDialog(this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<AuthUsers> call = RestHelper.API_SERVICE.updatePassword(email, password);
                    call.enqueue(new Callback<AuthUsers>() {
                        @Override
                        public void onResponse(Call<AuthUsers> call, Response<AuthUsers> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                saveUserAndDirToHome(response.body());
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, AuthLog.class));
    }

    private void saveUserAndDirToHome(AuthUsers userLogged) {
        Log.e("login result", userLogged.toString());
        PrefManager.setLastLog(this, userLogged.getEmail());
        AuthHelper.saveLoggedUser(this, userLogged);
        finish();
        LauncherApp.start(this);
    }
}
