package com.example.garage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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

public class ActivityLog extends AppCompatActivity {
    AnimatedBottomBar animatedBottomBar ;
    private List<Logs> logs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_log);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.logview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        logs = new ArrayList<>();
        logs.add(new Logs("Check spots availability", "12:00"));
        logs.add(new Logs("Open garage door", "12:04"));
        logs.add(new Logs("Close garage door", "12:05"));
        logs.add(new Logs("Check Temperature", "01:00"));
        logs.add(new Logs("Subscriptions", "02:00"));
        LogAdapter adapter = new LogAdapter(this, logs);

        recyclerView.setAdapter(adapter);
        EditText searc = (EditText) findViewById(R.id.search) ;
        searc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                cursor= databaseTrip.search(dp,searc.getText().toString());
//                if(cursor.getCount() > 0 ){
//                    name.clear();
//                    cursor.moveToFirst();
//                    do{
//                        ListItem listItem = new ListItem(nameFeild[0] + cursor.getString(0) ,
//                                nameFeild[1] + cursor.getString(1),
//                                nameFeild[2] + cursor.getString(2)  ,
//                                nameFeild[3] + cursor.getString(3) ,
//                                nameFeild[4] + cursor.getString(4) ,
//                                nameFeild[5] + cursor.getString(5) ,
//                                nameFeild[6] + cursor.getString(6),
//                                nameFeild[7] + cursor.getString(7));
//
//                        name.add(listItem);
//                    }while (cursor.moveToNext());
//                    adapter.notifyDataSetChanged();
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        animatedBottomBar = findViewById(R.id.bottom_bar);

        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                Intent intent ;

                if (tab1.getId() == R.id.tab_profile) {
                    intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    finish();
                    startActivity(intent);
                } else if (tab1.getId() == R.id.tab_home) {
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
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