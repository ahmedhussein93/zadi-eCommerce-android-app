package com.cafateria.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.auth.AuthUsers;
import com.cafateria.handler.Images;
import com.cafateria.handler.Paths;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.model.Cats;
import com.cafateria.model.Disease;
import com.cafateria.model.Food;
import com.cafateria.rest.RestHelper;
import com.github.javiersantos.bottomdialogs.BottomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFood extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ADD_FOOD";
    private static final int PICK_FROM_CAMERA_CODE = 101;
    private static final int PICK_FROM_GALLERY_CODE = 102;
    ImageView icon;
    TextView add;
    EditText item_name, item_desc, item_quantity, item_price;
    private boolean isImageChanged = false, isEdit = false;

    Spinner food_type_spinner;
    private AuthUsers user;
    private String id = null;
    private Uri uri;
    Map<String, Integer> map;
    LinearLayout wrapper;
    List<Integer> ids = new ArrayList<>();
    List<String> cats = new ArrayList<>();
    LayoutInflater inflater;
    private BottomDialog dialog;

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
        setContentView(R.layout.activity_add_food);
        AppSettings.setLang(this, AppSettings.getLang(this));
        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("id")) {
            id = bundle.getString("id");
            add.setText(getResources().getString(R.string.save));
            isEdit = true;
        }

        wrapper = (LinearLayout) findViewById(R.id.wrapper);
        ids.clear();

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


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

    private void setDiseases(List<Disease> diseases) {
        CheckBox cb;
        for (Disease disease : diseases) {
            cb = new CheckBox(this);
            cb.setText(disease.get_name(this));
            cb.setTextSize(20);
            cb.setButtonDrawable(R.drawable.check_selector);
            cb.setTag(disease.id);
            wrapper.addView(cb);
        }
    }


    private void init() {
        icon = (ImageView) findViewById(R.id.store_icon);
        icon.setOnClickListener(this);
        add = (TextView) findViewById(R.id.add);
        add.setOnClickListener(this);
        food_type_spinner = (Spinner) findViewById(R.id.food_type_spinner);

        item_name = (EditText) findViewById(R.id.store_name);
        item_quantity = (EditText) findViewById(R.id.item_quantity);
        item_desc = (EditText) findViewById(R.id.store_desc);
        item_price = (EditText) findViewById(R.id.store_price);

        user = AuthHelper.getLoggedUser(this);
        for (Cats cat : Global.CATS) {
            cats.add(cat.getName(this));
        }
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(cats));
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        food_type_spinner.setAdapter(dataAdapter);
        food_type_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.store_icon) {
            View customView = inflater.inflate(R.layout.image_pick_view, null);
            ImageView gallery = (ImageView) customView.findViewById(R.id.gallery);
            gallery.setOnClickListener(this);
            ImageView camera = (ImageView) customView.findViewById(R.id.cam);
            camera.setOnClickListener(this);

            dialog = new BottomDialog.Builder(this)
                    .setTitle("choose picture action")
                    .setCustomView(customView).show();

        } else if (v.getId() == R.id.add) {
            String name = item_name.getText().toString();
            String desc = item_desc.getText().toString();
            int quantity = Integer.parseInt(item_quantity.getText().toString());
            float price = Float.parseFloat(item_price.getText().toString().isEmpty() ? "0" : item_price.getText().toString());
            int type = Global.CATS.get(food_type_spinner.getSelectedItemPosition()).getId();
            name = name.replaceAll("[\r\n]+", "\n");
            desc = desc.replaceAll("[\r\n]+", "\n");
            addFood(uri, quantity, name, desc, price, type);
            //addFood(name, desc, price);
        } else if (v.getId() == R.id.gallery) {
            if (dialog != null) dialog.dismiss();
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, PICK_FROM_GALLERY_CODE);
        } else if (v.getId() == R.id.cam) {
            if (dialog != null) dialog.dismiss();
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, PICK_FROM_CAMERA_CODE);
        }
    }

    private void addFood(final Uri uri, final int quantity, final String name, final String desc, final float price, final int type) {
        final String time = System.currentTimeMillis() + "";
        if (uri != null) {
            //RequestBody descReqBody = RequestBody.create(MultipartBody.FORM, "file name");
            String filePath = Images.getPathFromUri(this, uri);
            File orginalFile = new File(filePath);
            //RequestBody filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), orginalFile);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), orginalFile);
            final MultipartBody.Part body = MultipartBody.Part.createFormData("file", orginalFile.getName(), requestFile);
            // MultipartBody.Part file = MultipartBody.Part.createFormData("photo", orginalFile.getName(), requestFile);

            AuthHelper.startDialog(this);
            AuthInternet.action(this, new AuthNetworking() {
                @Override
                public void accessInternet(boolean isConnected) {
                    if (isConnected) {

                        Call<Food> call = RestHelper.API_SERVICE.uploadFile(body,
                                RequestBody.create(MediaType.parse("text/plain"), time),
                                RequestBody.create(MediaType.parse("text/plain"), quantity + ""),
                                RequestBody.create(MediaType.parse("text/plain"), name),
                                RequestBody.create(MediaType.parse("text/plain"), desc),
                                RequestBody.create(MediaType.parse("text/plain"), price + ""),
                                RequestBody.create(MediaType.parse("text/plain"), AuthHelper.getLoggedUser(AddFood.this).getId() + ""),
                                RequestBody.create(MediaType.parse("text/plain"), type + "")
                        );
                        call.enqueue(new Callback<Food>() {
                            @Override
                            public void onResponse(Call<Food> call, Response<Food> response) {
                                AuthHelper.stopDialog();
                                if (response.isSuccessful()) {
                                    uploadDiseases(response.body());
                                } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<Food> call, Throwable t) {
                                AuthHelper.stopDialog();
                                t.printStackTrace();
                                Toast.makeText(getApplicationContext(), "onFailure:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        AuthHelper.stopDialog();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "plz select image ", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadDiseases(Food food) {
        ids.clear();
        for (int x = 0; x < wrapper.getChildCount(); x++) {
            CheckBox cb = (CheckBox) wrapper.getChildAt(x);
            int id = (int) cb.getTag();
            if (cb.isChecked())
                ids.add(id);
        }

        //send values
        if (ids.size() > 0) {
            AuthHelper.startDialog(this);
            Call<String> call = RestHelper.API_SERVICE.sendFoodDiseases(ids, food.id);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    AuthHelper.stopDialog();
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "food added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "food add error:" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "food added", Toast.LENGTH_SHORT).show();
        }
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && (requestCode == PICK_FROM_GALLERY_CODE || requestCode == PICK_FROM_CAMERA_CODE)) {
            uri = data.getData();

            String filePath = Images.getPathFromUri(this, uri);

            Bitmap b = Images.bitmapFromPath(filePath);
            if (BitmapCompat.getAllocationByteCount(b) <= 1000000) {
                icon.setImageDrawable(Drawable.createFromPath(filePath));
            } else {
                Log.e(TAG, "size before" + BitmapCompat.getAllocationByteCount(b));
                //Bitmap bm = Images.scaleDown(b, 1000000, true);
                Bitmap bm = Bitmap.createScaledBitmap(b, 612, 816, true);
                Log.e(TAG, "size after" + BitmapCompat.getAllocationByteCount(bm));
                Images.saveBmpToLocalDev(bm, Paths.imagesDir + "temp.png", new com.cafateria.helper.Callback() {
                    @Override
                    public void onComplete(boolean b) {
                        String path = Paths.imagesDir + "temp.png";
                        uri = Uri.fromFile(new File(path));
                        icon.setImageDrawable(Drawable.createFromPath(path));
                    }
                });
            }
            //Toast.makeText(this, "size:" + BitmapCompat.getAllocationByteCount(b), Toast.LENGTH_SHORT).show();
            //Log.e(TAG, "size:" + BitmapCompat.getAllocationByteCount(b));

        }/* else if (resultCode == RESULT_OK && requestCode == PICK_FROM_CAMERA_CODE) {
            uri = data.getData();
            String filePath = Images.getPathFromUri(this, uri);
            icon.setImageDrawable(Drawable.createFromPath(filePath));
        }*/ else if (resultCode == RESULT_OK && requestCode == 100) {
            System.out.println("back from cropping");
            Bitmap bm = (Bitmap) data.getExtras().get("img");
            isImageChanged = true;
            icon.setImageBitmap(bm);
        }
    }
}
