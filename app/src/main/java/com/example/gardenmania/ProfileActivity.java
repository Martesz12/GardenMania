package com.example.gardenmania;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProfileActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    // --------------- Felirat ---------------
    private TextView textViewProfile;
    // --------------- Profilkép készítéséhez szükséges kódok ---------------
    private static final int REQUEST_CODE_ASK_PERMISSION = 420;
    int tag = 1;
    ImageView imageViewProfile;
    Bundle pictureBundle;
    Bitmap imgProfile;
    String encoded;
    // ------------- Firebase autentikáció -------------
    private FirebaseUser user;
    // ------------- Menu kosár/kedvenc jelző -------------
    private FrameLayout redCircleCart;
    private TextView contentTextViewCart;
    private int cartItems = 0;
    // ------------- Különböző activity adatok betöltése/mentése -------------
    private SharedPreferences preferences;
    // ------------- Notification -------------
    private NotificationHandler mNotificationHandler;
    // ------------- Firestore -------------
    private FirebaseFirestore mFirestone;
    private CollectionReference mUsers;
    private User userData;

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
        // ------------- Különböző activity adatok betöltése/mentése -------------
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        if(preferences != null) {
            cartItems = preferences.getInt("cartItems", 0);
            encoded = preferences.getString("profilePicture", "");
        }
        // ------------- Notification -------------
        mNotificationHandler = new NotificationHandler(this);
        // ------------- Firestore -------------
        mFirestone = FirebaseFirestore.getInstance();
        mUsers = mFirestone.collection("Users");
        // ------------- Adatok beállítása -------------
        textViewProfile = findViewById(R.id.textViewProfile);
        queryData();

        // --------------- Profilkép készítéséhez szükséges kódok ---------------
        imageViewProfile = (ImageView)findViewById(R.id.imageViewProfile);
        if(!encoded.equals("")){
            byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
            imageViewProfile.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
    }

    // --------------- Megfelelő user adatainak betöltése ---------------
    private void queryData(){
        mUsers.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                User test = document.toObject(User.class);
                if(test.getEmail().trim().equals(user.getEmail().trim())){
                    userData = new User(test.getUsername(), test.getPassword(), test.getPhone(), test.getEmail(), test.getPicture());
                    textViewProfile.setText(userData.getUsername() + " profilja");
                }
            }
        });
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
                        //Toast.makeText(ProfileActivity.this,"Rendelésed leadtad!\nA kosár tartalma kiürült!", Toast.LENGTH_LONG).show();
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
                finish();
                return true;
            case R.id.home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
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
    // --------------- Profilkép lementése és betöltése ---------------
    protected void onPause() {
        super.onPause();

        if(imgProfile != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgProfile.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            encoded = Base64.encodeToString(b, Base64.DEFAULT);
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("profilePicture", encoded);
        editor.putInt("cartItems", cartItems);
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }


    // --------------- Profilkép készítése ---------------
    public void openCamera(View view){
        checkUserPermission();
    }

    void checkUserPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSION);
                return;
            }
        }
        takePicture();
    }

    public void takePicture(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, tag);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    takePicture();
                }else{
                    Toast.makeText(this, "Kérés elutasítva!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == tag && resultCode == RESULT_OK){
            pictureBundle = data.getExtras();
            imgProfile = (Bitmap) pictureBundle.get("data");
            imageViewProfile.setImageBitmap(imgProfile);
        }
    }

}