package com.cafateria.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.adapters.FoodAdapter;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.auth.AuthUsers;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.helper.Init;
import com.cafateria.model.Food;
import com.cafateria.rest.RestHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity {

    private static final String TAG = "SEARCH";
    private AuthUsers user;
    private RecyclerView recyclerView;
    private List<Food> itemsList = new ArrayList<>();
    private FoodAdapter mAdapter;
    private List<String> names = new ArrayList<>();
    private List<Integer> values = new ArrayList<>();
    private Spinner food_type_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        AppSettings.setLang(this, AppSettings.getLang(this));
        if (AuthHelper.getLoggedUser(this) == null)
            return;

        Init.initDrawer(this, R.id.nav_search);
        getSupportActionBar().setTitle(getResources().getString(R.string.search));
        user = AuthHelper.getLoggedUser(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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


        showSearchView();
    }

    private void showSearchView() {
        final Dialog dialog = new Dialog(this);
        final View view = View.inflate(this, R.layout.search_view, null);

        names.add("");
        values.add(-1);
        names.add(getResources().getString(R.string.foods));
        values.add(Food.TYPE_FOOD);
        names.add(getResources().getString(R.string.drinks));
        values.add(Food.TYPE_DRINK);
        food_type_spinner = view.findViewById(R.id.spinner);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        food_type_spinner.setAdapter(dataAdapter);


        final EditText name = view.findViewById(R.id.item_name);
        final EditText desc = view.findViewById(R.id.item_desc);
        final EditText from = view.findViewById(R.id.price_from);
        final EditText to = view.findViewById(R.id.price_to);
        AppCompatButton search = (AppCompatButton) view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AuthHelper.startDialog(Search.this);
                AuthInternet.action(Search.this, new AuthNetworking() {
                    @Override
                    public void accessInternet(boolean isConnected) {
                        if (isConnected) {

                            Call<List<Food>> call = RestHelper.API_SERVICE.search(
                                    name.getText().toString(),
                                    desc.getText().toString(),
                                    from.getText().toString(),
                                    to.getText().toString(),
                                    values.get(food_type_spinner.getSelectedItemPosition()));
                            call.enqueue(new Callback<List<Food>>() {
                                @Override
                                public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                                    AuthHelper.stopDialog();
                                    if (response.isSuccessful()) {
                                        itemsList.addAll(response.body());
                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<List<Food>> call, Throwable t) {

                                }
                            });
                        } else {
                            AuthHelper.stopDialog();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });


        dialog.setContentView(view);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = AuthHelper.getScreenWdith(this) - 10;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    /*private void createSearchQuery(final String name, final String desc, final String from, final String to, final int id) {
        Log.e(TAG, "name:" + name + "\ndesc:" + desc + "\nfrom:" + from + "\nto:" + to + "\ncat_id:" + id);

    }*/


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
        mAdapter.notifyDataSetChanged();
        Global.CURRENT_ACTIVITY = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        Global.CURRENT_ACTIVITY = null;
        super.onPause();
    }
}
