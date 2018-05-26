package com.cafateria.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.cafateria.R;
import com.cafateria.adapters.FoodAdapter;
import com.cafateria.auth.AuthHelper;
import com.cafateria.helper.Global;
import com.cafateria.model.Food;
import com.cafateria.model.SectionedRecycleViewModel;
import com.cafateria.rest.RestHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestSectionedRecycleView extends AppCompatActivity {

    List<Food> items = new ArrayList<>();
    LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sectioned_recyle_view);
        root = (LinearLayout) findViewById(R.id.root);
        //inflate
        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(this).inflate(R.layout.recycle_view, null);
        items.clear();
        FoodAdapter mAdapter = new FoodAdapter(this, items);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));//devedor
        recyclerView.setAdapter(mAdapter);
        root.addView(recyclerView);

        getData();
    }

    private void getData() {
        AuthHelper.startDialog(this);
        Call<List<SectionedRecycleViewModel>> call = RestHelper.API_SERVICE.getFood(0, 0);
        call.enqueue(new Callback<List<SectionedRecycleViewModel>>() {
            @Override
            public void onResponse(Call<List<SectionedRecycleViewModel>> call, Response<List<SectionedRecycleViewModel>> response) {
                AuthHelper.stopDialog();
                if (response.isSuccessful()) {
                    //SectionedRecycleViewHelper.setSections(recyclerView, response.body());
                    /*foods1.addAll(response.body());
                    foods2.addAll(response.body());
                    mAdapter1.notifyDataSetChanged();
                    mAdapter2.notifyDataSetChanged();*/
                }
            }

            @Override
            public void onFailure(Call<List<SectionedRecycleViewModel>> call, Throwable t) {

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
