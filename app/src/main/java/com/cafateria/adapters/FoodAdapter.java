package com.cafateria.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafateria.R;
import com.cafateria.activities.EditFood;
import com.cafateria.activities.Home;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthInternet;
import com.cafateria.auth.AuthNetworking;
import com.cafateria.auth.AuthUsers;
import com.cafateria.database.PrefManager;
import com.cafateria.handler.Images;
import com.cafateria.handler.Paths;
import com.cafateria.helper.Global;
import com.cafateria.model.Disease;
import com.cafateria.model.Food;
import com.cafateria.rest.ApiClient;
import com.cafateria.rest.RestHelper;
import com.github.javiersantos.bottomdialogs.BottomDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final String TAG = "ITEM_ADAPTER";
    private static final int HEADER = 101;
    private static final int ADMIN = 102;
    private static final int STUDENT = 103;
    private static final int STUFF = 104;
    private static final int MORE = 105;
    private List<Food> itemsList;
    private Context context;

    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.icon) {

        } else if (view.getId() == R.id.add_cart) {
            final Food item = (Food) view.getTag();
            if (item.isOnCart()) {
                //Global.CART.remove(item);
                item.removeFromCart();
                ((ImageView) view).setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_shopping_cart_black_24dp));
            } else {

                if (isCompatible(item)) {
                    //add to cart
                    item.addToCart();
                    ((ImageView) view).setImageDrawable(context.getResources().getDrawable(R.drawable.ic_remove_shopping_cart_black_24dp));
                } else {
                    //show dialoge ->
                    new BottomDialog.Builder(context)
                            .setTitle(context.getResources().getString(R.string.alert))
                            .setContent(context.getResources().getString(R.string.not_compatable))
                            .setPositiveText(context.getString(R.string.add))
                            .setNegativeText(context.getString(R.string.cancel))
                            .setNegativeTextColorResource(android.R.color.holo_green_light)
                            .setPositiveBackgroundColor(android.R.color.white)
                            .setPositiveTextColorResource(android.R.color.holo_red_dark)
                            .onPositive(new BottomDialog.ButtonCallback() {
                                @Override
                                public void onClick(BottomDialog dialog) {
                                    item.addToCart();
                                    ((ImageView) view).setImageDrawable(context.getResources().getDrawable(R.drawable.ic_remove_shopping_cart_black_24dp));
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        } else if (view.getId() == R.id.fav) {
            Food f = (Food) view.getTag();
            if (PrefManager.isInFav(context, f)) {//first ret false -> item exist
                PrefManager.RemoveFromFav(context, f);
                ((ImageView) view).setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
            } else {//item not exist so add it
                PrefManager.addToFav(context, f);
                ((ImageView) view).setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
            }
        } else if (view.getId() == R.id.title) {//on tap on more
           /* int cat_id = (int) view.getTag();
            Log.e(TAG, "cat_id:" + cat_id);*/
            Home.getItemsByCatId();
        } else if (view.getId() == R.id.del) {

            //show dialoge ->
            new BottomDialog.Builder(context)
                    .setTitle(context.getResources().getString(R.string.alert))
                    .setContent(context.getResources().getString(R.string.conf_del))
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

                                        final Food food = (Food) view.getTag();
                                        Call<String> call = RestHelper.API_SERVICE.delFood(food.id);
                                        call.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                AuthHelper.stopDialog();
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(context, "item deleted", Toast.LENGTH_SHORT).show();
                                                    itemsList.remove(food);
                                                    notifyDataSetChanged();
                                                } else {
                                                    Toast.makeText(context, "error while delete item!", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                t.printStackTrace();
                                            }
                                        });

                                    } else {
                                        AuthHelper.stopDialog();
                                        Toast.makeText(context, context.getString(R.string.not_con), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            dialog.dismiss();

                        }
                    }).show();

        }
    }

    private boolean isCompatible(Food item) {
        List<Disease> my, i;
        my = AuthHelper.getLoggedUser(context).getDiseases();
        i = item.diseases;
        for (Disease in_my : my) {
            for (Disease in_item : i) {
                Log.e("isCompatible", "i have :" + in_my.id + ", and food have:" + in_item.id);
                if (in_my.id == in_item.id)
                    return false;
            }
        }
        return true;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public class AdminViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle, price, quantity;
        public ImageView icon, del;

        public AdminViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
            price = (TextView) view.findViewById(R.id.price);
            quantity = (TextView) view.findViewById(R.id.quantity);
            del = (ImageView) view.findViewById(R.id.del);
            icon = (ImageView) view.findViewById(R.id.icon);
        }
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle, price;
        public ImageView icon, fav, add_cart;

        public StudentViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
            price = (TextView) view.findViewById(R.id.price);
            icon = (ImageView) view.findViewById(R.id.icon);
            fav = (ImageView) view.findViewById(R.id.fav);
            add_cart = (ImageView) view.findViewById(R.id.add_cart);

        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;

        public HeaderViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
        }
    }

    public class MoreViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MoreViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    public FoodAdapter(Context context, List<Food> list) {
        this.itemsList = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_header, parent, false));
        } else if (viewType == MORE) {
            return new MoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_more, parent, false));
        } else if (viewType == ADMIN) {
            return new AdminViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_admin, parent, false));
        } else if (viewType == STUFF) {
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_student, parent, false));
        } else //student
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_student, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Food food = itemsList.get(position);
        if (getItemViewType(position) == HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.title.setText(food.description);
            headerHolder.count.setText(food.id + "");
        } else if (getItemViewType(position) == MORE) {
            MoreViewHolder moreHolder = (MoreViewHolder) holder;
            moreHolder.title.setOnClickListener(this);
            moreHolder.title.setTag(food.food_type);
        } else if (getItemViewType(position) == ADMIN) {//admin view
            AdminViewHolder adminHolder = (AdminViewHolder) holder;
            adminHolder.title.setText(food.name.replaceAll("[\r\n]+", "\n"));
            adminHolder.subtitle.setText(food.description);
            adminHolder.price.setText(food.price + " " + context.getResources().getString(R.string.r));
            adminHolder.quantity.setText(food.quantity + "");
            adminHolder.del.setTag(food);
            adminHolder.del.setOnClickListener(this);
            adminHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Log.e("test", "CLICK ADMIN ON ICON");
                    Global.FOOD_TO_SHOW_DETAILS = food;
                    Intent i = new Intent(context, EditFood.class);
                    context.startActivity(i);*/
                }
            });
            Images.set(context, adminHolder.icon, ApiClient.BASE_UPLOADS_URL + food.file_name, Paths.imagesDir + food.file_name);
            //Picasso.with(context).load(ApiClient.BASE_UPLOADS_URL + food.file_name).placeholder(R.drawable.placeholder).error(R.drawable.error).into(adminHolder.icon);
        } else {//student and stuff
            StudentViewHolder studentHolder = (StudentViewHolder) holder;
            studentHolder.title.setText(food.name.replaceAll("[\r\n]+", "\n"));
            studentHolder.subtitle.setText(food.description);
            studentHolder.price.setText(food.price + " RS");
            studentHolder.fav.setTag(food.id + "");
            studentHolder.fav.setOnClickListener(this);
            //Picasso.with(context).load(ApiClient.BASE_UPLOADS_URL + food.file_name).placeholder(R.drawable.placeholder).error(R.drawable.error).into(studentHolder.icon);
            Images.set(context, studentHolder.icon, ApiClient.BASE_UPLOADS_URL + food.file_name, Paths.imagesDir + food.file_name);

            if (PrefManager.isInFav(context, food))
                studentHolder.fav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
            else
                studentHolder.fav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
            studentHolder.fav.setTag(food);

            if (food.isOnCart())
                studentHolder.add_cart.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_remove_shopping_cart_black_24dp));
            else
                studentHolder.add_cart.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_shopping_cart_black_24dp));

            studentHolder.add_cart.setTag(food);

            studentHolder.add_cart.setOnClickListener(this);

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (itemsList.get(position).name.equalsIgnoreCase("header"))
            return HEADER;
        else if (itemsList.get(position).name.equalsIgnoreCase("more"))
            return MORE;
        if (AuthHelper.getLoggedUser(context).getUser_type() == AuthUsers.ADMIN)
            return ADMIN;
        return 0;
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}