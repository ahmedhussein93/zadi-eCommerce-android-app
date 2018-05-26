package com.cafateria.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.adapters.CartAdapter;
import com.cafateria.auth.AuthUsers;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;


public class Cart extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RECENT_ADDED";
    private AuthUsers user;
    private RecyclerView recyclerView;
    private CartAdapter mAdapter;
    private TextView back, buy;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        AppSettings.setLang(this, AppSettings.getLang(this));
        showCart();

        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        buy = (TextView) findViewById(R.id.check_out);
        buy.setOnClickListener(this);
    }

    void showCart() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new CartAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));//devedor
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        } else if (R.id.check_out == view.getId()) {
            startActivity(new Intent(this, CompleteOrderInfo.class));
        }
    }

    public void setTotal(String _total) {
        ((TextView) findViewById(R.id.total)).setText(_total);
    }
}
