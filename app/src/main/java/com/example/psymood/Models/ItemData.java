package com.example.psymood.Models;

public class ItemData {
    private String title;
    private int image;


    public ItemData(){}

    public ItemData(String name, int image) {
        this.title = name;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
