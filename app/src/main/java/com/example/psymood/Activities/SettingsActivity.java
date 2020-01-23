package com.example.psymood.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.example.psymood.Fragments.DatePickerFragment;
import com.example.psymood.Helpers.SnackbarHelper;
import com.example.psymood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.psymood.Activities.RegisterActivity.getUriToDrawable;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private EditText settingsName, settingsEmail, settingsCalendar;
    private Button sign_out, save_info_user,settings_download_data;
    private ImageView settingsPhoto;
    private RadioGroup gender_options;
    private String gender = "";
    private String selectedDate = "";

    Uri photoProfileUri;
    private static final int PERMISSION_CODE = 1;
    private static final int PICK_IMAGE = 100;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbarBack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsName = findViewById(R.id.settingsName);
        settingsEmail = findViewById(R.id.settingsEmail);
        settingsPhoto = findViewById(R.id.settingsPhoto);
        sign_out = findViewById(R.id.settings_sing_out);
        save_info_user = findViewById(R.id.settingsSave);
        settings_download_data = findViewById(R.id.settings_download_data);
        gender_options = findViewById(R.id.gender_options);
        settingsCalendar = findViewById(R.id.settingsCalendar);

        TextView settings_name_title = findViewById(R.id.settings_name_title);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        initValuesOfForm(currentUser);
        settings_name_title.setText(currentUser.getDisplayName());

        settingsCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.settingsCalendar:
                        showDatePickerDialog();
                        break;
                }
            }
        });

        settingsPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkAndRequestForPermission("photo");
                } else {
                    openGallery();
                }
            }
        });

        //TODO CORREGIR LA LINEA 119

        save_info_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameUser = settingsName.getText().toString();

                if (currentUser != null && !nameUser.isEmpty())
                    updateInfoUserIntoFirebase(nameUser, photoProfileUri,currentUser);
                else
                    Toast.makeText(getApplicationContext(), "Por favor revisa lo campos", Toast.LENGTH_SHORT).show();

                checkGender();
                if (gender != null) {
                    storeGender(gender);
                }

                if (selectedDate != null) {
                    storeCalendar(selectedDate);
                }
            }
        });

        settings_download_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadData();
            }
        });

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void checkGender(){
        if (gender_options.getCheckedRadioButtonId() ==  R.id.radio_male){
            Log.e("Gender:male", "Gender is male");
            gender = "male";
        } else{
            Log.e("Gender:male", "Gender is female");
            gender = "female";
        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                selectedDate = day + " / " + (month+1) + " / " + year;
                settingsCalendar.setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void openGallery() {
        Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intentGallery, PICK_IMAGE);
    }


    private void checkAndRequestForPermission(String action) {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SettingsActivity.this,PERMISSIONS_STORAGE,PERMISSION_CODE);
        } else {
            if(action.equals("photo")){
                openGallery();
            }else{
                FirebaseInteractor.downloadData();
            }
        }
    }

    private void updateInfoUserIntoFirebase(final String nameUser, Uri pickedImageUri , final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("profile_user_photos");

        if(pickedImageUri == null){
            pickedImageUri = getUriToDrawable(getApplicationContext(),R.drawable.ic_astronaut_profile_grey);
        }
        final StorageReference imageFilePath = mStorage.child(pickedImageUri.getLastPathSegment());

        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image upload succesfully.
                //Now we can get our image url
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Uri contain user image url

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nameUser)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if(task.isSuccessful()){
                                    //user info update sucessfully
                                    showMessageInSettings("Se ha guardado correctamente");
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    private void initValuesOfForm(FirebaseUser currentUser) {

        if (currentUser != null) {
            Glide.with(this).load(currentUser.getPhotoUrl()).placeholder(R.drawable.ic_astronaut_profile_grey).into(settingsPhoto);
            settingsName.setText(currentUser.getDisplayName());
            settingsEmail.setText(currentUser.getEmail());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            //  El usuario a seleccionado una imagen correctamente
            //  Lo que ahora necesitamos el guardar la referencia en un Uri variable
            photoProfileUri = data.getData();
            Log.e("SettingsActivity", photoProfileUri.toString());
            settingsPhoto.setImageURI(photoProfileUri);
        }
    }

    private void downloadData(){
        Log.e("FirebaseInteractor","He entrado en el downloadData del boton");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                FirebaseInteractor.downloadData();
                showMessageInSettings("Se ha generado el CSV");
            }else{
                checkAndRequestForPermission("csv");
            }
        } else {
            FirebaseInteractor.downloadData();
            showMessageInSettings("Se ha generado el CSV");
        }

    }


    private void storeGender(final String gender){
        Log.e("storingGender","Storing gender in database");
        FirebaseInteractor.saveGenderInDatabase(gender);
    }

    private void storeCalendar(final String date){
        Log.e("storingCalendar","Storing calendar in database");
        FirebaseInteractor.saveDateInDatabase(date);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void showMessageInSettings(String message) {
        try {
            View view = this.findViewById(R.id.settings_activity);
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            SnackbarHelper.configSnackbar(view.getContext(), snackbar);
            snackbar.show();
        }catch (Exception e){
            Log.e("SettingsActivity","Error el mostrar el mensaje de CSV");
        }
    }
}
