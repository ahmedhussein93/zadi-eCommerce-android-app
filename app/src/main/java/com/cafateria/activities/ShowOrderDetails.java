package com.cafateria.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cafateria.R;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;

public class ShowOrderDetails extends AppCompatActivity implements View.OnClickListener {

  /*  private static final String TAG = "SHOW_ORDER_DETAILS";
    TextView name, phone, address;
    TextView total, submit, time, order_number;
    LinearLayout wrapper;
    TextView delivery;
    AuthUsers user;
    int order_id = 0;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order_details);
        AppSettings.setLang(this, AppSettings.getLang(this));
       /* name = (TextView) findViewById(R.id.item__name);
        delivery = (TextView) findViewById(R.id.delivery);
        order_number = (TextView) findViewById(R.id.order_number);
        user = AuthHelper.getLoggedUser(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null || !bundle.containsKey("order_id")) {
            Toast.makeText(getApplicationContext(), "order id not exist", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        order_id = bundle.getInt("order_id", 0);
        if (order_id == 0) {
            Toast.makeText(getApplicationContext(), "order id not valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AuthHelper.startDialog(this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {
                    Call<Order> call = RestHelper.API_SERVICE.getOrder(order_id);
                    call.enqueue(new Callback<Order>() {
                        @Override
                        public void onResponse(Call<Order> call, Response<Order> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                setinfo(response.body());
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, response.message());
                                Log.e(TAG, response.code() + "");
                                Log.e(TAG, response.toString());
                            }

                        }

                        @Override
                        public void onFailure(Call<Order> call, Throwable t) {
                            AuthHelper.stopDialog();
                            t.printStackTrace();
                        }
                    });
                } else {
                    AuthHelper.stopDialog();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

  /*  private void setinfo(Order o) {
        if (o.foods == null) {
            finish();
            return;
        }
        //System.out.println(new Gson().toJson(o, Order.class).toString());
        delivery.setText(getResources().getString(o.isDelivery == 1 ? R.string.yes : R.string.no));
        name.setText(o.name);
        order_number.setText(o.id + "");
        phone = (TextView) findViewById(R.id.phone);
        phone.setText(o.phone);
        address = (TextView) findViewById(R.id.address);
        address.setText(o.address);
        time = (TextView) findViewById(R.id.time);
        time.setText(DateFormat.format("dd-MM-yyyy hh:mm", Long.parseLong(o.date)).toString());
        total = (TextView) findViewById(R.id.price_total);

        wrapper = (LinearLayout) findViewById(R.id.wrapper);
        float _total = 0;
        for (Food food : o.foods) {
            _total += food.price * food.count;

            View view = LayoutInflater.from(this).inflate(R.layout.invoice_item, null);
            ((TextView) view.findViewById(R.id.item_name)).setText(food.name);
            ((TextView) view.findViewById(R.id.item_quantity)).setText(food.count + "");
            ((TextView) view.findViewById(R.id.item_price)).setText((food.price * food.count) + "");
            wrapper.addView(view);
        }
        total.setText(_total + " " + getResources().getString(R.string.r));

        submit = (TextView) findViewById(R.id.submit);
        submit.setEnabled(o.delivered != 1);
        submit.setText(getResources().getString(R.string.delivered));
        submit.setOnClickListener(this);
    }*/

    @Override
    public void onClick(View view) {
       /* AuthHelper.startDialog(this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<String> call = RestHelper.API_SERVICE.setOrderAsDelivered(order_id);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            AuthHelper.stopDialog();
                            String result = response.body();
                            System.out.println(result);
                            if (response.isSuccessful()) {
                                if (result != null && !result.equalsIgnoreCase("error")) {
                                    submit.setEnabled(false);
                                    Toast.makeText(getApplicationContext(), "order checked as delivered", Toast.LENGTH_SHORT).show();
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
        });*/
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