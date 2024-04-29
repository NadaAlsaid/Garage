package com.example.garage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
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

import java.util.ArrayList;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class ActivityLog extends AppCompatActivity {
    AnimatedBottomBar animatedBottomBar ;
    ArrayList<Logs> logs;
    LogAdapter adapter;
    String mail;
    LogSQL logger ;
    SQLiteDatabase sqLiteDatabase;
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

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext() , 1));
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        mail = sharedPreferences.getString("email", "");
//        logs.add(new Logs("Check spots availability", "12:00"));
//        logs.add(new Logs("Open garage door", "12:04"));
//        logs.add(new Logs("Close garage door", "12:05"));
//        logs.add(new Logs("Check Temperature", "01:00"));
//        logs.add(new Logs("Subscriptions", "02:00"));
        logs = new ArrayList<>();
        logger = new LogSQL(getApplicationContext());
        sqLiteDatabase = logger.getWritableDatabase();
        if(check_connection()){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("Logs");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        logger.deleteAlllogs();
                        Toast.makeText(ActivityLog.this, "Network connected", Toast.LENGTH_SHORT).show();
                        for (DataSnapshot spotSnapshot : snapshot.getChildren()) {
                            log_firebase log_fire = spotSnapshot.getValue(log_firebase.class);
                            if (log_fire.email.equals(mail)) {
                                Logs log = new Logs(log_fire.email, log_fire.action, log_fire.timeStamp);

                                logs.add(log);

                                long test = logger.insertEvent(log.getEmail(), log.getAction(), log.getTimeStamp());

                                Toast.makeText(ActivityLog.this,  ""+test, Toast.LENGTH_SHORT).show();

                            }
                        }
                        adapter = new LogAdapter(getApplicationContext(), logs);
//                        Toast.makeText(ActivityLog.this, "No of logs = "+logs.size(), Toast.LENGTH_SHORT).show();
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(ActivityLog.this, "No logs found", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("FirebaseResult", "Error reading logs: ", error.toException());
                }
            });

        }else{

            Cursor cursor = logger.getLogsByEmail(mail);
            if(cursor.getCount() > 0 ) {
                Toast.makeText(ActivityLog.this, ""+cursor.getCount(), Toast.LENGTH_SHORT).show();
                logs.clear();
                if (cursor.moveToFirst()) {
                    do {
                        Logs log = new Logs(cursor.getString(1), cursor.getString(2));
                        logs.add(log);
                    } while (cursor.moveToNext());
                }

                adapter = new LogAdapter(getApplicationContext(), logs);
                recyclerView.setAdapter(adapter);
            }else {
                Toast.makeText(ActivityLog.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }


        EditText searc = (EditText) findViewById(R.id.search) ;
        searc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(check_connection()){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("Logs");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            logs.clear();
                            for (DataSnapshot spotSnapshot : snapshot.getChildren()) {
                                log_firebase log_fire = spotSnapshot.getValue(log_firebase.class);
                                if (log_fire.email.equals(mail) && log_fire.action.toLowerCase().startsWith(searc.getText().toString().toLowerCase())) {
                                    Logs log = new Logs(log_fire.email, log_fire.action, log_fire.timeStamp);
                                    logs.add(log);
                                }
                            }
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("FirebaseResult", "Error reading logs: ", error.toException());
                        }
                    });

                }
                else{
                    Toast.makeText(ActivityLog.this, searc.getText().toString(), Toast.LENGTH_SHORT).show();

                    Cursor cursor= logger.search(sqLiteDatabase , searc.getText().toString());
                    logs.clear();
                    if(cursor.getCount() > 0 ) {

                        cursor.moveToFirst();
                        do {
                            if (cursor.getString(0).equals(mail) ) {
                                Logs listItem = new Logs(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                                logs.add(listItem);
                            }
                        } while (cursor.moveToNext());


                    }
                    adapter.notifyDataSetChanged();
                }


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



    private boolean check_connection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null){
            return false;
        }
        return networkInfo.isConnected();
    }
}