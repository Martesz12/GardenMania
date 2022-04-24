package com.example.gardenmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();

    // ------------- GUI elemek -------------
    EditText usernameRegister;
    EditText passwordRegister;
    EditText passwordAgainRegister;
    EditText phoneRegister;
    EditText emailRegister;

    // ------------- Firebase autentikáció -------------
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ------------- GUI elemek -------------
        usernameRegister = findViewById(R.id.usernameRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        passwordAgainRegister = findViewById(R.id.passwordAgainRegister);
        phoneRegister = findViewById(R.id.phoneRegister);
        emailRegister = findViewById(R.id.emailRegister);

        // ------------- Firebase autentikáció -------------
        mAuth = FirebaseAuth.getInstance();
    }

    public void register(View view){
        // ------------- Adatok lekérése, ellenőrzése -------------
        String username = usernameRegister.getText().toString();
        String password = passwordRegister.getText().toString();
        String passwordAgain = passwordAgainRegister.getText().toString();
        String phone = phoneRegister.getText().toString();
        String email = emailRegister.getText().toString();
        if (!password.equals(passwordAgain)) {
            Log.e(LOG_TAG, "Nem egyenlő a jelszó és a megerősítése.");
            return;
        }

        // ------------- Firebase-s mókolás -------------
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Sikeres regisztráció!", Toast.LENGTH_LONG).show();
                    successfulRegister();
                }else{
                    Log.d(LOG_TAG, "Sikertelen regisztráció!");
                    Toast.makeText(RegisterActivity.this,"Sikertelen regisztráció!\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        Log.i(LOG_TAG, "Regisztrált: " + username + " " + email);
    }

    private void successfulRegister(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void back(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}