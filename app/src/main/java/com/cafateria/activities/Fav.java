package com.cafateria.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.adapters.FoodAdapter;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthUsers;
import com.cafateria.database.PrefManager;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.helper.Init;
import com.cafateria.model.Food;

import java.util.ArrayList;
import java.util.List;

public class Fav extends AppCompatActivity {

    private static final String TAG = "FAV";
    private AuthUsers user;
    private RecyclerView recyclerView;
    private List<Food> itemsList = new ArrayList<>();
    private FoodAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        AppSettings.setLang(this, AppSettings.getLang(this));
        if (AuthHelper.getLoggedUser(this) == null)
            return;

        Init.initDrawer(this, R.id.nav_fav);//.initDrawer(this, R.id.nav_home);
        getSupportActionBar().setTitle(getResources().getString(R.string.fav));
        user = AuthHelper.getLoggedUser(this);
        itemsList.clear();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        itemsList.addAll(PrefManager.getFavList(this));
        mAdapter = new FoodAdapter(this, itemsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));//devedor
        recyclerView.setAdapter(mAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
