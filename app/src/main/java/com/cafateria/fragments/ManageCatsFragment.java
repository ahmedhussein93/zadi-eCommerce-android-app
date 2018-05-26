package com.cafateria.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.adapters.MngCatsAdapter;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.helper.Global;
import com.cafateria.model.Cats;
import com.cafateria.rest.RestHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pc on 24/03/2018.
 */

public class ManageCatsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ManageCatsFragment";
    private RecyclerView recyclerView;
    private MngCatsAdapter mAdapter;
    ImageView add;
    EditText ar, en;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_cats, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        ar = (EditText) view.findViewById(R.id.ar);
        en = (EditText) view.findViewById(R.id.en);
        mAdapter = new MngCatsAdapter(getActivity());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));//devedor
        recyclerView.setAdapter(mAdapter);

        add = (ImageView) view.findViewById(R.id.add);
        add.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        final String _ar = ar.getText().toString();
        final String _en = en.getText().toString();
        AuthHelper.startDialog(getActivity());
        AuthInternet.action(getActivity(), new AuthNetworking() {
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
                                Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), getResources().getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void update() {

    }
}
