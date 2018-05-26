package com.cafateria.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.activities.Cart;
import com.cafateria.handler.Images;
import com.cafateria.handler.Paths;
import com.cafateria.helper.Global;
import com.cafateria.model.Food;
import com.cafateria.rest.ApiClient;
import com.github.javiersantos.bottomdialogs.BottomDialog;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> implements View.OnClickListener {

    private Context context;
    private RecyclerView mRecyclerView;
    private MyViewHolder holder;

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.delete) {
            showDialogDelete(Integer.parseInt(view.getTag().toString()));
        } else if (view.getId() == R.id.plus) {
            Food i = (Food) view.getTag();
            i.count += i.count < i.quantity ? 1 : 0;
            holder.count.setText(i.count + "");
            Global.CART.get(Global.CART.indexOf(i)).count = i.count;
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } else if (view.getId() == R.id.minus) {
            Food i = (Food) view.getTag();
            i.count -= i.count > 1 ? 1 : 0;
            mRecyclerView.getAdapter().notifyDataSetChanged();
            Global.CART.get(Global.CART.indexOf(i)).count = i.count;
            holder.count.setText(i.count + "");
        } else if (view.getId() == R.id.icon) {
            String id = view.getTag().toString();
            //Intent i = new Intent(context, ItemDetails.class);
            //i.putExtra("id", id);
            //context.startActivity(i);
        }
        updateTotal();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        updateTotal();
        mRecyclerView = recyclerView;
    }

    private void updateTotal() {
        float _total = 0;
        for (Food i : Global.CART) {
            _total += i.price * i.count;
        }
        ((Cart) context).setTotal(_total + " " + context.getResources().getString(R.string.r));
    }

    private void showDialogDelete(final int id) {
        new BottomDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.alert))
                .setContent(context.getResources().getString(R.string.delete_from_cart))
                .setPositiveText(context.getString(R.string.delete))
                .setNegativeText(context.getString(R.string.cancel))
                .setNegativeTextColorResource(android.R.color.holo_green_light)
                .setPositiveBackgroundColor(android.R.color.white)
                .setPositiveTextColorResource(android.R.color.holo_red_dark)
                .onPositive(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(BottomDialog dialog) {
                        //delete item from view
                        Food i = null;
                        for (Food item : Global.CART)
                            if (item.id == id)
                                i = item;

                        if (i != null)
                            Global.CART.remove(i);
                        updateTotal();
                        if (Global.CART.size() > 0)
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        else
                            ((Activity) context).finish();
                        dialog.dismiss();
                    }
                }).show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price, count;
        public ImageView delete, icon, plus, minus;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            price = (TextView) view.findViewById(R.id.product_price);
            plus = (ImageView) view.findViewById(R.id.plus);
            minus = (ImageView) view.findViewById(R.id.minus);
            delete = (ImageView) view.findViewById(R.id.delete);
            icon = (ImageView) view.findViewById(R.id.icon);
        }
    }

    public CartAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        this.holder = holder;
        holder.title.setText(Global.CART.get(position).name.replaceAll("[\r\n]+", "\n"));

        //price * quantity
        float net_price = Global.CART.get(position).price * Global.CART.get(position).count;
        holder.price.setText(net_price + context.getResources().getString(R.string.r));
        holder.count.setText(Global.CART.get(position).count + "");

        holder.plus.setTag(Global.CART.get(position));
        holder.minus.setTag(Global.CART.get(position));
        holder.delete.setTag(Global.CART.get(position).id);


        holder.plus.setOnClickListener(this);
        holder.minus.setOnClickListener(this);
        holder.delete.setOnClickListener(this);

        holder.icon.setTag(Global.CART.get(position).id);
        holder.icon.setOnClickListener(this);
        Images.set(context, holder.icon, ApiClient.BASE_UPLOADS_URL + Global.CART.get(position).file_name, Paths.imagesDir + Global.CART.get(position).file_name);
        //Picasso.with(context).load(ApiClient.BASE_UPLOADS_URL + Global.CART.get(position).file_name).placeholder(R.drawable.placeholder).error(R.drawable.error).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return Global.CART.size();
    }
}