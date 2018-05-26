package com.cafateria.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.activities.SelectDiseases;
import com.cafateria.database.PrefManager;
import com.cafateria.handler.Images;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.ProfileImageCropper;
import com.cafateria.rest.RestHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AuthCompleteUserInfo extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "COMPLETE_USER_INFO";
    EditText fname, lname, password, conf_pass, email;
    ImageView img;
    private Bitmap bm;
    TextView student, stuff, state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_complete_user_info);
        AppSettings.setLang(this, AppSettings.getLang(this));
        if (AuthHelper.TEMP_USER == null) {
            finish();
            startActivity(new Intent(this, AuthLog.class));
        } else {
            fname = (EditText) findViewById(R.id.first_name);
            lname = (EditText) findViewById(R.id.last_name);
            email = (EditText) findViewById(R.id.email);
            email.setEnabled(false);
            email.setText(AuthHelper.TEMP_USER.getEmail());
            password = (EditText) findViewById(R.id.password);
            conf_pass = (EditText) findViewById(R.id.conf_pass);
            img = (ImageView) findViewById(R.id.img);
            img.setOnClickListener(this);

            TextView save = (TextView) findViewById(R.id.save);
            save.setOnClickListener(this);

            state = (TextView) findViewById(R.id.state);
            student = (TextView) findViewById(R.id.student);
            student.setOnClickListener(this);
            student.setActivated(true);
            stuff = (TextView) findViewById(R.id.stuff);
            stuff.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:

                String f_name = fname.getText().toString();
                String l_name = lname.getText().toString();
                String _password = password.getText().toString();

                Log.e(TAG, "EMAIL:" + AuthHelper.TEMP_USER.getEmail());
                AuthHelper.TEMP_USER.setId(0);
                AuthHelper.TEMP_USER.setFirst_name(f_name);
                AuthHelper.TEMP_USER.setLast_name(l_name);
                AuthHelper.TEMP_USER.setPassword(_password);
                int userType = student.isActivated() ? AuthUsers.STUDENT : AuthUsers.STUFF;
                AuthHelper.TEMP_USER.setUser_type(userType);
                insertUser(AuthHelper.TEMP_USER);
                break;
            case R.id.img:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
            case R.id.student:
                state.setText(getResources().getString(R.string.just_order));
                stuff.setActivated(false);
                student.setActivated(true);
                break;
            case R.id.stuff:
                state.setText(getResources().getString(R.string.order_delivery));
                stuff.setActivated(true);
                student.setActivated(false);
                break;
        }
    }

    private void insertUser(final AuthUsers user) {
        AuthHelper.startDialog(this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<AuthUsers> call = RestHelper.API_SERVICE.register(user.getFirst_name(), user.getLast_name(), user.getEmail(), user.getPassword(), user.getUser_type());
                    call.enqueue(new Callback<AuthUsers>() {
                        @Override
                        public void onResponse(Call<AuthUsers> call, Response<AuthUsers> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                saveUserAndDirToNext(response.body());
                            } else {
                                Toast.makeText(getApplicationContext(), "error while register ", Toast.LENGTH_SHORT).show();
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

    private void saveUserAndDirToNext(AuthUsers userLogged) {
        Log.e("login result", userLogged.toString());
        PrefManager.setLastLog(this, userLogged.getEmail());
        AuthHelper.saveLoggedUser(this, userLogged);
        finish();

        startActivity(new Intent(this, SelectDiseases.class));
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
            bm = (Bitmap) data.getExtras().get("img");
            img.setImageBitmap(bm);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, AuthLog.class));
    }
}
