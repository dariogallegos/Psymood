package com.example.psymood.Activities;

import android.content.Intent;

import com.example.psymood.Models.InfoUser;
import com.example.psymood.Preferences.ApplicationPreferences;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.psymood.Helpers.SnackbarHelper;
import com.example.psymood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String KEYNAME = "REMEMBER_ME";

    private EditText userMail, userPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private TextView linkRegister;
    private RadioButton rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase instance
        mAuth = FirebaseAuth.getInstance();

        //SharedPreferences instance
        ApplicationPreferences.init(getApplicationContext());

        //Form login
        userMail = findViewById(R.id.userMail);
        userPassword = findViewById(R.id.userPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        ImageView loginPhoto = findViewById(R.id.loginPhoto);
        linkRegister = findViewById(R.id.linkRegister);
        rememberMe = findViewById(R.id.buttonRememberMe);

        linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLogin.setVisibility(View.INVISIBLE);
                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    buttonLogin.setVisibility(View.VISIBLE);
                    showMessage("Por favor, verifica los campos vacios");
                } else {
                    signIn(mail, password);
                }
            }
        });

        //Load info user when log in
        loadInfoUser();

    }

    private void loadInfoUser() {
        if (ApplicationPreferences.loadInfoUser(KEYNAME)!= null) {
            // The user has a saved data in shared preferences, most likely the user has already logged in to psymood.
            InfoUser infoUser = ApplicationPreferences.loadInfoUser(KEYNAME);
            userMail.setText(infoUser.getEmailUser());
            userPassword.setText(infoUser.getPasswordUser());
        }
    }

    private void rememberMeUser(String mail, String password) {
        if (rememberMe.isChecked()) {
            InfoUser infoUser = new InfoUser(mail, password);
            ApplicationPreferences.saveInfoUser(KEYNAME, infoUser);
        }
    }


    //comprobacion del emial and password
    private void signIn(final String mail, final String password) {
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    rememberMeUser(mail, password);
                    updateUI();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthWeakPasswordException e) {
                        userPassword.setError(getString(R.string.error_weak_password));
                        //userPassword.requestFocus();

                    } catch (FirebaseAuthInvalidUserException e) {
                        userMail.setError(getString(R.string.error_invalid_email));
                        //userMail.requestFocus();

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        userPassword.setError(getString(R.string.error_invalid_password));
                        //userPassword.requestFocus();

                    } catch (Exception e) {
                        showMessage(e.getMessage());
                    }
                    //showMessage(task.getException().toString());
                    buttonLogin.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateUI() {
        Intent intent = new Intent(getApplicationContext(), NavigationHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String message) {
        View view = this.findViewById(R.id.coordinator_layout_login);
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        SnackbarHelper.configSnackbar(view.getContext(), snackbar);
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //El ususario se ha conetado previamente, le redirigiremos a la pagina de home
            updateUI();
        }
    }
}
