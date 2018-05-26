package com.cafateria.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.adapters.FoodAdapter;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Init;
import com.cafateria.model.Food;
import com.cafateria.rest.RestHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessQuantity extends AppCompatActivity {

    private static final String TAG = "LessQuantity";
    private static List<Food> list = new ArrayList<>();
    private static FoodAdapter mAdapter;
    public static LessQuantity mThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_less_quantity);

        mThis = this;
        AppSettings.setLang(this, AppSettings.getLang(this));

        Init.initDrawer(this, R.id.nav_less_quantity);//.initDrawer(this, R.id.nav_home);
        getSupportActionBar().setTitle(getResources().getString(R.string.less_quantity));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        list.clear();
        mAdapter = new FoodAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));//devedor
        recyclerView.setAdapter(mAdapter);

        getData(10);
    }

    private void getData(final int q) {
        AuthHelper.startDialog(mThis);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<List<Food>> call = RestHelper.API_SERVICE.getByQuantity(q);
                    call.enqueue(new Callback<List<Food>>() {
                        @Override
                        public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                list.clear();
                                list.addAll(response.body());
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Food>> call, Throwable t) {
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            LauncherApp.start(this);
        }
    }

    public void getLess(View view) {
        String quantity = ((EditText) findViewById(R.id.quantity)).getText().toString();
        if (quantity.isEmpty()) {
            //Toast.makeText(this,"enter valid quantity",Toast.LENGTH_SHORT).show();
        } else {
            getData(Integer.parseInt(quantity));
        }
    }
}
