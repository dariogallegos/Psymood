package com.example.psymood.Preferences;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.psymood.Models.ItemGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


public class ApplicationPreferences {
    private static SharedPreferences mSharedPreferences;

    public static  void init(Context context){
        if(mSharedPreferences == null){
            mSharedPreferences = context.getSharedPreferences("MY_PREFES", Activity.MODE_PRIVATE);
        }
    }

    /*public static void saveGroup(String KEYNAME, ItemGroup itemGroup){
        SharedPreferences.Editor prefersEditor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemGroup);
        prefersEditor.putString(KEYNAME,json);
        prefersEditor.apply();
    }

    public static ItemGroup loadGroup(String KEYNAME){
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(KEYNAME, "");
        return gson.fromJson(json,ItemGroup.class);
    }*/


    public static void saveListGroup(String KEYNAME, List<ItemGroup> itemGroupList){
        SharedPreferences.Editor prefersEditor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemGroupList);
        prefersEditor.putString(KEYNAME,json);
        prefersEditor.apply();
    }

    public static List<ItemGroup> loadListGroup(String KEYNAME){

        List<ItemGroup> task;
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(KEYNAME, "");
        task =  gson.fromJson(json, new TypeToken<ArrayList<ItemGroup>>(){}.getType());
        return task;
    }

}
