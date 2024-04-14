package com.example.garage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class HomeActivity extends AppCompatActivity  {
    AnimatedBottomBar animatedBottomBar ;
    private List<Item> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.this,2));

        items = new ArrayList<>();
        items.add(new Item("Control garage door", R.drawable.door));
        items.add(new Item("Check spots availability", R.drawable.spots));
        items.add(new Item("Check Temperature", R.drawable.temp));
        items.add(new Item("Subscriptions", R.drawable.subscription));
        Gfeatures adapter = new Gfeatures(this, items);

        adapter.setOnItemClickListener(new Gfeatures.OnItemClickListener() {
            @Override
            public void onItemClick(Item clickedItem) {
                // Handle the item click here
                // You can access the clickedItem and perform any desired actions
                if (clickedItem.getName() == "Control garage door") {
                    Intent intent = new Intent(getApplicationContext(), Manage_Door.class);
                    startActivity(intent);
                } else if (clickedItem.getName() == "Check spots availability") {
                    Intent intent = new Intent(getApplicationContext(), Spot_Availability.class);
                    startActivity(intent);
                } else if (clickedItem.getName() == "Check Temperature") {
                    Intent intent = new Intent(getApplicationContext(), TemperatureActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getName() == "Subscriptions") {
                    Intent intent = new Intent(getApplicationContext(), Subscription.class);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
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
                }
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });

    }


}