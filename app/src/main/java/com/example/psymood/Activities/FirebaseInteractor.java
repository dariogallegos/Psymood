package com.example.psymood.Activities;

import android.util.Log;

import com.example.psymood.Models.InfoUser;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FirebaseInteractor {

    private static DatabaseReference myRef;
    private static FirebaseUser myCurrentUser;

    public static void initUI(FirebaseUser currentUser) {

        if (myRef == null) {
            myRef = FirebaseDatabase.getInstance().getReference("InfoUser");
            myCurrentUser = currentUser;
        }


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue();
                Log.d("FirebaseInteractor", "Value is: " + "ha habido un cambio");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("FirebaseInteractor", "Failed to read value.", error.toException());
            }
        });

    }

    public static void createInfoUserInDatabase() {


        //TODO comprobacion si el usuario ya tiene informacion o si se acaba de registrar y los campos estan vacios.
        //TODO : chequear si ya existe, si es asi solo traemos la referencia, nada mas.


        InfoUser infoUser = new InfoUser(myCurrentUser.getDisplayName(),myCurrentUser.getEmail(),myCurrentUser.getPhotoUrl().toString());
        myRef.child(myCurrentUser.getUid()).setValue(infoUser);

    }

    public static void saveAudioInDatabase(String urlAudio){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        DatabaseReference myAudioRef = myRef.child(myCurrentUser.getUid()).child("audiosUser").child(currentDateandTime);
        myAudioRef.setValue(urlAudio);
    }

    public static void saveMoodStateInDatabase(String typeState, String valueState){

        DatabaseReference myStateRef =  myRef.child(myCurrentUser.getUid()).child("stateUser").child(typeState);
        myStateRef.setValue(valueState);

    }


}
