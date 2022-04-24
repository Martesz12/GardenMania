package com.example.gardenmania;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class ProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProfileActivity.class.getName();
    // ------------- Firebase autentikáció -------------
    private FirebaseUser user;
    // ------------- Menu kosár/kedvenc jelző -------------
    private FrameLayout redCircleCart;
    private TextView contentTextViewCart;
    private int cartItems = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // ------------- Firebase autentikáció -------------
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Létező user!");
        }else{
            Toast.makeText(ProfileActivity.this,"Jelentkezz be a funkció használatához!", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "Nem létező user!");
            finish();
        }
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
                return true;
            case R.id.search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.cart:
                if(user != null){
                    if(cartItems > 0){
                        cartItems = 0;
                        redCircleCart.setVisibility(GONE);
                        Toast.makeText(ProfileActivity.this,"Rendelésed leadtad!\nA kosár tartalma kiürült!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(ProfileActivity.this,"Nincs még termék a kosaradban!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ProfileActivity.this,"Jelentkezz be a funkció használatához!", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.favourite:
                intent = new Intent(this, FavouriteActivity.class);
                startActivity(intent);
                return true;
            case R.id.home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                if(user != null){
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(ProfileActivity.this,"Sikeres kijelentkezés!", Toast.LENGTH_LONG).show();
                    logout();
                }else{
                    Toast.makeText(ProfileActivity.this,"Nem vagy bejelentkezve!", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertCartMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootViewCart = (FrameLayout) alertCartMenuItem.getActionView();

        redCircleCart = (FrameLayout) rootViewCart.findViewById(R.id.view_alert_red_circle_cart);
        contentTextViewCart = (TextView) rootViewCart.findViewById(R.id.view_alert_count_textview_cart);

        rootViewCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onOptionsItemSelected(alertCartMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    //TODO elmenteni a cartItems számát és a betöltéskor megjeleníteni a számot
}