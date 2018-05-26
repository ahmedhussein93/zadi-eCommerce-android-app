package com.cafateria.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.adapters.OrderAdapter;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.auth.AuthUsers;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.helper.Init;
import com.cafateria.model.Order;
import com.cafateria.rest.RestHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowOrdrs extends AppCompatActivity {

   /* private static final String TAG = "SHOW_ORDERS";
    private AuthUsers user;
    private RecyclerView recyclerView;
    private OrderAdapter mAdapter;
    private List<Order> orders = new ArrayList<>();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ordrs);

        Init.initDrawer(this, R.id.nav_show_orders);
        AppSettings.setLang(this, AppSettings.getLang(this));
        getSupportActionBar().setTitle(getResources().getString(R.string.show_orders));

       /* user = AuthHelper.getLoggedUser(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new OrderAdapter(this, orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));//devedor
        recyclerView.setAdapter(mAdapter);*/
        //setOrders();
    }

    /*private void setOrders() {
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<List<Order>> call = RestHelper.API_SERVICE.getOrders();
                    call.enqueue(new Callback<List<Order>>() {
                        @Override
                        public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                orders.addAll(response.body());
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, response.message());
                                Log.e(TAG, response.code() + "");
                                Log.e(TAG, response.toString());
                            }

                        }

                        @Override
                        public void onFailure(Call<List<Order>> call, Throwable t) {

                        }
                    });
                } else {
                    AuthHelper.stopDialog();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/

    boolean doubleBackToExitPressedOnce = false;

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
