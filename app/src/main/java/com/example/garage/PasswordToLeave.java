package com.example.garage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Random;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class PasswordToLeave extends AppCompatActivity {
    AnimatedBottomBar animatedBottomBar ;
    int generated;
    Random r ;
    TextView textView ;
    String mail ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_to_leave);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        mail = sharedPreferences.getString("email", "");
        EditText passwordEditText = findViewById(R.id.passwordEdittext);
        Button verify = findViewById(R.id.verify);
        Random r = new Random( System.currentTimeMillis() );
        generated =  ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        textView = findViewById(R.id.password);
        textView.setText("Your Password : " + String.valueOf( generated));
        textView.setVisibility(View.VISIBLE);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_connection()) {

                    String password = passwordEditText.getText().toString() ;
                    if(password.equals(String.valueOf(generated))) {


                        FirebaseDatabase database = FirebaseDatabase.getInstance();

//            Toast.makeText(getApplicationContext(), mail, Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(), ref.getKey() , Toast.LENGTH_SHORT).show();
                        DatabaseReference ref = database.getReference("Spots");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Toast.makeText(getApplicationContext(), " Network connected", Toast.LENGTH_SHORT).show();
//
                                for (DataSnapshot spotSnapshot : snapshot.getChildren()) {
                                    Spot_firebase spots = spotSnapshot.getValue(Spot_firebase.class);
                                    if (spots != null && spots.email.equals(mail)) {
                                        if(spots.is_available.equals("false")) {
                                            String password = passwordEditText.getText().toString();
                                            if (password.equals(String.valueOf(generated)) ) {
                                                spots.password = "true";

                                            } else {
                                                spots.password = "false";
                                                Toast.makeText(getApplicationContext(), "your password is wrong", Toast.LENGTH_SHORT).show();
                                                generated = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
                                                textView.setText("Your Password : " + String.valueOf(generated));

                                            }
                                            DatabaseReference spotRef = database.getReference("Spots").child("spot" + spots.spot_id);

                                            // Update both is_available and user_id
                                            HashMap<String, Object> updates = new HashMap<>();
                                            updates.put("password", "true");
                                            spotRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                    // Handle update completion (same as before)
                                                    Toast.makeText(getApplicationContext(), "You can open the door now 🫡", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            passwordEditText.setText("");
                                        }else{
                                            Toast.makeText(getApplicationContext(), "You should subscribe first", Toast.LENGTH_SHORT).show();

                                        }
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
        return true;
    }
}