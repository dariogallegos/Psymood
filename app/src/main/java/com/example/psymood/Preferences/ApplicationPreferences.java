package com.example.psymood.Preferences;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class ApplicationPreferences {
    private static SharedPreferences mSharedPreferences;

    public static  void init(Context context){
        if(mSharedPreferences == null){
            mSharedPreferences = context.getSharedPreferences("MY_PREFES", Activity.MODE_PRIVATE);
        }
    }

    public static void saveName(String KEYNAME, String name){
        SharedPreferences.Editor prefersEditor = mSharedPreferences.edit();
        prefersEditor.putString(KEYNAME,name);
        prefersEditor.apply();
    }

    public static String loadName(String KEYNAME){
        return mSharedPreferences.getString(KEYNAME,"");
    }


    /*public static void saveName(String KEYNAME, List<ExamenModel> examenModelList){
        SharedPreferences.Editor prefersEditor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(examenModelList);
        prefersEditor.putString(KEYNAME,json);
        prefersEditor.apply();
    }*/

    /*public static List<ExamenModel> loadName(String KEYNAME){

        List<ExamenModel> task;
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(KEYNAME, "");
        task =  gson.fromJson(json, new TypeToken<ArrayList<ExamenModel>>(){}.getType());
        return task;
    }*/

}
