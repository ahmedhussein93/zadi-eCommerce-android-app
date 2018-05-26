package com.cafateria.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.activities.LauncherApp;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.database.PrefManager;
import com.cafateria.fragments.HomeFragment;
import com.cafateria.fragments.ManageCatsFragment;
import com.cafateria.helper.Global;
import com.cafateria.model.Cats;
import com.cafateria.rest.RestHelper;
import com.github.javiersantos.bottomdialogs.BottomDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MngCatsAdapter extends RecyclerView.Adapter<MngCatsAdapter.MyViewHolder> implements View.OnClickListener {

    private Context context;

    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.root) {
            int cat_id = (int) view.getTag();
            PrefManager.setCurrentCatId(context, cat_id);
            HomeFragment homeFragment = (HomeFragment) ((Activity) context).getFragmentManager().findFragmentById(R.id.home);
            if (homeFragment != null)
                homeFragment.update();
            else
                LauncherApp.start((Activity) context);
        } else {
            AuthInternet.action(context, new AuthNetworking() {
                @Override
                public void accessInternet(boolean isConnected) {
                    if (isConnected) {
                        if (view.getId() == R.id.delete) {
                            String id = view.getTag().toString();
                            showDialogDelete(id);
                        }
                    }
                }
            });
        }
    }

    private void showDialogDelete(final String id) {

        //show dialoge ->
        new BottomDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.alert))
                .setContent(context.getResources().getString(R.string.conf_del_cat))
                .setPositiveText(context.getString(R.string.delete))
                .setNegativeText(context.getString(R.string.cancel))
                .setNegativeTextColorResource(android.R.color.holo_green_light)
                .setPositiveBackgroundColor(android.R.color.white)
                .setPositiveTextColorResource(android.R.color.holo_red_dark)
                .onPositive(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(BottomDialog dialog) {
                        AuthHelper.startDialog(context);
                        AuthInternet.action(context, new AuthNetworking() {
                            @Override
                            public void accessInternet(boolean isConnected) {
                                if (isConnected) {

                                    Call<List<Cats>> call = RestHelper.API_SERVICE.delCat(id);
                                    call.enqueue(new Callback<List<Cats>>() {
                                        @Override
                                        public void onResponse(Call<List<Cats>> call, Response<List<Cats>> response) {
                                            AuthHelper.stopDialog();
                                            if (response.isSuccessful()) {
                                                Global.CATS.clear();
                                                Global.CATS.addAll(response.body());
                                                notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<List<Cats>> call, Throwable t) {
                                            t.printStackTrace();
                                        }
                                    });
                                } else {
                                    AuthHelper.stopDialog();
                                    Toast.makeText(context, context.getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).show();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ar, en, count_items;
        public ImageView delete;
        private LinearLayout root;

        public MyViewHolder(View view) {
            super(view);
            root = (LinearLayout) view.findViewById(R.id.root);
            ar = (TextView) view.findViewById(R.id.ar);
            en = (TextView) view.findViewById(R.id.en);
            count_items = (TextView) view.findViewById(R.id.count_items);
            delete = (ImageView) view.findViewById(R.id.delete);
        }
    }

    public MngCatsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mng_cat_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Cats cat = Global.CATS.get(position);
        holder.root.setTag(cat.getId());
        holder.root.setOnClickListener(this);
        holder.ar.setText(cat.getName_ar());
        holder.en.setText(cat.getName_en());
        holder.delete.setOnClickListener(this);
        holder.delete.setTag(cat.getId());
        holder.count_items.setText(cat.num_of_items);
    }

    @Override
    public int getItemCount() {
        return Global.CATS.size();
    }
}