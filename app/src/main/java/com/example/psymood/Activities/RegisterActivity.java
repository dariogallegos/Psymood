package com.example.psymood.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.AnyRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.psymood.Helpers.SnackbarHelper;
import com.example.psymood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private ImageView regUserPhoto;
    static int PERMISSIONCODE = 1;
    static final int PICK_IMAGE = 100;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Uri pickedImageUri;
    private EditText userName, userMail, userPassword, userConfirm;
    private TextView linkLogIn;
    private Button buttonRegister;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Form to register user
        userName = findViewById(R.id.regName);
        userMail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userConfirm = findViewById(R.id.regConfirmPass);
        buttonRegister = findViewById(R.id.regButton);
        linkLogIn = findViewById(R.id.linkLogIn);

        //firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //Load image user
        regUserPhoto = findViewById(R.id.regUserPhoto);
        regUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >=22){
                    checkAndRequestForPermission();
                }
                else{ openGallery(); }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Podriamos poner algun efecto como una barra de progreso
                final String email = userMail.getText().toString();
                final String password = userPassword.getText().toString();
                final String confirm = userConfirm.getText().toString();
                final String name = userName.getText().toString();

                if(email.isEmpty() || name.isEmpty() || password.isEmpty() || confirm.isEmpty()){
                    //Something goes wrong, then we show a message with the error
                    showMessage("Por favor, verifica todos los campos");
                }
                else if(!password.equals(confirm)){
                    showMessage("Las contraseñas son distintas");
                }
                else{
                    //Everithing is ok and all fields is correct -> create a user account
                    buttonRegister.setVisibility(View.INVISIBLE);
                    createUserAccount(email,name,password);
                }
            }
        });

        linkLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    //Method to create a user account with email and password
    private void createUserAccount(String email, final String name, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //user account created successfully
                    showMessage("Cuenta creada");
                    updateUserInfo(name,pickedImageUri, mAuth.getCurrentUser());
                }
                else{
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthUserCollisionException e){
                        //If email already registered.
                        userMail.setError(getString(R.string.error_email_alredy_exist));

                    } catch (FirebaseAuthWeakPasswordException e) {
                        //The password is so weak
                        userPassword.setError(getString(R.string.error_weak_password));

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        //If email are in incorrect format
                        userPassword.setError(getString(R.string.error_invalid_password));

                    } catch (Exception e) {
                        showMessage(e.toString());
                    }
                    buttonRegister.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    //Method to updtade user photo and name
    private void updateUserInfo(final String name, Uri pickedImageUri, final FirebaseUser currentUser){
        //Lo primero es subir la foto a Firebase Storage y guardar la referencia de la imagen.
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");


        if(pickedImageUri == null){
            pickedImageUri = getUriToDrawable(getApplicationContext(),R.drawable.default_photo_profile);
        }

        //Guardamos la referencia en imafeFilePath
        //TODO aqui es donde asignamos el nombre que va a tener la photo al subir a firebase. Lo suyo seria cambiar los primeros digitos de
        //TODO de la foto para que se suban con el ide de usuario o algo para poder odernarlas.
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
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if(task.isSuccessful()){
                                    //user info update sucessfully
                                    showMessage("Register Complete");
                                    goToHome();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(getApplicationContext(), NavigationHomeActivity.class);
        startActivity(intent);
        finish();
    }

    //A simple method to show a toast
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    //Method to check permission and load a user picture
    private void checkAndRequestForPermission() {


        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this ,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(RegisterActivity.this,"Permitir a Psymood acceder a la galeria?",Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(RegisterActivity.this,PERMISSIONS_STORAGE, PERMISSIONCODE);
            }
        }
        else{
            openGallery();
        }
    }
    private void openGallery() {
        //TODO: open gallery intent and wait for user pick an image
       /* Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
        intentGallery.setType("+image/*");
        startActivityForResult(intentGallery, REQUESTCODE);*/

       Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
       startActivityForResult(intentGallery,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null){
            //  El usuario a seleccionado una imagen correctamente
            //  Lo que ahora necesitamos el guardar la referencia en un Uri variable
            pickedImageUri = data.getData();
            regUserPhoto.setImageURI(pickedImageUri);
        }
    }

    public static final Uri getUriToDrawable(@NotNull Context context,@AnyRes int drawableId) {

        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId) );
        return imageUri;
    }
}
