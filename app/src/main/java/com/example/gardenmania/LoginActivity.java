package com.example.gardenmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getName();
    // ------------- GUI elemek -------------
    EditText emailLogin;
    EditText passwordLogin;
    // ------------- Firebase autentikáció -------------
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // ------------- GUI elemek -------------
        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        // ------------- Firebase autentikáció -------------
        mAuth = FirebaseAuth.getInstance();
    }


    // -------------- Bejelentkezés funkció lekezelése --------------
    public void login(View view){
        String email = emailLogin.getText().toString();
        String password = passwordLogin.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Sikeres bejelentkezés!", Toast.LENGTH_LONG).show();
                    successfulLogin();
                }else{
                    Log.d(LOG_TAG, "Sikertelen bejelentkezés!");
                    Toast.makeText(LoginActivity.this,"Sikertelen bejelentkezés!\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void successfulLogin(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        finish();
    }


    // -------------- Regisztrációs gomb funkciója --------------
    public void register(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    // -------------- Vissza gomb funkciója --------------
    public void back(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    // -------------- TextView adatainak lementése/visszaállítása --------------
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("emailLogin", emailLogin.getText().toString());  // save your instance
        savedInstanceState.putString("passwordLogin", passwordLogin.getText().toString());  // save your instance
        Log.i(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String email = savedInstanceState.getString("emailLogin"); //get it
        String password = savedInstanceState.getString("passwordLogin"); //get it
        Log.i(LOG_TAG, "onRestoreInstanceState");
        emailLogin.setText(email);
        passwordLogin.setText(password);
    }
}