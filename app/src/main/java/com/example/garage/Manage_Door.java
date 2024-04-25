package com.example.garage;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import nl.joery.animatedbottombar.AnimatedBottomBar;
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

//import nl.joery.animatedbottombar.AnimatedBottomBar;

public class Manage_Door extends AppCompatActivity {
    Switch doorSwitch;
    Boolean IsClosed = false;
    AnimatedBottomBar animatedBottomBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            DatabaseReference doorRef = database.getReference("Door_status");
            doorRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String value = dataSnapshot.getValue(String.class);
                    if(value.equalsIgnoreCase("Close")){
                        IsClosed =true;
                        doorSwitch.setBackgroundResource(R.color.red);
                        doorSwitch.setChecked(!(IsClosed));
                    }else if (value.equalsIgnoreCase("open")){
                        IsClosed = false;
                        doorSwitch.setBackgroundResource(R.color.teal_700);
                        doorSwitch.setChecked(!(IsClosed));
                    }else {
                        Toast.makeText(Manage_Door.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    doorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                Toast.makeText(getApplicationContext(), "The Door is open", Toast.LENGTH_SHORT).show();
                                doorSwitch.setBackgroundResource(R.color.teal_700);
                                doorRef.setValue("open"); // Update the door's status in Firebase

                            }else{
                                Toast.makeText(getApplicationContext(), "The Door is closed", Toast.LENGTH_SHORT).show();
                                doorSwitch.setBackgroundResource(R.color.red);
                                doorRef.setValue("Close"); // Update the door's status in Firebase
                            }
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
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
}
