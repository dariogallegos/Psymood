package com.example.psymood.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.psymood.Helpers.SnackbarHelper;
import com.example.psymood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail,userPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private ImageView loginPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase instace
        mAuth = FirebaseAuth.getInstance();

        //Form login
        userMail = findViewById(R.id.userMail);
        userPassword = findViewById(R.id.userPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        loginPhoto = findViewById(R.id.loginPhoto);

        loginPhoto.setOnClickListener(new View.OnClickListener() {
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

                if(mail.isEmpty() || password.isEmpty()){
                    buttonLogin.setVisibility(View.VISIBLE);
                    showMessage("Por favor, verifica todos los campos");
                }else{
                    signIn(mail,password);
                }

            }
        });
    }

    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    updateUI();
                }
                else {
                    showMessage(task.getException().toString());
                }
            }
        });
    }

    private void updateUI() {
        Intent intent = new Intent(getApplicationContext(), NavigationHomeActivity.class);
        startActivity(intent);
        //finish();
    }

    private void showMessage(String message) {
        View view = this.findViewById(R.id.coordinator_layout_login);
        Snackbar snackbar = Snackbar.make(view, message,Snackbar.LENGTH_LONG);
        SnackbarHelper.configSnackbar(view.getContext(),snackbar);
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            //El ususario se ha conetado previamente, le redirigiremos a la pagina de home
            updateUI();
        }
    }
}
