package com.cafateria.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.adapters.FoodAdapter;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.auth.AuthUsers;
import com.cafateria.database.PrefManager;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.helper.Init;
import com.cafateria.model.Food;
import com.cafateria.model.Sections;
import com.cafateria.rate.RateUs;
import com.cafateria.rest.RestHelper;
import com.cafateria.services.AdminService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {

    private static final String TAG = "HOME";
    //private static List<Food> list = new ArrayList<>();
    //private static FoodAdapter mAdapter;
    public static Home mThis;
   // private static int cat_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mThis = this;
        AppSettings.setLang(this, AppSettings.getLang(this));
        if ((getIntent().getBooleanExtra("EXIT", false)) || (AuthHelper.getLoggedUser(this) == null)) {
            finish();
            return;
        }
        AuthUsers user = AuthHelper.getLoggedUser(this);
        Init.initDrawer(this, R.id.nav_home);//.initDrawer(this, R.id.nav_home);
        getSupportActionBar().setTitle(getResources().getString(R.string.home));

        if (user.getUser_type() == AuthUsers.ADMIN && !Global.isServiceRunning(this, AdminService.class)) {//
            //AdminHelper.checkLessQuantity(this);
            Log.e(TAG, "start service from home");
            startService(new Intent(this, AdminService.class));
        } else {
            Log.e(TAG, "service is running or not admin  from home admin?:" + (user.getUser_type() == AuthUsers.ADMIN));
        }

        //stop service is running on not admin device (admin was logged before)
        if (user.getUser_type() != AuthUsers.ADMIN && Global.isServiceRunning(this, AdminService.class))
            stopService(new Intent(this, AdminService.class));

        //rate app if not admin
        if (AuthUsers.ADMIN != user.getUser_type()) {
            RateUs.app_launched(this);
        }

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (user.getUser_type() == AuthUsers.ADMIN)
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.tap_view_cart, Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.show_cart), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Global.CART.size() < 1) {
                            Toast.makeText(getApplicationContext(), R.string.no_items_yet, Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(getApplicationContext(), Cart.class));
                        }
                    }
                }).show();
            }
        });*/

      /*  RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        list.clear();
        mAdapter = new FoodAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));//devedor
        recyclerView.setAdapter(mAdapter);

        cat_id = PrefManager.getCurrentCatId(this);
        if (cat_id == -1) {
            if (user.getUser_type() == AuthUsers.ADMIN)
                startActivity(new Intent(this, ManageCats.class));
            else
                startActivity(new Intent(this, Categories.class));

            finish();
            return;
        }

        Global.LAST_ID = 0;
        list.add(new Food(0, 0, 0, "more", null, null, 0, null, cat_id, null));
        getItemsByCatId();
*/
    }

    public static void getItemsByCatId() {
       /* AuthInternet.action(mThis, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {
                    mThis.getSupportActionBar().setTitle(Global.getCatName(mThis, cat_id));
                    AuthHelper.startDialog(mThis);
                    Call<Sections> call = RestHelper.API_SERVICE.getItemsById(cat_id, Global.LAST_ID);
                    call.enqueue(new Callback<Sections>() {
                        @Override
                        public void onResponse(Call<Sections> call, Response<Sections> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                list.addAll(list.size() - 1, response.body().items);
                                Global.LAST_ID = response.body().last_id;

                                if (response.body().isLast) {
                                    //hide show more
                                    list.remove(list.size() - 1);//remove last index(show more)
                                }

                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<Sections> call, Throwable t) {
                            AuthHelper.stopDialog();
                            t.printStackTrace();
                        }
                    });
                } else {
                    AuthHelper.stopDialog();
                    Toast.makeText(mThis, mThis.getResources().getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.agin_to_exist), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
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
