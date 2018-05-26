package com.cafateria.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.activities.Account;
import com.cafateria.activities.AddFood;
import com.cafateria.activities.AdminSettings;
import com.cafateria.activities.Categories;
import com.cafateria.activities.Fav;
import com.cafateria.activities.Home;
import com.cafateria.activities.LessQuantity;
import com.cafateria.activities.ManageCats;
import com.cafateria.activities.ManageDiseases;
import com.cafateria.activities.ManageUsers;
import com.cafateria.activities.Search;
import com.cafateria.activities.Settings;
import com.cafateria.activities.ShowOrdrs;
import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.AuthUsers;

import java.io.File;

public class Init {
    private static final String TAG = "INIT";
    static DrawerLayout drawer;

    private static void switchActivity(int id, Activity activity) {
        boolean isStartActivity = true;
        Intent i = null;
        if (id == R.id.nav_home) {
            isStartActivity = true;
            i = new Intent(activity, Home.class);
        } else if (id == R.id.nav_logout) {
            isStartActivity = false;
            AuthHelper.logout(activity);
        } else if (id == R.id.nav_add_foods) {
            isStartActivity = true;
            i = new Intent(activity, AddFood.class);
        } else if (id == R.id.nav_manage_cats) {
            isStartActivity = true;
            i = new Intent(activity, ManageCats.class);
        } else if (id == R.id.nav_manage_diseases) {
            isStartActivity = true;
            i = new Intent(activity, ManageDiseases.class);
        } else if (id == R.id.nav_manage_users) {
            isStartActivity = true;
            i = new Intent(activity, ManageUsers.class);
        } else if (id == R.id.nav_cats) {
            isStartActivity = true;
            i = new Intent(activity, Categories.class);
        } else if (id == R.id.nav_show_orders) {
            i = new Intent(activity, ShowOrdrs.class);
        } else if (id == R.id.nav_fav) {
            isStartActivity = true;
            i = new Intent(activity, Fav.class);
        } else if (id == R.id.nav_less_quantity) {
            isStartActivity = true;
            i = new Intent(activity, LessQuantity.class);
        } else if (id == R.id.nav_search) {
            isStartActivity = true;
            i = new Intent(activity, Search.class);
        } else if (id == R.id.nav_account) {
            isStartActivity = true;
            i = new Intent(activity, Account.class);
        } else if (id == R.id.nav_settings) {
            isStartActivity = false;
            activity.finish();
            Intent intent = new Intent(activity, Settings.class);
            Global.CLASS_TO_BACK = activity.getClass();
            intent.putExtra("type", Global.CLASS_TO_BACK);
            activity.startActivity(intent);
        } else if (id == R.id.nav_admin_settings) {
            isStartActivity = false;
            activity.finish();
            Intent intent = new Intent(activity, AdminSettings.class);
            Global.CLASS_TO_BACK = activity.getClass();
            intent.putExtra("type", Global.CLASS_TO_BACK);
            activity.startActivity(intent);
        } else if (id == R.id.nav_share) {
            isStartActivity = false;
            ApplicationInfo app = activity.getApplicationInfo();
            String filePath = app.sourceDir;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            activity.startActivity(Intent.createChooser(intent, "Share app via"));
        }

        if (isStartActivity && i != null) {
            //Log.e(TAG,"if (isStartActivity && i != null) {");
            activity.finish();
            activity.startActivity(i);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    public static void initDrawer(final AppCompatActivity activity, int icon_id) {
        //init action bar and drawer layout
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //select menu for logged user
        AuthUsers user = AuthHelper.getLoggedUser(activity);
        //set vars (user info ' pic,name,mail,...)
        NavigationView nv = (NavigationView) activity.findViewById(R.id.nav_view);
        Log.e(TAG, "user type ->" + (user.getUser_type() == AuthUsers.STUDENT ? "student" : "admin"));
        if (nv != null) {
            if (user.getUser_type() == AuthUsers.STUDENT || user.getUser_type() == AuthUsers.STUFF) {
                nv.inflateMenu(R.menu.home_menu);
            } else {
                nv.inflateMenu(R.menu.admin_menu);
            }


            //fill header info with user data
            View v = nv.getHeaderView(0);
            if (v != null) {
                ImageView drawer_icon = (ImageView) v.findViewById(R.id.img);
                drawer_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.finish();
                        activity.startActivity(new Intent(activity, Account.class));
                    }
                });
                //Images.set(activity, drawer_icon, "profile/" + user.getId() + ".png", Paths.profileImagesDir + "/" + user.getId() + ".png");
                ((TextView) v.findViewById(R.id.name)).setText(user.getFirst_name() + " " + user.getLast_name());
                ((TextView) v.findViewById(R.id.email)).setText(user.getEmail());

            }

            nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @SuppressWarnings("StatementWithEmptyBody")
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    // Handle navigation view item clicks here.
                    //Log.e("Activity",(item.getItemId() == R.id.logOutIcon)+"");
                    switchActivity(item.getItemId(), activity);
                    DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            });

            //set selected icon bg
            nv.setCheckedItem(icon_id);
        }
    }
}
