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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.Set;

public class FavouriteActivity extends AppCompatActivity {
    private static final String LOG_TAG = FavouriteActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    // ------------- Firebase autentikáció -------------
    private FirebaseUser user;
    // ------------- Kedvenc Set -------------
    Set<String> favouriteSet;
    // ------------- Különböző activity adatok betöltése/mentése -------------
    private SharedPreferences preferences;
    TextView kedvencek_list;
    // ------------- Menu kosár/kedvenc jelző -------------
    private FrameLayout redCircleCart;
    private TextView contentTextViewCart;
    private int cartItems = 0;
    // ------------- Notification -------------
    private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        // ------------- Firebase autentikáció -------------
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Létező user!");
        }else{
            Toast.makeText(FavouriteActivity.this,"Jelentkezz be a funkció használatához!", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "Nem létező user!");
            finish();
        }
        // ------------- Különböző activity adatok betöltése/mentése -------------
        favouriteSet = new HashSet<String>();
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        if(preferences != null) {
            favouriteSet = preferences.getStringSet("favouriteSet", null);
            cartItems = preferences.getInt("cartItems", 0);
        }
        kedvencek_list = findViewById(R.id.kedvencek_list);
        String lista = "";
        for(String item : favouriteSet){
            lista = lista + item + "\n";
        }
        kedvencek_list.setText(lista);
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
                        //Toast.makeText(FavouriteActivity.this,"Rendelésed leadtad!\nA kosár tartalma kiürült!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(FavouriteActivity.this,"Nincs még termék a kosaradban!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(FavouriteActivity.this,"Jelentkezz be a funkció használatához!", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.profile:
                if(user != null){
                    intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(FavouriteActivity.this,"Nem vagy bejelentkezve!", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.logout:
                if(user != null){
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(FavouriteActivity.this,"Sikeres kijelentkezés!", Toast.LENGTH_LONG).show();
                    logout();
                }else{
                    Toast.makeText(FavouriteActivity.this,"Nem vagy bejelentkezve!", Toast.LENGTH_LONG).show();
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