package com.example.garage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
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

import java.time.LocalTime;

import nl.joery.animatedbottombar.AnimatedBottomBar;

//import nl.joery.animatedbottombar.AnimatedBottomBar;

public class Manage_Door extends AppCompatActivity {
    Switch doorSwitch;
    Boolean IsClosed = false , isPass = false ;
    String email;
    AnimatedBottomBar animatedBottomBar ;
    final boolean[] isOrNO = new boolean[1];

    private static final String stamp = Long.toString(System.currentTimeMillis());//2
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_door);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });
        doorSwitch = findViewById(R.id.door_status_switch);

        if(check_connection()){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference emailRef = database.getReference("Spots");
            IsRegisteredOrNot(database , emailRef);
        }else if(check_connection() == false){
            Toast.makeText(Manage_Door.this, "Please Check your Network Connection!", Toast.LENGTH_SHORT).show();

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
                }else if (tab1.getId() == R.id.tab_home) {
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
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
        return networkInfo.isConnected();
    }

    private void IsRegisteredOrNot( FirebaseDatabase database ,  DatabaseReference doorRef ){
        doorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Toast.makeText(getApplicationContext(), " Network connected", Toast.LENGTH_SHORT).show();
                for (DataSnapshot spotSnapshot : snapshot.getChildren()) {
                    Spot_firebase spot = spotSnapshot.getValue(Spot_firebase.class);
                    assert spot != null;
                    if ( spot.email.equals(email) && spot.password.equals("true")) {
                        DatabaseReference doorRef = database.getReference("Door_status");
                        doorRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String value = dataSnapshot.getValue(String.class);

                                if (value.equalsIgnoreCase("Close")) {
                                    IsClosed = true;
                                    doorSwitch.setBackgroundResource(R.color.red);
                                    doorSwitch.setChecked(!(IsClosed));
                                    isPass = true ;
                                } else if (value.equalsIgnoreCase("open")) {
                                    IsClosed = false;
                                    doorSwitch.setBackgroundResource(R.color.teal_700);
                                    doorSwitch.setChecked(!(IsClosed));
                                    isPass = true ;
                                }
                                if (isPass) {
                                    doorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            String tag;
                                            if (isChecked) {
                                                Toast.makeText(getApplicationContext(), "The Door is open", Toast.LENGTH_SHORT).show();
                                                doorSwitch.setBackgroundResource(R.color.teal_700);
                                                doorRef.setValue("open"); // Update the door's status in Firebase
                                                SpotsDbHelper spotsDbHelper = new SpotsDbHelper(Manage_Door.this);
                                                Spot ss = spotsDbHelper.get_spot(email);
                                                tag = "you open door";
                                                Logs spotLog = new Logs(tag, stamp);
                                                LocalTime currentTime = LocalTime.now();
                                                spotLog.CreateLog(email, tag, String.valueOf(currentTime));
//                                            if (ss != null) {
//                                                updateSpotAvailabilityInFirebase(ss);
//                                                Toast.makeText(Manage_Door.this, "User" + email + " coming out", Toast.LENGTH_SHORT).show();
//                                                Clear_spot_firebase(ss);
//                                            }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "The Door is closed", Toast.LENGTH_SHORT).show();
                                                doorSwitch.setBackgroundResource(R.color.red);
                                                doorRef.setValue("Close"); // Update the door's status in Firebase
                                                tag = "you close door";
                                                Logs spotLog = new Logs(tag, stamp);
                                                LocalTime currentTime = LocalTime.now();
                                                spotLog.CreateLog(email, tag, String.valueOf(currentTime));
                                            }
                                        }
                                    });
                                    isPass = false ;
                                }else {
                                    Toast.makeText(Manage_Door.this, "Error", Toast.LENGTH_SHORT).show();
                                    doorSwitch.setBackgroundResource(R.color.gray);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }else{
                        doorSwitch.setBackgroundResource(R.color.gray);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Firebase ,Error reading values: ", Toast.LENGTH_SHORT).show();
            }
        });


    }
}