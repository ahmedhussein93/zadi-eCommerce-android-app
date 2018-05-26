package com.cafateria.rest;

/**
 * Created  on 04/03/2018.
 * *  *
 */

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static final boolean online = true;//if webservice is online
    private static final boolean localeLan = false;// true->if webservice if locale accessed by real phone via wifi false->if user access by genemotion

    public static final String BASE_UPLOADS_URL = online ? "https://cafateria.000webhostapp.com/cafateria/uploads/" : localeLan ? "http://192.168.43.240/cafateria/uploads/" : "http://192.168.135.2/cafateria/uploads/";
    public static final String BASE_API_URL = online ? "https://cafateria.000webhostapp.com/cafateria/index.php/api/" : localeLan ? "http://192.168.43.240/cafateria/api/" : "http://192.168.135.2/cafateria/api/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}