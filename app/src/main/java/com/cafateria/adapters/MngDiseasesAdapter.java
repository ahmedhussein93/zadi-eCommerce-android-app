package com.cafateria.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.model.Disease;
import com.cafateria.rest.RestHelper;
import com.github.javiersantos.bottomdialogs.BottomDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MngDiseasesAdapter extends RecyclerView.Adapter<MngDiseasesAdapter.MyViewHolder> implements View.OnClickListener {

    private Context context;
    List<Disease> list;

    @Override
    public void onClick(final View view) {

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

    private void showDialogDelete(final String id) {

        //show dialoge ->
        new BottomDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.alert))
                .setContent(context.getResources().getString(R.string.conf_del_disease))
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


                                    Call<List<Disease>> call = RestHelper.API_SERVICE.delDisease(id);
                                    call.enqueue(new Callback<List<Disease>>() {
                                        @Override
                                        public void onResponse(Call<List<Disease>> call, Response<List<Disease>> response) {
                                            AuthHelper.stopDialog();
                                            if (response.isSuccessful()) {
                                                list.clear();
                                                list.addAll(response.body());
                                                notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<List<Disease>> call, Throwable t) {
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
        public TextView ar, en;
        public ImageView delete;

        public MyViewHolder(View view) {
            super(view);
            ar = (TextView) view.findViewById(R.id.ar);
            en = (TextView) view.findViewById(R.id.en);
            delete = (ImageView) view.findViewById(R.id.delete);
        }
    }

    public MngDiseasesAdapter(Context context, List<Disease> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mng_dis_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Disease disease = list.get(position);
        holder.ar.setText(disease.name);
        holder.en.setText(disease.name_en);
        holder.delete.setOnClickListener(this);
        holder.delete.setTag(disease.id);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}