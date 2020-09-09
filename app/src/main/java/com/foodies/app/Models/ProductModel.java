package com.foodies.app.Models;

public class ProductModel {
    private String product_name,
    product_image,
    product_description,
    product_key,
    product_category;
    public String getProduct_category() {
        return product_category;
    }

    private int product_price,cooking_time;
    private boolean available,shipping;
    public long getProduct_quantity() {
        return product_quantity;
    }

    private long product_id,product_quantity,product_added_time;

    public long getProduct_added_time() {
        return product_added_time;
    }

    public long getProduct_id() {
        return product_id;
    }

    public ProductModel() {
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getProduct_description() {
        return product_description;
    }

    public String getProduct_key() {
        return product_key;
    }

    public int getProduct_price() {
        return product_price;
    }

    public int getCooking_time() {
        return cooking_time;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isShipping() {
        return shipping;
    }
}
