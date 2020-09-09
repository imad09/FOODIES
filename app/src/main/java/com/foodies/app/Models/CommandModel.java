package com.foodies.app.Models;


public class CommandModel {
    private String uid,mobile;
    private long total_price,order_number,time;
    private boolean valide;

    public CommandModel() {
    }

    public String getUid() {
        return uid;
    }

    public String getMobile() {
        return mobile;
    }

    public long getTotal_price() {
        return total_price;
    }

    public long getOrder_number() {
        return order_number;
    }

    public long getTime() {
        return time;
    }

    public boolean isValide() {
        return valide;
    }
}
