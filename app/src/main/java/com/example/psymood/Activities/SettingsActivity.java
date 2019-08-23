package com.example.psymood.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.psymood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.psymood.Activities.RegisterActivity.getUriToDrawable;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private EditText settingsName, settingsEmail;
    private Button sign_out, save_info_user;
    private ImageView settingsPhoto;

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

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        initValuesOfForm(currentUser);

        settingsPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        save_info_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameUser = settingsName.getText().toString();

                if (currentUser != null && !nameUser.isEmpty())
                    updateInfoUserIntoFirebase(nameUser, photoProfileUri,currentUser);
                else
                    Toast.makeText(getApplicationContext(), "Por favor revisa lo campos", Toast.LENGTH_SHORT).show();
            }
        });



        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user pick an image

        Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intentGallery, PICK_IMAGE);
    }


    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "Permitir a Psymood acceder a la galeria?", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(SettingsActivity.this, PERMISSIONS_STORAGE, PERMISSION_CODE);
            }
        } else {
            openGallery();
        }
    }


    private void updateInfoUserIntoFirebase(final String nameUser, Uri pickedImageUri , final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");

        if(pickedImageUri == null){
            pickedImageUri = getUriToDrawable(getApplicationContext(),R.drawable.default_photo_profile);
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
                                    showMessage("Register Complete");
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


    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
