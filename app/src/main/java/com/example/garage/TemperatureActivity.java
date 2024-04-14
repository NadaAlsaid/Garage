package com.example.garage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class TemperatureActivity extends AppCompatActivity {
    TextView temperature ;
    AnimatedBottomBar animatedBottomBar ;
    final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            if (o) {
                Toast.makeText( getApplicationContext(), "Post notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Post notification permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_temperature);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        temperature = findViewById(R.id.Temperature);
        temperature.setText("70.0");
        float reading = Float.parseFloat(temperature.getText().toString()) ;
        if(30.0 <  reading){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "test")
                    .setSmallIcon(R.drawable.baseline_notifications_24)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("There is fire in garage!!\n temperature is " + temperature.getText().toString())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = getString(R.string.app_name);
                    String description = "Example Notification";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("test", name, importance);
                    channel.setDescription(description);
                    notificationManager.createNotificationChannel(channel);
                    notificationManager.notify(10, builder.build());
                }
            }
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
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return true;
//    }
//    public boolean onOptionsItemSelected(MenuItem item){
//        Intent intent ;
//
//        if (item.getItemId() == R.id.tab_profile) {
//            intent = new Intent(getApplicationContext(), UserProfileActivity.class);
//            finish();
//            startActivity(intent);
//        } else if (item.getItemId() == R.id.tab_cart) {
//            intent = new Intent(getApplicationContext(), ActivityLog.class);
//            finish();
//            startActivity(intent);
//        }
//        else if (item.getItemId() == R.id.tab_logout) {
//            SharedPreferences preferences = getSharedPreferences("checkedbox", MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putString("remember", "false");
//            editor.apply();
//            intent = new Intent(getApplicationContext(), WelcomeActivity.class);
//            startActivity(intent);
//        }
//        return false;
//    }
}