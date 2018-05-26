package com.cafateria.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cafateria.R;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;
import com.cafateria.model.Food;
import com.cafateria.rest.ApiClient;
import com.cafateria.rest.ApiInterface;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetails extends AppCompatActivity {

    Food food;
    ScrollView main_container;
    private ImageView coverImage;
    private TextView title, year, homepage, companies, tagline, overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_movie_details);
        AppSettings.setLang(this, AppSettings.getLang(this));

        if (Global.FOOD_TO_SHOW_DETAILS == null) {
            finish();
            return;
        }
        food = Global.FOOD_TO_SHOW_DETAILS;
        init();
        //Picasso.with(this).load(Global.images_base_yrl + food.getPosterPath()).into(coverImage);
        //getMovieDetails(movie.getId());
        //setInfo();
    }

    private void getMovieDetails(int movie_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);


    }

    private void setInfo(Food movie) {
      /*  title.setText(movie.getTitle());
        year.setText(movie.getReleaseDate());
        overview.setText(movie.getOverview());
        homepage.setText(movie.getHomePage());

        for (ProductionCompanies company : movie.getProductionCompanies()) {
            companies.setText("\" " + company.name + "\" ");
        }

        tagline.setText(movie.getTagLine());*/
    }

    private void init() {
        /*main_container = (ScrollView) findViewById(R.id.main_container);
        main_container.setPadding(0, Global.getScreenHeight() - 100, 0, 0);
        title = (TextView) findViewById(R.id.title);
        year = (TextView) findViewById(R.id.year);
        coverImage = (ImageView) findViewById(R.id.cover);
        //blackMask = findViewById(R.id.black_mask);
        homepage = (TextView) findViewById(R.id.homepage);
        companies = (TextView) findViewById(R.id.companies);
        tagline = (TextView) findViewById(R.id.tagline);
        //overviewContainer = findViewById(R.id.overview_container);
        overview = (TextView) findViewById(R.id.overview);*/
    }

    @Override
    protected void onResume() {
        Global.CURRENT_ACTIVITY = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        Global.CURRENT_ACTIVITY = null;
        super.onPause();
    }
}
