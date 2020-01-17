package com.example.psymood.Activities;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.psymood.Models.InfoUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class FirebaseInteractor {

    private static DatabaseReference myRef;
    private static FirebaseUser myCurrentUser;
    private static List<String[]> data;
    private static String TAG = "firebaseInteractor";

    public static void initUI() {


        if (myRef == null) {
            myRef = FirebaseDatabase.getInstance().getReference("InfoUser");
            myCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        data = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + "ha habido un cambio");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public static void createInfoUserInDatabase() {

        myCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final InfoUser infoUser = new InfoUser(myCurrentUser.getDisplayName(),myCurrentUser.getEmail(),myCurrentUser.getPhotoUrl().toString());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(myCurrentUser.getUid()).exists()){
                    myRef.child(myCurrentUser.getUid()).setValue(infoUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"Se ha producido un error a leer de Database Realtime");
            }
        });
    }

    public static void saveAudioInDatabase(String urlAudio){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());
        DatabaseReference myAudioRef = myRef.child(myCurrentUser.getUid()).child("audiosUser").child(currentDateAndTime);
        myAudioRef.setValue(urlAudio);
    }

    public static void saveMoodStateInDatabase(String typeState, String valueState){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateAndTime = sdf.format(new Date());
        DatabaseReference myStateRef =  myRef.child(myCurrentUser.getUid()).child("statesUser").child(currentDateAndTime).child(typeState);
        myStateRef.setValue(valueState);

    }

    public static void savePhotoInDatabase(String urlPhoto) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());
        DatabaseReference myPhotoRef = myRef.child(myCurrentUser.getUid()).child("dailyPhotosUser").child(currentDateAndTime);
        myPhotoRef.setValue(urlPhoto);
    }

    public static void downloadData(){
        myCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference database = myRef.child(myCurrentUser.getUid());
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InfoUser infoUser =  dataSnapshot.getValue(InfoUser.class);

                Gson gson =  new Gson();
                JSONObject states = null;
                JSONObject audios = null;
                JSONObject photos =  null;

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    try {
                        String json;

                        if(Objects.equals(ds.getKey(), "statesUser")){
                            json = gson.toJson(ds.getValue());
                            states = new JSONObject(json);
                        }

                        if(Objects.equals(ds.getKey(), "audiosUser")){
                            json =  gson.toJson(ds.getValue());
                            audios = new JSONObject(json);
                        }

                        if(Objects.equals(ds.getKey(),"dailyPhotosUser")){
                            json =  gson.toJson(ds.getValue());
                            photos = new JSONObject(json);
                        }

                    }catch (Exception e){
                        Log.e("FirebaseInteractor","Se ha producido un error al obtener el datasnap");
                    }
                }
                generateCSVInDevice(infoUser,audios,states,photos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //TODO Hay que comprobar los permisos:
    private static void generateCSVInDevice(InfoUser infoUser, JSONObject audios, JSONObject states, JSONObject photos) {
        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/datos.csv"); // Here csv file name is MyCsvFile.csv

        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv));

            String[] header = new String[]{"Type","Date","Value"};
            data.add(header);

            if(states != null){
                addDataToCSV("states",states);
            }
            if(audios != null){
                addDataToCSV("audios",audios);
            }
            if(photos != null){
                addDataToCSV("photos",photos);
            }
            writer.writeAll(data); // data is adding to csv
            writer.close();

        } catch (IOException e) {
            Log.e(TAG,"Error al generar el CSV");
        }
    }

    private static void addDataToCSV(String typeOfData,JSONObject json){
        if(json != null){
            Iterator<String> iterator = json.keys();

            while (iterator.hasNext()){
                try{
                    String dateOfData = iterator.next();
                    String valueOfData  = json.get(dateOfData).toString();
                    data.add(new String[]{typeOfData,dateOfData,valueOfData});
                    Log.d(TAG,"Se ha generado el CSV correctamente");

                } catch (JSONException e) {
                    Log.e(TAG,"Error al extraer un dato del objeto JSON");
                }
            }
        }
    }

}
