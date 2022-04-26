package com.example.gardenmania;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    // ------------- Firebase autentikáció -------------
    private FirebaseUser user;
    private TextView welcomeText;
    private TextView welcomeDescription;
    private TextView welcomeLogin;
    // ------------- Menu kosár/kedvenc jelző -------------
    private FrameLayout redCircleCart;
    private TextView contentTextViewCart;
    private int cartItems = 0;
    // ------------- Különböző activity adatok betöltése/mentése -------------
    private SharedPreferences preferences;
    // ------------- Notification -------------
    private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ------------- Firebase autentikáció -------------
        user = FirebaseAuth.getInstance().getCurrentUser();
        // ------------- Text-ek animálása -------------
        welcomeText = findViewById(R.id.welcomeText);
        welcomeDescription = findViewById(R.id.welcomeDescription);
        welcomeLogin = findViewById(R.id.welcomeLogin);
        welcomeText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in));
        welcomeDescription.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in));
        welcomeLogin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in));
        // ------------- Különböző activity adatok betöltése/mentése -------------
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        if(preferences != null) {
            cartItems = preferences.getInt("cartItems", 0);
        }
        // ------------- Notification -------------
        mNotificationHandler = new NotificationHandler(this);
    }


    // --------------- Menu ---------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.login:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.cart:
                if(user != null){
                    if(cartItems > 0){
                        cartItems = 0;
                        redCircleCart.setVisibility(GONE);
                        mNotificationHandler.send("Rendelésed leadtad! A kosár tartalma kiürült!");
                        //Toast.makeText(MainActivity.this,"Rendelésed leadtad!\nA kosár tartalma kiürült!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this,"Nincs még termék a kosaradban!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Jelentkezz be a funkció használatához!", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.favourite:
                intent = new Intent(this, FavouriteActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                if(user != null){
                    intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this,"Nem vagy bejelentkezve!", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.logout:
                if(user != null){
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(MainActivity.this,"Sikeres kijelentkezés!", Toast.LENGTH_LONG).show();
                    logout();
                }else{
                    Toast.makeText(MainActivity.this,"Nem vagy bejelentkezve!", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertCartMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootViewCart = (FrameLayout) alertCartMenuItem.getActionView();

        redCircleCart = (FrameLayout) rootViewCart.findViewById(R.id.view_alert_red_circle_cart);
        contentTextViewCart = (TextView) rootViewCart.findViewById(R.id.view_alert_count_textview_cart);

        loadCartAlertIcon();

        rootViewCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onOptionsItemSelected(alertCartMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    // --------------- Kosár darabszám jelző ---------------
    public void loadCartAlertIcon(){
        if(user != null){
            if(0 < cartItems){
                contentTextViewCart.setText(String.valueOf(cartItems));
            }else{
                contentTextViewCart.setText("");
            }
            redCircleCart.setVisibility((cartItems > 0) ? VISIBLE : GONE);
        }
    }

    // --------------- Kosár dbszámának lementése ---------------
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("cartItems", cartItems);
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }
}