package com.cafateria.model;

import com.cafateria.helper.Global;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created  on 10/03/2018.
 * *  *
 */

public class Food {
    public static final int TYPE_FOOD = 0;
    public static final int TYPE_DRINK = 1;

    public int id, add_by;
    public String name;
    public String description;
    public String file_name;
    public float price;
    public int food_type;
    public Timestamp timestamp;
    public List<Disease> diseases;
    public int count = 1;
    public int quantity;

    public Food() {

    }

    public Food(int id, int add_by, int quantity, String name, String description, String img_path, float price, Timestamp timestamp, int food_type, List<Disease> diseases) {
        this.id = id;
        this.add_by = add_by;
        this.name = name;
        this.description = description;
        this.file_name = img_path;
        this.price = price;
        this.timestamp = timestamp;
        this.food_type = food_type;
        this.diseases = diseases;
        this.quantity = quantity;
    }


    public boolean isOnCart() {
        for (Food item : Global.CART)
            if (item.id == this.id)
                return true;
        return false;
    }

    public void addToCart() {
        if (!isOnCart())
            Global.CART.add(this);
    }

    public void removeFromCart() {
        Food i = null;
        for (Food item : Global.CART)
            if (item.id == this.id)
                i = item;
        if (i != null)
            Global.CART.remove(i);
    }
}
