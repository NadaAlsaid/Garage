package com.example.garage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;

import nl.joery.animatedbottombar.AnimatedBottomBar;

//import nl.joery.animatedbottombar.AnimatedBottomBar;

public class Spot_Availability extends AppCompatActivity {
    RecyclerView available_Spots_list;
    ArrayList<Spot>offline_spots_list = new ArrayList<>();
    ArrayList<Spot> online_spots_list = new ArrayList<>();
    AnimatedBottomBar animatedBottomBar ;
    int userID;
    //mariam added lines 1-2

    //    String mail=getIntent().getStringExtra("mail");
    String mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spot_availability);
//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
//        if (extras != null) {
//            userID = extras.getInt("user_id", 0);  // -1 is a default value if "user_id" is not found
//            mail = extras.getString("mail","oyara392");
//            Toast.makeText(Spot_Availability.this, "user " +userID, Toast.LENGTH_SHORT).show();
//        }
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        mail = sharedPreferences.getString("email", "");
//        Toast.makeText(Spot_Availability.this, "user " +mail, Toast.LENGTH_SHORT).show();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        available_Spots_list = findViewById(R.id.spot_list_rv);
        SpotsDbHelper spotsDbHelper = new SpotsDbHelper(Spot_Availability.this);
        if(check_connection()){
            FirebaseDatabase  database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("Spots");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Toast.makeText(Spot_Availability.this, " Network connected", Toast.LENGTH_SHORT).show();
                    spotsDbHelper.deleteAllSpots();
                    for (DataSnapshot spotSnapshot : snapshot.getChildren()) {
                        Spot_firebase spot = spotSnapshot.getValue(Spot_firebase.class);
                        Spot spot_normal = new Spot();
                        spot_normal.setSpot_id(Integer.parseInt(spot.spot_id));
                        if (spot != null && spot.is_available != null) {
                            if (spot.is_available.equalsIgnoreCase("true")) {
                                spot_normal.setSpot_availablility(true);

                            } else if (spot.is_available.equalsIgnoreCase("false")) {
                                spot_normal.setSpot_availablility(false);

                            } else {
                                // Handle unexpected values (optional)
                                spot_normal.setSpot_availablility(false);  // Set a default value (optional)
                                Toast.makeText(Spot_Availability.this, "spot "+spot_normal.getSpot_id()+"Has problem in availability",Toast.LENGTH_SHORT).show();
                            }
                            spot_normal.setUser_id(spot.email);
                            spot_normal.setTime_in(spot.time_in);
                            spot_normal.setTime_out(spot.time_out);
                            online_spots_list.add(spot_normal);
                            long test = spotsDbHelper.addSpot(spot_normal.getSpot_id() , spot_normal.getUser_id() , spot_normal.isSpot_availablility(), spot_normal.getTime_in() , spot_normal.getTime_out());
//                            Toast.makeText(Spot_Availability.this, "spot"+test,Toast.LENGTH_SHORT).show();
                        }
                    }

                    offline_spots_list= spotsDbHelper.getAllSpots();
                    Spot_adapter adapter = new Spot_adapter(Spot_Availability.this,offline_spots_list,mail,true);
                    RecyclerView.LayoutManager layoutmanager = new GridLayoutManager(Spot_Availability.this,2);
                    available_Spots_list.setHasFixedSize(true);
                    available_Spots_list.setLayoutManager(layoutmanager);
                    available_Spots_list.setAdapter(adapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Spot_Availability.this, "Firebase ,Error reading values: ", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(Spot_Availability.this, "Please Check your Network Connection!", Toast.LENGTH_SHORT).show();

            offline_spots_list= spotsDbHelper.getData();

            Spot_adapter adapter = new Spot_adapter(Spot_Availability.this,offline_spots_list,mail,check_connection());
            RecyclerView.LayoutManager layoutmanager = new GridLayoutManager(Spot_Availability.this,2);
            available_Spots_list.setHasFixedSize(true);
            available_Spots_list.setLayoutManager(layoutmanager);
            available_Spots_list.setAdapter(adapter);
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
