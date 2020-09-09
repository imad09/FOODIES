package com.foodies.app.Models;

public class DashboardModel {
    private String image_url;
    private String dash_name;

    public DashboardModel(String image_url, String dash_name) {
        this.image_url = image_url;
        this.dash_name = dash_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getDash_name() {
        return dash_name;
    }
}
