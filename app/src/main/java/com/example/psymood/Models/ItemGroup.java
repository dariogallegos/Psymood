package com.example.psymood.Models;

import java.util.List;

public class ItemGroup {
    private  String title;
    private List<ItemData> itemData;

    public  ItemGroup(){

    }

    public ItemGroup(String title,List<ItemData> itemData) {
        this.title = title;
        this.itemData = itemData;
    }

    public List<ItemData> getItemList() {
        return itemData;
    }
    public void setItemList(List<ItemData> itemData) {
        this.itemData = itemData;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
