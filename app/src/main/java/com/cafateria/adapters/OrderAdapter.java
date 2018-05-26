package com.cafateria.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.activities.ShowOrderDetails;
import com.cafateria.auth.AuthUsers;
import com.cafateria.fragments.HomeFragment;
import com.cafateria.helper.Global;
import com.cafateria.model.Order;

import java.util.List;

/**
 * Created  on 13/03/2018.
 * *  *
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> implements View.OnClickListener {

    private static final String TAG = OrderAdapter.class.getSimpleName();
    private List<Order> list;
    private Context context;

    public OrderAdapter(Context context, List<Order> orders) {
        this.list = orders;
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.name) {
            Order order = (Order) view.getTag();

            HomeFragment homeFragment = (HomeFragment) ((Activity) context).getFragmentManager().findFragmentById(R.id.home);
            if (homeFragment != null)
                homeFragment.update();
            //else
            /*Intent i = new Intent(context, ShowOrderDetails.class);
            i.putExtra("order_id", order.id);
            context.startActivity(i);*/
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, req_date, type;
        ImageView is_delivered;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            req_date = (TextView) view.findViewById(R.id.re_date);
            date = (TextView) view.findViewById(R.id.date);
            type = (TextView) view.findViewById(R.id.type);
            is_delivered = (ImageView) view.findViewById(R.id.is_delivered);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order item = list.get(position);
        holder.is_delivered.setVisibility(item.delivered == 1 ? View.VISIBLE : View.GONE);
        holder.name.setText(item.name);
        holder.name.setTag(item);
        holder.name.setOnClickListener(this);
        //holder.date.setText(DateFormat.format("dd-MM-yyyy hh:mm", Long.parseLong(item.date)).toString());
        holder.req_date.setText(!item.req_date.isEmpty() ? Global.getTimeAgo(Long.parseLong(item.req_date)) : "");
        //Log.e(TAG, "time ts:" + item.date);
        holder.type.setText(item.user_type == AuthUsers.STUDENT ? "student" : "stuff");
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}