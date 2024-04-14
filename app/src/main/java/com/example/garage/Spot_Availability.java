package com.example.garage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class Spot_Availability extends AppCompatActivity {
    RecyclerView available_Spots_list;
    AnimatedBottomBar animatedBottomBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spot_availability);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        available_Spots_list = findViewById(R.id.spot_list_rv);
        ArrayList<Spot> spots_list= new ArrayList<>();
        Spot s1 = new Spot();
        Spot s2 = new Spot();
        Spot s3 = new Spot();
        Spot s4 = new Spot();
        Spot s5 = new Spot();
        Spot s6 = new Spot();
        s1.setSpot_id(1);
        s1.setSpot_availablility(true);
        spots_list.add(s1);

        s2.setSpot_id(2);
        s2.setSpot_availablility(false);
        spots_list.add(s2);

        s3.setSpot_id(3);
        s3.setSpot_availablility(false);

        spots_list.add(s3);

        s4.setSpot_id(4);
        s4.setSpot_availablility(true);
        spots_list.add(s4);

        s5.setSpot_id(5);
        s5.setSpot_availablility(true);
        spots_list.add(s5);

        s6.setSpot_id(6);
        s6.setSpot_availablility(true);
        spots_list.add(s6);

        Spot_adapter adapter = new Spot_adapter(spots_list);
        RecyclerView.LayoutManager layoutmanager = new GridLayoutManager(this,2);
        available_Spots_list.setHasFixedSize(true);
        available_Spots_list.setLayoutManager(layoutmanager);
        available_Spots_list.setAdapter(adapter);
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
}