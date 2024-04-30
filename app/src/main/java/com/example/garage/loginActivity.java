package com.example.garage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class loginActivity extends AppCompatActivity {
    EditText editTextLoginEmail, editTextLoginPwd;
    ProgressBar progressBar;
    boolean isNetworkConnected = false;
    UserModel readUserDetails;
    CheckBox rememberME;
    Button google ;
    Button register_btn ;
    private FirebaseAuth authProfile;
    String textEmail = " ";
    MyDatabaseHelper userDatabase ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.progressBar);
        rememberME =findViewById(R.id.checkbox_remember_me);
        register_btn = findViewById(R.id.register_link);
        google = findViewById(R.id.google) ;

        userDatabase = new MyDatabaseHelper(this);
        authProfile = FirebaseAuth.getInstance();

        SharedPreferences preferences =getSharedPreferences("checkedbox",MODE_PRIVATE);
        String checkbox=preferences.getString("remember","");

        if(checkbox.equals("true")){
            Intent intent = new Intent(loginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        Button buttonForgetPassword = findViewById(R.id.button_forgot_password);
        buttonForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(loginActivity.this, "you can reset your password now ! ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(loginActivity.this,ForgetPasswordActivity.class));
            }
        });
        Button buttonLogin = findViewById(R.id.button_login);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();
                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(loginActivity.this, "please enter your Email ", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(loginActivity.this, "please re-enter your email ", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid email is required");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(loginActivity.this, "please enter your password ", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("password is required");
                    editTextLoginPwd.requestFocus();

                }
//                else if () {}
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);

                }
            }
        });

        rememberME.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    SharedPreferences preferences=getSharedPreferences("checkedbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("remember","true");
                    editor.apply();
                    preferences = getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.remove("email" );
                    editor.putString("email" , editTextLoginEmail.getText().toString()) ;
                    editor.apply();
                    Toast.makeText(loginActivity.this, "Checked ", Toast.LENGTH_SHORT).show();

                }else if(!compoundButton.isChecked()){
                    SharedPreferences preferences=getSharedPreferences("checkedbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("remember","false");
                    editor.apply();
                    Toast.makeText(loginActivity.this, "UnChecked ", Toast.LENGTH_SHORT).show();

                }
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = editTextLoginEmail.getText().toString() ;
                String pass = editTextLoginPwd.getText().toString() ;
                LoginNetworkCallback();
                if (isNetworkConnected){
                    readUserDetails = new UserModel();
                    FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                readUserDetails =  task.getResult().toObject(UserModel.class);
                                if(readUserDetails!=null){
                                    if( readUserDetails.getTextEmail().equals(mail)  && pass.equals( readUserDetails.getTextPwd())){
                                        SharedPreferences preferences = getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.remove("email" );
                                        editor.putString("email" , mail) ;
                                        Toast.makeText(getApplicationContext(), "user " +mail, Toast.LENGTH_SHORT).show();
                                        editor.apply();
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        finish();
                                        startActivity(intent);
                                    }

                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });


                }else {
                    if (!mail.equals("") && !pass.equals("")) {

                        Cursor checkPasswordAndEmail = userDatabase.checkPasswordAndEmail(mail, pass);
                        checkPasswordAndEmail.moveToNext();

                        if (checkPasswordAndEmail.getCount() > 0) {
                            SharedPreferences preferences = getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.remove("email" );
                            editor.putString("email" , mail) ;
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            finish();
                            startActivity(intent);
                        } else {
                            editTextLoginEmail.setText("");
                            editTextLoginPwd.setText("");
                            Toast.makeText(getApplicationContext(), "Your Email or Password is wrong ", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (mail.equals("")) {
                            editTextLoginEmail.setError("Valid email is required");
                            editTextLoginEmail.requestFocus();
                        } else {

                            editTextLoginPwd.setError("password is required");
                            editTextLoginPwd.requestFocus();
                        }
                    }
                }
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String Email,String Pwd) {

        authProfile.signInWithEmailAndPassword(Email, Pwd).addOnCompleteListener(loginActivity.this,new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                if (task.isSuccessful()){
                    SharedPreferences preferences = getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("email" );
                    editor.putString("email" , Email) ;
                    editor.apply();
                    Toast.makeText(loginActivity.this, "you are logged in now ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext() , HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(loginActivity.this, "something went worng!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }


        });
    }
    private void LoginNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    isNetworkConnected = true;
                    Toast.makeText(getApplicationContext(), "Internet connection available.", Toast.LENGTH_SHORT).show();
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    isNetworkConnected = false;
                    Toast.makeText(getApplicationContext(), "Internet connection lost.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}