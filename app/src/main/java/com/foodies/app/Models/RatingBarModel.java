package com.foodies.app.Models;

public class RatingBarModel {
    private String uid,message;
    private long time;
    private float rating;

    public RatingBarModel(String uid, String message, long time, float rating) {
        this.uid = uid;
        this.message = message;
        this.time = time;
        this.rating = rating;
    }

    public RatingBarModel() {
    }

    public String getUid() {
        return uid;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    public float getRating() {
        return rating;
    }

}
