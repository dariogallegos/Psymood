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

    public static void saveNumState(String KEYNAME,int numState){
        SharedPreferences.Editor prefersEditor = mSharedPreferences.edit();
        prefersEditor.putInt(KEYNAME,numState);
        prefersEditor.apply();
    }

    public static int loadNumState(String KEYNAME){
        return mSharedPreferences.getInt(KEYNAME, 0);
    }


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
