package com.example.gardenmania;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private static final String LOG_TAG = SearchActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    // ------------- Firebase autentikáció -------------
    private FirebaseUser user;
    // ------------- Item kilistázós dolgok -------------
    private RecyclerView mRecycleView;
    private ArrayList<ShoppingItem> mItemList;
    private ShoppingItemAdapter mAdapter;
    private int gridNumber = 1;
    // ------------- Menu kosár/kedvenc jelző -------------
    private FrameLayout redCircleCart;
    private TextView contentTextViewCart;
    private int cartItems = 0;
    // ------------- Kedvenc Set -------------
    Set<String> favouriteSet;
    // ------------- Különböző activity adatok betöltése/mentése -------------
    private SharedPreferences preferences;
    // ------------- Notification -------------
    private NotificationHandler mNotificationHandler;
    // ------------- Firestore -------------
    private FirebaseFirestore mFirestone;
    private CollectionReference mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // ------------- Firebase autentikáció -------------
        user = FirebaseAuth.getInstance().getCurrentUser();

        // ------------- Item kilistázós dolgok -------------
        mRecycleView = findViewById(R.id.recycleView);
        mRecycleView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();
        mAdapter = new ShoppingItemAdapter(this, mItemList);
        mRecycleView.setAdapter(mAdapter);
        // ------------- Firestore -------------
        mFirestone = FirebaseFirestore.getInstance();
        mItems = mFirestone.collection("Items");
        queryData();
        // initalizeData();
        // ------------- Különböző activity adatok betöltése/mentése -------------
        favouriteSet = new HashSet<String>();
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        if(preferences != null) {
            favouriteSet = preferences.getStringSet("favouriteSet", new HashSet<String>());
            cartItems = preferences.getInt("cartItems", 0);
        }
        // ------------- Notification -------------
        mNotificationHandler = new NotificationHandler(this);
    }


    // --------------- Item list feltöltése ---------------
    private void queryData(){
        mItemList.clear();
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);

        mItems.orderBy("name").limit(15).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                ShoppingItem item = document.toObject(ShoppingItem.class);
                mItemList.add(item);
            }
            if(mItemList.size() == 0){
                Log.i(LOG_TAG, "Volt 0 a cucc");
                initalizeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    private void initalizeData(){
        String[] itemsList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources().getStringArray(R.array.shopping_item_infos);
        String[] itemsPrice = getResources().getStringArray(R.array.shopping_item_prices);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);

        // mItemList.clear();
        for (int i = 0; i < itemsList.length; i++) {
            mItems.add(new ShoppingItem(itemsList[i], itemsInfo[i], itemsPrice[i], itemsImageResource.getResourceId(i,0)));
            //mItemList.add(new ShoppingItem(itemsList[i], itemsInfo[i], itemsPrice[i], itemsImageResource.getResourceId(i,0)));
        }

        itemsImageResource.recycle();
        // mAdapter.notifyDataSetChanged();
    }


    // --------------- Menu ---------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        MenuItem item;
        if(user != null){
            item = menu.findItem(R.id.login);
            item.setVisible(false);
        }else{
            item = menu.findItem(R.id.cart);
            item.setVisible(false);
            item = menu.findItem(R.id.favourite);
            item.setVisible(false);
            item = menu.findItem(R.id.profile);
            item.setVisible(false);
            item = menu.findItem(R.id.logout);
            item.setVisible(false);
        }

        // -------- Keresősáv --------
        SearchView searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
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
            case R.id.cart:
                if(user != null){
                    if(cartItems > 0){
                        cartItems = 0;
                        redCircleCart.setVisibility(GONE);
                        mNotificationHandler.send("Rendelésed leadtad! A kosár tartalma kiürült!");
                        // Toast.makeText(SearchActivity.this,"Rendelésed leadtad!\nA kosár tartalma kiürült!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(SearchActivity.this,"Nincs még termék a kosaradban!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(SearchActivity.this,"Jelentkezz be a funkció használatához!", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.favourite:
                intent = new Intent(this, FavouriteActivity.class);
                startActivity(intent);
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
                    Toast.makeText(SearchActivity.this,"Nem vagy bejelentkezve!", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.logout:
                if(user != null){
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(SearchActivity.this,"Sikeres kijelentkezés!", Toast.LENGTH_LONG).show();
                    favouriteSet = null;
                    cartItems = 0;
                    logout();
                }else{
                    Toast.makeText(SearchActivity.this,"Nem vagy bejelentkezve!", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(){
        Intent intent = new Intent(this, SearchActivity.class);
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

    public void updateCartAlertIcon(){
        if(user != null){
            cartItems = (cartItems + 1);
            if(0 < cartItems){
                contentTextViewCart.setText(String.valueOf(cartItems));
            }else{
                contentTextViewCart.setText("");
            }
            redCircleCart.setVisibility((cartItems > 0) ? VISIBLE : GONE);
        }else{
            Toast.makeText(SearchActivity.this,"Jelentkezz be a funkció használatához!", Toast.LENGTH_LONG).show();
        }
    }

    // -------------- Kosár dbszámának lementése/visszaállítása --------------
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("cartItems", cartItems);  // save your instance
        Log.i(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int cart = savedInstanceState.getInt("cartItems"); //get it
        Log.i(LOG_TAG, "onRestoreInstanceState");
        cartItems = cart;
    }

    // --------------- Kedvencek hozzáadása egy Set-hez ---------------
    public void addToFavourite(String item){
        if(user != null){
            favouriteSet.add(item);
            Toast.makeText(SearchActivity.this,"Hozzáadtad a kedvencekhez!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(SearchActivity.this,"Jelentkezz be a funkció használatához!", Toast.LENGTH_LONG).show();
        }
    }

    // --------------- Kosár dbszámának lementése ---------------
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("favouriteSet", favouriteSet);
        editor.putInt("cartItems", cartItems);
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }
}