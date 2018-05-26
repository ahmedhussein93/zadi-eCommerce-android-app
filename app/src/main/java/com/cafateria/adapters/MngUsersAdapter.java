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
import com.cafateria.auth.AuthUsers;
import com.cafateria.rest.RestHelper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MngUsersAdapter extends RecyclerView.Adapter<MngUsersAdapter.MyViewHolder> implements View.OnClickListener {

    private static final String TAG = "MNG_USERS_ADAPTER";
    private final SimpleDateFormat dateFormat;
    private List<AuthUsers> itemsList;
    private Context context;
    private RecyclerView mRecyclerView;

    @Override
    public void onClick(final View view) {
        AuthInternet.action(context, new AuthNetworking() {
            @Override
            public void accessInternet(boolean isConnected) {
                if (isConnected) {
                    if (view.getId() == R.id.accept || view.getId() == R.id.delete) {
                        final String id = view.getTag().toString();
                        final int state = view.getId() == R.id.accept ? AuthUsers.ACCEPTED : AuthUsers.REJECTED;

                        AuthHelper.startDialog(context);
                        Call<List<AuthUsers>> call = RestHelper.API_SERVICE.updateUserState(id, state);
                        call.enqueue(new Callback<List<AuthUsers>>() {
                            @Override
                            public void onResponse(Call<List<AuthUsers>> call, Response<List<AuthUsers>> response) {
                                AuthHelper.stopDialog();
                                if (response.isSuccessful()) {
                                    itemsList.clear();
                                    itemsList.addAll(response.body());
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<List<AuthUsers>> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });

                    } else if (view.getId() == R.id.icon) {

                    }
                } else {
                    AuthHelper.stopDialog();
                    Toast.makeText(context, context.getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        // Global.CART.clear();
        mRecyclerView = recyclerView;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, type;
        public ImageView accept, delete, icon, state;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            type = (TextView) view.findViewById(R.id.type);
            accept = (ImageView) view.findViewById(R.id.accept);
            delete = (ImageView) view.findViewById(R.id.delete);
            icon = (ImageView) view.findViewById(R.id.icon);
            state = (ImageView) view.findViewById(R.id.state);
        }

    }

    public MngUsersAdapter(Context context, List<AuthUsers> moviesList) {
        this.itemsList = moviesList;
        this.context = context;
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mng_user_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AuthUsers user = itemsList.get(position);
        holder.name.setText(user.getFirst_name() + " " + user.getLast_name());
        holder.type.setText(user.getUser_type() == AuthUsers.STUFF ? context.getResources().getString(R.string.stuff) : context.getResources().getString(R.string.student));

        holder.delete.setTag(user.getId());
        holder.accept.setTag(user.getId());
        holder.delete.setOnClickListener(this);
        holder.accept.setOnClickListener(this);

        holder.icon.setTag(user.getId());
        holder.icon.setOnClickListener(this);

        holder.accept.setVisibility(View.VISIBLE);
        holder.delete.setVisibility(View.VISIBLE);

        if (user.getState() == AuthUsers.ACCEPTED) {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.state.setImageDrawable(context.getResources().getDrawable(R.drawable.accepted));
        } else if (user.getState() == AuthUsers.REJECTED) {
            holder.delete.setVisibility(View.INVISIBLE);
            holder.state.setImageDrawable(context.getResources().getDrawable(R.drawable.rejected));
        } else {
            holder.state.setImageDrawable(context.getResources().getDrawable(R.drawable.waiting));
        }
        //Images.set(context, holder.icon, "profile/" + user.getId() + ".png", Paths.profileImagesDir + "/" + user.getId() + ".png");
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}