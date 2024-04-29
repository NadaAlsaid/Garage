package com.example.garage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class UserProfileActivity extends AppCompatActivity {
    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile, url;
    private ImageView profileImageView;
    String userEmail ;
    private Button logoutButton;
    AnimatedBottomBar animatedBottomBar;
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private static final int RC_CHOOSE_IMAGE = 101;
    Uri profileImageUri;
    UserModel readUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textViewWelcome = findViewById(R.id.textView_show_welcome);
        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewDoB = findViewById(R.id.textView_show_dob);
        textViewGender = findViewById(R.id.textView_show_gender);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        progressBar = findViewById(R.id.progress_bar);
        logoutButton = findViewById(R.id.logout_button);
        profileImageView = findViewById(R.id.imageView_profile_dp);
        readUserDetails = new UserModel();


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(UserProfileActivity.this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("checkedbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                Intent intent = new Intent(UserProfileActivity.this, WelcomeActivity.class);
                startActivity(intent);

            }
        });


        progressBar.setVisibility(View.VISIBLE);
        showUserProfile();
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

//    private void showUserProfile() {
//
//        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    readUserDetails =  task.getResult().toObject(UserModel.class);
//                    if(readUserDetails!=null){
//                        fullName = readUserDetails.getTextfullName();
//                        email = readUserDetails.getTextEmail();
//                        doB = readUserDetails.getTextDoB();
//                        gender = readUserDetails.getTextGender();
//                        mobile = readUserDetails.getTextMobile();
//                        String url = readUserDetails.getUrl() ;
//                        textViewWelcome.setText("welcome, " + readUserDetails.getTextUserName() + " !");
//                        textViewFullName.setText(fullName);
//                        textViewEmail.setText(email);
//                        textViewDoB.setText(doB);
//                        textViewGender.setText(gender);
//                        textViewMobile.setText(mobile);
//                        if(url != null) {
//                            profileImageUri = Uri.parse(url) ;
//
////                            profileImageView.setImageURI(profileImageUri);
//                        }
////                        Toast.makeText(getApplicationContext() , "heeeeeeeelp" , Toast.LENGTH_LONG).show();
//                    }
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
//
//
//    }


    private void showUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("email", "");
        if (isConnectedToInternet()) {
            readUserDetailsFromFirebase();
        } else {
            readUserDetailsFromLocalDatabase();
        }

    }




    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }




    private void readUserDetailsFromFirebase() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    readUserDetails = task.getResult().toObject(UserModel.class);
                    if (readUserDetails != null) {
                        fullName = readUserDetails.getTextfullName();
                        email = readUserDetails.getTextEmail();
                        doB = readUserDetails.getTextDoB();
                        gender = readUserDetails.getTextGender();
                        mobile = readUserDetails.getTextMobile();
//                        Toast.makeText(getApplicationContext(),  "  "+ readUserDetails.getUrl() , Toast.LENGTH_LONG).show();
                        url = readUserDetails.getUrl();
                        textViewWelcome.setText("Welcome, " + readUserDetails.getTextUserName() + "!");
                        textViewFullName.setText(fullName);
                        textViewEmail.setText(email);
                        textViewDoB.setText(doB);
                        textViewGender.setText(gender);
                        textViewMobile.setText(mobile);
                        if (url != null) {
//                            profileImageUri = Uri.parse(url);
                            profileImageView.setImageURI(Uri.parse(url));
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }




    private void readUserDetailsFromLocalDatabase() {

        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(this);
        Toast.makeText(getApplicationContext(), userEmail, Toast.LENGTH_LONG).show();

        Cursor cursor = databaseHelper.get_user_info(userEmail);

        if (cursor.moveToFirst()) {
            int fullNameIndex = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_FULL_NAME);
            int userNameIndex = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_USER_NAME);
            int dobIndex = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_DOB);
            int genderIndex = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_GENDER);
            int picUrlIndex = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_PIC_URL);
            int mobileIndex = cursor.getColumnIndex(MyDatabaseHelper.COLUMN_MOBILE);

            fullName = cursor.getString(fullNameIndex);
            String userName = cursor.getString(userNameIndex);
            doB = cursor.getString(dobIndex);
            gender = cursor.getString(genderIndex);
            url = cursor.getString(picUrlIndex);
            mobile = cursor.getString(mobileIndex);
            textViewWelcome.setText("Welcome, " + userName + "!");

            textViewFullName.setText(fullName );
            textViewEmail.setText(userEmail);
            textViewDoB.setText(doB);
            textViewGender.setText(gender);
            textViewMobile.setText(mobile);
            if (url != null) {
                profileImageView.setImageURI(Uri.parse(url));
            }
        }

        cursor.close();
        progressBar.setVisibility(View.GONE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            profileImageUri = data.getData();
            profileImageView.setImageURI(profileImageUri);

        }
    }


}