package com.cafateria.model;

import java.util.List;

/**
 * Created  on 12/03/2018.
 * *  *
 */

public class Order {
    public int id, user_id, user_type, delivered;
    public String name, phone, date, req_date, address;
    public List<ItemIdCount> items;
    public List<Food> foods;
    public int isDelivery;

    public Order() {
    }
}

