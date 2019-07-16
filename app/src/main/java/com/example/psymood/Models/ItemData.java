package com.example.psymood.Models;

public class ItemData {
    private String title,image;


    public ItemData(){}

    public ItemData(String name, String image) {
        this.title = name;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
