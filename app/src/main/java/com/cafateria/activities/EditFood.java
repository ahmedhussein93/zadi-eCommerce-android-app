package com.cafateria.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.handler.Images;
import com.cafateria.handler.Paths;
import com.cafateria.helper.Global;
import com.cafateria.model.Food;
import com.cafateria.rest.ApiClient;
import com.cafateria.rest.RestHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditFood extends AppCompatActivity implements View.OnClickListener {

    ImageView icon;
    TextView save;
    EditText cat_name, item_name, item_desc, item_quantity, item_price;
    Food food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);
        if (Global.FOOD_TO_SHOW_DETAILS == null) {
            finish();
            return;
        }
        food = Global.FOOD_TO_SHOW_DETAILS;
        init();
    }

    private void init() {
        icon = (ImageView) findViewById(R.id.store_icon);
        Images.set(this, icon, ApiClient.BASE_UPLOADS_URL + food.file_name, Paths.imagesDir + food.file_name);

        save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(this);

        cat_name = (EditText) findViewById(R.id.cat_name);
        cat_name.setText(Global.getCatName(this, food.food_type));
        item_name = (EditText) findViewById(R.id.store_name);
        item_name.setText(food.name);
        item_quantity = (EditText) findViewById(R.id.item_quantity);
        item_quantity.setText(food.quantity + "");
        item_desc = (EditText) findViewById(R.id.store_desc);
        item_desc.setText(food.description);
        item_price = (EditText) findViewById(R.id.store_price);
        item_price.setText(food.price + "");

    }

    @Override
    public void onClick(View view) {
        AuthHelper.startDialog(this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<String> call = RestHelper.API_SERVICE.updateFood(food.id, item_name.getText().toString(), Integer.parseInt(item_quantity.getText().toString()), item_desc.getText().toString(), Float.parseFloat(item_price.getText().toString()));
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.item_updated), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            t.printStackTrace();

                        }
                    });
                } else {
                    AuthHelper.stopDialog();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
