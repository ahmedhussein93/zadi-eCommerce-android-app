package com.cafateria.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cafateria.R;
import com.cafateria.adapters.CatsAdapter;
import com.cafateria.auth.AuthHelper;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Callback;
import com.cafateria.helper.Global;
import com.cafateria.helper.Init;
import com.cafateria.model.Cats;

public class Categories extends AppCompatActivity {

    private static final String TAG = "CATEGORIES";
    private RecyclerView recyclerView;
    private CatsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        AppSettings.setLang(this, AppSettings.getLang(this));
        Init.initDrawer(this, R.id.nav_cats);
        getSupportActionBar().setTitle(getResources().getString(R.string.cats));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        mAdapter = new CatsAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));//devedor
        recyclerView.setAdapter(mAdapter);

        if (Global.CATS.size() < 1) {
            AuthHelper.startDialog(this);
            Cats.updateCats(new Callback() {
                @Override
                public void onComplete(boolean b) {
                    AuthHelper.stopDialog();
                }
            });
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
