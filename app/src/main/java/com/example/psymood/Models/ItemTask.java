package com.example.psymood.Models;

public class ItemTask {

    String titleTask;
    String numTask;
    int colorTask;
    int iconTask;

    public ItemTask(){}

    public ItemTask(String titleTask, String numTask, int colorTask,int iconTask) {
        this.titleTask = titleTask;
        this.numTask = numTask;
        this.colorTask =  colorTask;
        this.iconTask = iconTask;
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
