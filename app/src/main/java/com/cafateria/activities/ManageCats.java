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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.adapters.MngCatsAdapter;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.helper.Init;
import com.cafateria.model.Cats;
import com.cafateria.rest.RestHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCats extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MANGE_CATS";
    private RecyclerView recyclerView;
    private MngCatsAdapter mAdapter;
    ImageView add;
    EditText ar, en;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cats);
        Init.initDrawer(this, R.id.nav_manage_cats);
        AppSettings.setLang(this, AppSettings.getLang(this));
        getSupportActionBar().setTitle(getResources().getString(R.string.manage_cats));

        if (AuthHelper.getLoggedUser(this) == null)
            return;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        ar = (EditText) findViewById(R.id.ar);
        en = (EditText) findViewById(R.id.en);
        mAdapter = new MngCatsAdapter(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));//devedor
        recyclerView.setAdapter(mAdapter);

        add = (ImageView) findViewById(R.id.add);
        add.setOnClickListener(this);

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
    public void onClick(View view) {
        final String _ar = ar.getText().toString();
        final String _en = en.getText().toString();
        AuthHelper.startDialog(this);
        AuthInternet.action(this, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {

                    Call<List<Cats>> call = RestHelper.API_SERVICE.addCat(_ar, _en);
                    call.enqueue(new Callback<List<Cats>>() {
                        @Override
                        public void onResponse(Call<List<Cats>> call, Response<List<Cats>> response) {
                            AuthHelper.stopDialog();
                            if (response.isSuccessful()) {
                                Global.CATS.clear();
                                Global.CATS.addAll(response.body());
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, response.message());
                                Log.e(TAG, response.code() + "");
                                Log.e(TAG, response.toString());
                            }

                        }

                        @Override
                        public void onFailure(Call<List<Cats>> call, Throwable t) {
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
