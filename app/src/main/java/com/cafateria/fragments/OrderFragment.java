package com.cafateria.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.auth.AuthUsers;

/**
 * Created by pc on 24/03/2018.
 */

public class OrderFragment extends Fragment {

    private static final String TAG = "SHOW_ORDER_DETAILS";
    TextView name, phone, address;
    TextView total, submit, time, order_number;
    LinearLayout wrapper;
    TextView delivery;
    AuthUsers user;
    int order_id = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_order_details, container, false);
        return view;
    }
}
