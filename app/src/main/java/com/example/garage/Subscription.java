package com.example.garage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class Subscription extends AppCompatActivity {
    AnimatedBottomBar animatedBottomBar ;

    TextView subscriptionTextView , spotTextView ;
    SubscriptionDbHelper subscriptionDbHelper ;
    String mail , price = "0$" , spot = "0" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subscription);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        mail = sharedPreferences.getString("email", "");
        subscriptionTextView = findViewById(R.id.TotalPrice);
        spotTextView = findViewById(R.id.placeParking);
        subscriptionDbHelper = new SubscriptionDbHelper(getApplicationContext()) ;
        mail = mail.replaceAll(".com" , "");
        if(check_connection()) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

//            Toast.makeText(getApplicationContext(), mail, Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(), ref.getKey() , Toast.LENGTH_SHORT).show();
            DatabaseReference ref = database.getReference("subscription");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Toast.makeText(getApplicationContext(), " Network connected", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), ref.getKey() , Toast.LENGTH_SHORT).show();

                    Price_Spots spots = snapshot.child(mail).getValue(Price_Spots.class);
                    price = spots.getPrice();
//                    Toast.makeText(getApplicationContext(), spots.toString() , Toast.LENGTH_SHORT).show();
                    spot = spots.getSpot_id();
                    if(!price.equals(" ") ) {
                        price = "Total Price : " + price + "$ 👌";
                        subscriptionTextView.setText(price);
                        spot = "You stop at "+ spot +" spot" ;
                        spotTextView.setText(spot);
                        Cursor cursor = subscriptionDbHelper.getAllSpots(mail);
                        if(cursor.getCount() == 0 ){
                           long r =  subscriptionDbHelper.addSpot(spots.getSpot_id() , mail , spots.getPrice()) ;
//                            Toast.makeText(getApplicationContext(), "Please " +r, Toast.LENGTH_SHORT).show();
                        }else {
                            boolean flag= subscriptionDbHelper.updateSpot(spots.getSpot_id() , mail , spots.getPrice()) ;
//                            Toast.makeText(getApplicationContext(), "Please " + flag, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        price = "Total Price : 0$ 👌";
                        subscriptionTextView.setText(price);
                        spot ="In which spot did you park 🤔";
                        spotTextView.setText(spot);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Firebase ,Error reading values: ", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Cursor cursor = subscriptionDbHelper.getAllSpots(mail);
//            Toast.makeText(getApplicationContext(), "Please Connection!" + cursor.getCount(), Toast.LENGTH_SHORT).show();
            if(cursor.getCount() != 0 ){
                cursor.moveToFirst();
                price = "Total Price : " + cursor.getString(2) + " 👌";
                subscriptionTextView.setText(price);
                spot = "You stop at "+ cursor.getString(0) +" spot" ;
                spotTextView.setText(spot);
            }
            Toast.makeText(getApplicationContext(), "Please Check your Network Connection!", Toast.LENGTH_SHORT).show();

        }
        animatedBottomBar = findViewById(R.id.bottom_bar);

        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                Intent intent ;

                if (tab1.getId() == R.id.tab_profile) {
                    intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    finish();
                    startActivity(intent);
                } else if (tab1.getId() == R.id.tab_cart) {
                    intent = new Intent(getApplicationContext(), ActivityLog.class);
                    finish();
                    startActivity(intent);
                }
                else if (tab1.getId() == R.id.tab_logout) {
                    SharedPreferences preferences = getSharedPreferences("checkedbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.putString("email", "");
                    editor.apply();

                    intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                    startActivity(intent);
                }else if (tab1.getId() == R.id.tab_home) {
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
                    finish();
                    startActivity(intent);
                }
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });

    }
    public Boolean check_connection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null){
            return false;
        }
        return true;
    }
}