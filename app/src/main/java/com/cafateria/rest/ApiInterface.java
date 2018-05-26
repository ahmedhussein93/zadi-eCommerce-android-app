package com.cafateria.rest;

/**
 * Created  on 04/03/2018.
 * *  *
 */


import com.cafateria.auth.AuthUsers;
import com.cafateria.model.Cats;
import com.cafateria.model.Disease;
import com.cafateria.model.Food;
import com.cafateria.model.LastUpdatedModel;
import com.cafateria.model.Order;
import com.cafateria.model.SectionedRecycleViewModel;
import com.cafateria.model.Sections;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    Call<AuthUsers> getUser(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("checkMail")
    Call<AuthUsers> getUser(@Field("email") String email);

    @FormUrlEncoded
    @POST("updatePassword")
    Call<AuthUsers> updatePassword(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("register")
    Call<AuthUsers> register(@Field("first_name") String first_name, @Field("last_name") String last_name, @Field("email") String email, @Field("password") String password, @Field("user_type") int type);

    @FormUrlEncoded
    @POST("updateUserInfo")
    Call<AuthUsers> update(@FieldMap Map<String, Object> data, @Field("id") int id);

    @FormUrlEncoded
    @POST("add_food")
    Call<Food> addFood(@Field("name") String name, @Field("desc") String desc, @Field("price") float price, @Field("ids[]") List<Integer> d_ids);

    @FormUrlEncoded
    @POST("get_foods")
    Call<List<SectionedRecycleViewModel>> getFood(@Field("cat_id") int cat_id, @Field("last_item_id") int item_id);

    @FormUrlEncoded
    @POST("get_sections")
    Call<Sections> getSections(@Field("last_food_id") int lf, @Field("last_drink_id") int ld, @Field("food_count") int fc, @Field("drink_count") int dc);

    @Multipart
    @POST("uploadFile")
    Call<Food> uploadFile(@Part MultipartBody.Part file,
                          @Part("time") RequestBody time,
                          @Part("quantity") RequestBody quantity,
                          @Part("name") RequestBody name,
                          @Part("desc") RequestBody desc,
                          @Part("price") RequestBody price,
                          @Part("added_by") RequestBody added_by,
                          @Part("type") RequestBody type
    );

    @FormUrlEncoded
    @POST("delete_food")
    Call<String> delFood(@Field("id") int id);

    @FormUrlEncoded
    @POST("search")
    Call<List<Food>> search(@Field("name") String name, @Field("desc") String desc, @Field("from") String from, @Field("to") String to, @Field("type") int food_type);

    @GET("getDiseases")
    Call<List<Disease>> getDiseases();

    @FormUrlEncoded
    @POST("sendDiseases")
    Call<String> sendDiseases(@Field("ids[]") List<Integer> ids, @Field("id") int id);

    @FormUrlEncoded
    @POST("sendFoodDiseases")
    Call<String> sendFoodDiseases(@Field("ids[]") List<Integer> ids, @Field("id") int id);

    @FormUrlEncoded
    @POST("order")
    Call<String> order(@Field("oder_string_json") String osj);

    @GET("getOrders")
    Call<List<Order>> getOrders();

    @FormUrlEncoded
    @POST("setOrderAsDelivered")
    Call<String> setOrderAsDelivered(@Field("id") int id);

    @FormUrlEncoded
    @POST("sendEmail")
    Call<String> sendMail(@Field("from") String from, @Field("password") String password, @Field("to") String to, @Field("sub") String subject, @Field("txt") String text);

    @GET("getCats")
    Call<List<Cats>> getCats();

    @FormUrlEncoded
    @POST("addCat")
    Call<List<Cats>> addCat(@Field("name_ar") String ar, @Field("name_en") String en);

    @FormUrlEncoded
    @POST("deleteCat")
    Call<List<Cats>> delCat(@Field("id") String id);

    @FormUrlEncoded
    @POST("getUsers")
    Call<List<AuthUsers>> getUsers(@Field("id") int id);

    @FormUrlEncoded
    @POST("updateUserState")
    Call<List<AuthUsers>> updateUserState(@Field("id") String id, @Field("state") int state);

    @FormUrlEncoded
    @POST("getItemsByCatId")
    Call<Sections> getItemsById(@Field("cat_id") int cat_id, @Field("last_id") int last_id);

    @FormUrlEncoded
    @POST("getByQuantity")
    Call<List<Food>> getByQuantity(@Field("num") int num);

    @FormUrlEncoded
    @POST("checkLastUpdated")
    Call<LastUpdatedModel> checkLastUpdated(@Field("last_order_id") int last_order_id);

    @FormUrlEncoded
    @POST("rate")
    Call<String> rate(@Field("user_id") int user_id, @Field("comment") String comment, @Field("dev_id") String dev_id, @Field("rate") float rate);

    @FormUrlEncoded
    @POST("getLessQuantity")
    Call<List<Food>> getLessQuantity(@Field("amount") int q);

    @FormUrlEncoded
    @POST("updateFood")
    Call<String> updateFood(@Field("id") int id, @Field("name") String name, @Field("quantity") int q, @Field("description") String desc, @Field("price") float price);

    @FormUrlEncoded
    @POST("addDisease")
    Call<List<Disease>> addDisease(@Field("name") String ar, @Field("name_en") String en);

    @FormUrlEncoded
    @POST("delDisease")
    Call<List<Disease>> delDisease(@Field("id") String id);

    @FormUrlEncoded
    @POST("updateTokenFCM")
    Call<String> updateTokenFCM(@Field("user_id") int id, @Field("token") String token);

    @FormUrlEncoded
    @POST("getOrder")
    Call<Order> getOrder(@Field("order_id")  int order_id);
}


