package com.foodies.app.Models;

public class TourModel {
    private String name,description;
    private String image;

    public TourModel(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

}
