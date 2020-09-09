package com.foodies.app.Models;

public class MenuModel {
    private String menu_name,
    menu_image,
    menu_background;
    public String getMenu_background() {
        return menu_background;
    }
    private int average;
    public MenuModel() {
    }

    public String getMenu_name() {
        return menu_name;
    }

    public String getMenu_image() {
        return menu_image;
    }

    public int getAverage() {
        return average;
    }
}
