package com.cafateria.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.activities.Categories;
import com.cafateria.activities.Home;
import com.cafateria.activities.ManageCats;
import com.cafateria.adapters.FoodAdapter;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.auth.AuthUsers;
import com.cafateria.database.PrefManager;
import com.cafateria.helper.Global;
import com.cafateria.model.Food;
import com.cafateria.model.Sections;
import com.cafateria.rest.RestHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pc on 24/03/2018.
 */

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static List<Food> list = new ArrayList<>();
    private static FoodAdapter mAdapter;
    private static int cat_id = 0;
    private static Activity mThis;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mThis = getActivity();
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_home, container, false);
        list.clear();
        mAdapter = new FoodAdapter(getActivity(), list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));//devedor
        recyclerView.setAdapter(mAdapter);

        AuthUsers user = AuthHelper.getLoggedUser(getActivity());
        if (cat_id == -1) {
            if (user.getUser_type() == AuthUsers.ADMIN)
                startActivity(new Intent(getActivity(), ManageCats.class));
            else
                startActivity(new Intent(getActivity(), Categories.class));
        }

        update();
        return recyclerView;
    }

    public static void getItemsByCatId() {
        AuthInternet.action(mThis, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {
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
        });
    }

    public void update() {
        //Toast.makeText(getActivity(), "home:"+, Toast.LENGTH_LONG).show();
        cat_id = PrefManager.getCurrentCatId(getActivity());
        list.clear();
        Global.LAST_ID = 0;
        list.add(new Food(0, 0, 0, "more", null, null, 0, null, cat_id, null));
        getItemsByCatId();
    }
}
