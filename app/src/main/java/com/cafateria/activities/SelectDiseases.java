package com.cafateria.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.auth.AuthUsers;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.model.Disease;
import com.cafateria.rest.RestHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectDiseases extends AppCompatActivity {
    private static final String TAG = "SELECT_DISEASE";
    LinearLayout wrapper;
    List<Integer> ids = new ArrayList<>();
    private List<Disease> diseases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_diseases);
        AppSettings.setLang(this, AppSettings.getLang(this));
        wrapper = (LinearLayout) findViewById(R.id.wrapper);
        ids.clear();

        AuthHelper.startDialog(this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<List<Disease>> call = RestHelper.API_SERVICE.getDiseases();
                    call.enqueue(new Callback<List<Disease>>() {
                        @Override
                        public void onResponse(Call<List<Disease>> call, Response<List<Disease>> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                diseases = response.body();
                                setDiseases(diseases);
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, response.message());
                                Log.e(TAG, response.code() + "");
                                Log.e(TAG, response.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Disease>> call, Throwable t) {

                        }
                    });
                } else {
                    AuthHelper.stopDialog();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setDiseases(List<Disease> diseases) {
        CheckBox cb;
        for (Disease disease : diseases) {
            cb = new CheckBox(this);
            cb.setButtonDrawable(R.drawable.check_selector);
            cb.setText(disease.get_name(this));
            cb.setTextSize(20);
            cb.setTag(disease.id);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    for (int x = 0; x < wrapper.getChildCount(); x++) {
                        CheckBox cb = (CheckBox) wrapper.getChildAt(x);
                        if ((int) cb.getTag() == -1)
                            cb.setChecked(false);
                    }
                }
            });
            wrapper.addView(cb);
        }
        //nothing
        cb = new CheckBox(this);
        cb.setTextSize(20);
        cb.setButtonDrawable(R.drawable.check_selector);
        cb.setText(getResources().getString(R.string.nothing));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (int x = 0; x < wrapper.getChildCount(); x++) {
                        CheckBox cb = (CheckBox) wrapper.getChildAt(x);
                        if ((int) cb.getTag() != -1)
                            cb.setChecked(false);
                    }
                    compoundButton.setChecked(true);
                }
            }
        });
        cb.setTag(-1);
        wrapper.addView(cb);
    }

    public void done(View view) {
        uploadDiseases();
    }

    private void uploadDiseases() {
        ids.clear();
        for (int x = 0; x < wrapper.getChildCount(); x++) {
            CheckBox cb = (CheckBox) wrapper.getChildAt(x);
            int id = (int) cb.getTag();
            if (cb.isChecked() && id != -1)
                ids.add(id);
        }

        //send values
        if (ids.size() > 0) {
            AuthHelper.startDialog(this);
            AuthInternet.action(this, new AuthNetworking() {
                @Override
                public void accessInternet(boolean isConnected) {
                    if (isConnected) {
                        Call<String> call = RestHelper.API_SERVICE.sendDiseases(ids, AuthHelper.getLoggedUser(getApplicationContext()).getId());
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                AuthHelper.stopDialog();
                                Log.e(TAG, "send finished");
                                if (response.isSuccessful()) {
                                    updateUserDiseasesInLocaleStorage(ids);
                                    //System.out.println(response.body());
                                    finish();
                                    LauncherApp.start(SelectDiseases.this);
                                } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, response.message());
                                    Log.e(TAG, response.code() + "");
                                    Log.e(TAG, response.toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                    } else {
                        AuthHelper.stopDialog();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            finish();
            LauncherApp.start(SelectDiseases.this);
        }
    }

    private void updateUserDiseasesInLocaleStorage(List<Integer> ids) {
        List<Disease> userDiseases = new ArrayList<>();
        for (Disease disease : diseases) {
            if (ids.contains(disease.id))
                userDiseases.add(disease);
        }
        AuthUsers loggedUser = AuthHelper.getLoggedUser(this);
        loggedUser.setDiseases(userDiseases);
        AuthHelper.saveLoggedUser(this, loggedUser);
    }

    @Override
    protected void onResume() {
        Global.CURRENT_ACTIVITY = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        Global.CURRENT_ACTIVITY = null;
        super.onPause();
    }
}
