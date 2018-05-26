package com.cafateria.rest;

/**
 * Created  on 10/03/2018.
 * *  *
 */

public class RestHelper {
    public static final ApiInterface API_SERVICE = ApiClient.getClient().create(ApiInterface.class);
}
