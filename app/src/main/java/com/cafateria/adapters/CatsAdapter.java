package com.cafateria.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.activities.Home;
import com.cafateria.database.PrefManager;
import com.cafateria.helper.Global;
import com.cafateria.model.Cats;


public class CatsAdapter extends RecyclerView.Adapter<CatsAdapter.MyViewHolder> implements View.OnClickListener {

    private Context context;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.root) {
            PrefManager.setCurrentCatId(context, Integer.parseInt(view.getTag().toString()));
            ((Activity) context).finish();
            context.startActivity(new Intent(context, Home.class));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public RelativeLayout root;

        MyViewHolder(View view) {
            super(view);
            root = (RelativeLayout) view.findViewById(R.id.root);
            content = (TextView) view.findViewById(R.id.content);
        }
    }

    public CatsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Cats cat = Global.CATS.get(position);
        holder.content.setText(cat.getName(context));
        holder.root.setTag(cat.getId());
        holder.root.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return Global.CATS.size();
    }
}