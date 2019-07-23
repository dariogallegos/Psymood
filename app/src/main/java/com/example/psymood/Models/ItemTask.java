package com.example.psymood.Models;

public class ItemTask {

    private String titleTask;
    private String numTask;
    private int colorTask;
    private int iconTask;
    private int menuItem;

    public ItemTask(){}

    public ItemTask(String titleTask, String numTask, int colorTask,int iconTask,int menuItem) {
        this.titleTask = titleTask;
        this.numTask = numTask;
        this.colorTask =  colorTask;
        this.iconTask = iconTask;
        this.menuItem = menuItem;
    }

    public int getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(int menuItem) {
        this.menuItem = menuItem;
    }

    public int getIconTask() {
        return iconTask;
    }

    public void setIconTask(int iconTask) {
        this.iconTask = iconTask;
    }

    public int getColorTask() {
        return colorTask;
    }

    public void setColorTask(int colorTask) {
        this.colorTask = colorTask;
    }

    public String getTitleTask() {
        return titleTask;
    }

    public void setTitleTask(String titleTask) {
        this.titleTask = titleTask;
    }

    public String getNumTask() {
        return numTask;
    }

    public void setNumTask(String numTask) {
        this.numTask = numTask;
    }

}
