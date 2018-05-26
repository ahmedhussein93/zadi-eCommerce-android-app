package com.cafateria.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.auth.AccountVerificationMethods;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.auth.AuthUsers;
import com.cafateria.auth.Interfaces;
import com.cafateria.handler.Images;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.helper.Init;
import com.cafateria.helper.ProfileImageCropper;
import com.cafateria.model.Disease;
import com.cafateria.rest.RestHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Account extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "ACCOUNT";
    ImageView edit_name, edit_email, edit_password, user_image;
    EditText name, email, password;
    AuthUsers user;
    Map<String, Object> map = new HashMap<>();
    LinearLayout wrapper;
    List<Integer> ids = new ArrayList<>();
    private List<Disease> diseases;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        AppSettings.setLang(this, AppSettings.getLang(this));
        Init.initDrawer(this, R.id.nav_account);
        getSupportActionBar().setTitle(getResources().getString(R.string.my_account));
        user = AuthHelper.getLoggedUser(this);
        Log.e("ACCOUNT", new Gson().toJson(user, AuthUsers.class));
        ids.clear();
        init();
        setInfo(user);
        Log.e(TAG, "ds#:" + user.getDiseases().size());
    }

    private void setInfo(AuthUsers user) {
        //Log.e(TAG, "email" + user.getEmail());
        name.setText(user.getFirst_name() + " " + user.getLast_name());
        email.setText(user.getEmail().length() < 3 ? "" : user.getEmail());
        password.setText(user.getPassword());
        // Images.set(this, user_image, "profile/" + user.getId() + ".png", Paths.profileImagesDir + "/" + user.getId() + ".png");
        //setPics(user_image);

        setDieasesView();
    }

    private void setDieasesView() {
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
                                setDiseases(response.body());
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


    private void init() {
        wrapper = (LinearLayout) findViewById(R.id.wrapper);
        //name ----------------------------------------------------------
        name = (EditText) findViewById(R.id.user_profile_name);
        edit_name = (ImageView) findViewById(R.id.edit_name);
        edit_name.setOnClickListener(this);
        //---------------------------------------------------------------
        //email ----------------------------------------------------------
        email = (EditText) findViewById(R.id.email_ed);
        edit_email = (ImageView) findViewById(R.id.edit_email);
        edit_email.setOnClickListener(this);
        //---------------------------------------------------------------
        //password ----------------------------------------------------------
        password = (EditText) findViewById(R.id.password_ed);
        edit_password = (ImageView) findViewById(R.id.edit_password);
        edit_password.setOnClickListener(this);
        //---------------------------------------------------------------
        //-------------user image -----
        user_image = (ImageView) findViewById(R.id.user_profile_photo);
        //user_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_name:
                if (edit_name.getTag().toString().equalsIgnoreCase("0")) {//default state => click to edit
                    name.setEnabled(true);
                    edit_name.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_black_24dp));
                    edit_name.setTag("1");
                } else {//click to save
                    name.setEnabled(false);
                    edit_name.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
                    edit_name.setTag("0");
                    if (!name.getText().toString().equalsIgnoreCase(user.getFirst_name() + " " + user.getLast_name())) {
                        //send info to update

                        map.clear();
                        map.put("first_name", name.getText().toString().split(" ")[0]);
                        map.put("last_name", name.getText().toString().split(" ")[1]);

                        user.setFirst_name(name.getText().toString().split(" ")[0]);
                        user.setLast_name(name.getText().toString().split(" ")[1]);
                        updateInfo(map);
                    }
                }
                break;
            // TODO: 06/03/2018  need confirmation
            //------------------------end name click -----------
            case R.id.edit_email:
                if (edit_email.getTag().toString().equalsIgnoreCase("0")) {//default state => click to edit
                    email.setEnabled(true);
                    edit_email.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_black_24dp));
                    edit_email.setTag("1");
                } else {//click to save
                    email.setEnabled(false);
                    edit_email.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
                    edit_email.setTag("0");
                    final String _email = email.getText().toString();
                    if (!_email.equalsIgnoreCase(user.getEmail())) {
                        AccountVerificationMethods.confirmMail(Account.this, _email, new Interfaces.callback() {
                            @Override
                            public void call(boolean isConfirmed) {
                                if (isConfirmed) {
                                    //send info to update
                                    map.clear();
                                    map.put("email", _email);
                                    user.setEmail(email.getText().toString());
                                    updateInfo(map);
                                } else {
                                    email.setText(user.getEmail());
                                }
                            }
                        });
                    }
                }
                break;
            //------------------------end email click -----------

            case R.id.edit_password:
                if (edit_password.getTag().toString().equalsIgnoreCase("0")) {//default state => click to edit
                    password.setEnabled(true);
                    edit_password.setImageDrawable(getResources().getDrawable(R.drawable.ic_save_black_24dp));
                    edit_password.setTag("1");
                } else {//click to save
                    password.setEnabled(false);
                    edit_password.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
                    edit_password.setTag("0");
                    if (!password.getText().toString().equalsIgnoreCase(user.getPassword())) {
                        //send info to update
                        map.clear();
                        map.put("password", password.getText().toString());
                        user.setPassword(password.getText().toString());
                        updateInfo(map);
                    }
                }
                break;
            //------------------------end password click -----------

            case R.id.user_profile_photo:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == 1) {

            Uri selectedImage = data.getData();
            String filePath = Images.getPathFromUri(this, selectedImage);
            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);


            System.out.println("path:" + filePath);
            Intent i = new Intent(this, ProfileImageCropper.class);
            //Bundle bundle = new Bundle();
            i.putExtra("filepath", filePath);
            //startActivity(i);
            startActivityForResult(i, 100);

        } else if (resultCode == RESULT_OK && requestCode == 100) {
            System.out.println("back from cropping");
            Bitmap bm = (Bitmap) data.getExtras().get("img");
           /* Images.update(this, bm, user_image, "profile/" + user.getId() + ".png", Paths.profileImagesDir + "/" + user.getId() + ".png", new Callback() {
                @Override
                public void onComplete(boolean b) {
                }
            });*/
        }
    }

    private void updateInfo(final Map map) {
        //map.put("id", user.getId());
        Log.e(TAG, "updateInfo:" + map.toString());
        AuthHelper.startDialog(Account.this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<AuthUsers> call = RestHelper.API_SERVICE.update(map, AuthHelper.getLoggedUser(getApplicationContext()).getId());
                    call.enqueue(new retrofit2.Callback<AuthUsers>() {
                        @Override
                        public void onResponse(Call<AuthUsers> call, Response<AuthUsers> response) {
                            Log.e(TAG, "result:" + response);
                            Log.e(TAG, "result:" + response.body());
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                AuthHelper.saveLoggedUser(getApplicationContext(), response.body());
                                Toast.makeText(getApplication(), "data updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "error while updating ", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, response.message());
                                Log.e(TAG, response.code() + "");
                                Log.e(TAG, response.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthUsers> call, Throwable t) {
                            AuthHelper.stopDialog();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            LauncherApp.start(this);
        }
    }

    public void uploadDiseases(View view) {
        ids.clear();
        for (int x = 0; x < wrapper.getChildCount(); x++) {
            CheckBox cb = (CheckBox) wrapper.getChildAt(x);
            int id = (int) cb.getTag();
            if (cb.isChecked() && id != -1)
                ids.add(id);
        }

        //send values
        //if (ids.size() > 0) {
        AuthHelper.startDialog(this);

        Call<String> call = RestHelper.API_SERVICE.sendDiseases(ids, AuthHelper.getLoggedUser(this).getId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, response.body());
                AuthHelper.stopDialog();
                Log.e(TAG, "send finished");
                if (response.isSuccessful()) {
                    updateUserDiseasesInLocaleStorage(ids);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.diseases_updated), Toast.LENGTH_SHORT).show();
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
        // }
    }

    private void updateUserDiseasesInLocaleStorage(List<Integer> ids) {
        List<Disease> userDiseases = new ArrayList<>();
        for (Disease disease : diseases) {
            if (ids.contains(disease.id))
                userDiseases.add(disease);
        }
        user.setDiseases(userDiseases);
        AuthHelper.saveLoggedUser(this, user);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            if ((int) compoundButton.getTag() == -1) {//if nothing btn uncheck all eccept it
                for (int x = 0; x < wrapper.getChildCount(); x++) {
                    if (wrapper.getChildAt(x) != null)
                        ((CheckBox) wrapper.getChildAt(x)).setChecked(false);
                }
                compoundButton.setChecked(true);
            } else {
                if (wrapper.findViewWithTag(-1) != null)
                    ((CheckBox) wrapper.findViewWithTag(-1)).setChecked(false);
            }
        }
    }

    private void setDiseases(List<Disease> diseases) {
        CheckBox cb;
        for (Disease disease : diseases) {
            cb = new CheckBox(this);
            cb.setButtonDrawable(R.drawable.check_selector);
            cb.setText(disease.get_name(this));
            cb.setTag(disease.id);
            cb.setTextSize(20);
            cb.setOnCheckedChangeListener(this);
            //check if i have -> check it
            for (Disease d : user.getDiseases()) {
                if (d.id == disease.id) {
                    //Log.e(TAG, d.id + "-" + disease.id);
                    Log.e(TAG, cb.getTag() + "-> must be checked");
                    cb.setChecked(true);
                }
            }
            wrapper.addView(cb);
        }
        //nothing
        cb = new CheckBox(this);
        cb.setButtonDrawable(R.drawable.check_selector);
        cb.setText(getResources().getString(R.string.nothing));
        cb.setTextSize(20);
        cb.setOnCheckedChangeListener(this);
        cb.setTag(-1);
        wrapper.addView(cb);
    }
}
