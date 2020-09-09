package com.foodies.app.Models;

public class CartModel {
    private String key;
    private long time;
    private int quantity;

    public CartModel(String key, long time, int quantity) {
        this.key = key;
        this.time = time;
        this.quantity = quantity;
    }

    public CartModel() {
    }

    public String getKey() {
        return key;
    }

    public long getTime() {
        return time;
    }

    public int getQuantity() {
        return quantity;
    }

}
