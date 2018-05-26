package com.cafateria.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.auth.AuthUsers;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.model.Food;
import com.cafateria.model.ItemIdCount;
import com.cafateria.model.Order;
import com.cafateria.rest.RestHelper;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteOrderInfo extends AppCompatActivity implements View.OnClickListener {

    EditText name, phone, address;
    TextView total, submit, time;
    LinearLayout wrapper;
    AuthUsers user;
    CheckBox delivery;
    LinearLayout deliveryWapper;
    private long selected_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order_info);
        AppSettings.setLang(this, AppSettings.getLang(this));
        user = AuthHelper.getLoggedUser(this);
        name = (EditText) findViewById(R.id.item__name);
        name.setText(user.getFirst_name() + " " + user.getLast_name());
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);

        delivery = (CheckBox) findViewById(R.id.delivery);
        deliveryWapper = (LinearLayout) findViewById(R.id.deliveryWapper);
        if (user.getUser_type() != AuthUsers.STUFF)
            deliveryWapper.setVisibility(View.GONE);

        time = (TextView) findViewById(R.id.time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd hh:mm a", Locale.ENGLISH);
        Date a = new Date();
        a.setTime(System.currentTimeMillis() + (60 * 60 * 1000));//date plus one hour
        String dateString = formatter.format(a);
        selected_time = a.getTime();
        time.setText(dateString);


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), DatePickerTimePickerActivity.class), 100);
            }
        });
        total = (TextView) findViewById(R.id.price_total);
        float _total = 0;
        for (Food i : Global.CART) {
            _total += i.price * i.count;
        }
        total.setText(_total + " " + getResources().getString(R.string.r));

        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(this);
        wrapper = (LinearLayout) findViewById(R.id.wrapper);
        for (Food food : Global.CART) {
            View view = LayoutInflater.from(this).inflate(R.layout.invoice_item, null);
            ((TextView) view.findViewById(R.id.item_name)).setText(food.name);
            ((TextView) view.findViewById(R.id.item_quantity)).setText(food.count + "");
            ((TextView) view.findViewById(R.id.item_price)).setText((food.price * food.count) + "");
            wrapper.addView(view);
        }

    }

    @Override
    public void onClick(View view) {
        Order order = new Order();

        order.user_id = user.getId();
        order.name = name.getText().toString();
        order.phone = phone.getText().toString();
        order.address = address.getText().toString();
        order.date = selected_time + "";
        order.req_date = System.currentTimeMillis() + "";
        order.isDelivery = delivery.isChecked() ? 1 : 0;

        order.items = new ArrayList<>();
        for (Food i : Global.CART) {
            order.items.add(new ItemIdCount(i.id, i.count));
        }

        final String order_json_string = new Gson().toJson(order, Order.class);
        System.out.println(order_json_string);
        AuthHelper.startDialog(this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {


                    Call<String> call = RestHelper.API_SERVICE.order(order_json_string);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            AuthHelper.stopDialog();
                            String result = response.body();
                            System.out.println(result);
                            if (response.isSuccessful()) {
                                if (result != null && !result.equalsIgnoreCase("error")) {
                                    //Toast.makeText(getApplicationContext(), "order submitted successfully", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(CompleteOrderInfo.this, OrderDone.class);
                                    i.putExtra("order_id", result);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(getApplicationContext(), "error while submitting your order!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            AuthHelper.stopDialog();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                selected_time = bundle.getLong("time");
                System.out.println("on back :selected_time ->" + selected_time);
                int year = bundle.getInt("year");
                int month = bundle.getInt("month");
                int day = bundle.getInt("day");
                int hour = bundle.getInt("hour");
                int minute = bundle.getInt("minute");

                StringBuffer strBuffer = new StringBuffer();
                strBuffer.append(year);
                strBuffer.append("-");
                strBuffer.append(month);
                strBuffer.append("-");
                strBuffer.append(day);
                strBuffer.append(" ");
                strBuffer.append(hour);
                strBuffer.append(":");
                strBuffer.append(minute);
                time.setText(strBuffer);
                //Toast.makeText(this, "result:" + strBuffer, Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
