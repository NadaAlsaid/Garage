package com.example.garage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText editTextPwdResetEmail;
    private EditText editTextNewPwdReset;
    private Button buttonPwdReset;
    private ProgressBar progressBar;
    private final static String TAG ="ForgetPasswordActivity";
    private FirebaseAuth authProfile;
    UserModel readUserDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextPwdResetEmail =findViewById(R.id.editText_password_reset_email);
        editTextNewPwdReset=findViewById(R.id.editText_new_pwd);
        buttonPwdReset =findViewById(R.id.button_password_reset);
        progressBar =findViewById(R.id.progressBar);

        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=editTextPwdResetEmail.getText().toString();
                String newPassword=editTextNewPwdReset.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgetPasswordActivity.this, "please enter your  Registered Email ", Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Valid Email is required");
                    editTextPwdResetEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgetPasswordActivity.this, "please valid email ", Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Valid email is required");
                    editTextPwdResetEmail.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });

    }

    private void resetPassword(String email) {

        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Please check your inbox for password reset link ", Toast.LENGTH_LONG).show();

                    showNewPasswordDialog();

//                    Intent intent = new Intent(ForgetPasswordActivity.this, loginActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please check", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });




    }
    private void showNewPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);
        builder.setTitle("Enter New Password");

        // Set up the input
        final EditText input = new EditText(ForgetPasswordActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = input.getText().toString().trim();
                String Email = editTextPwdResetEmail.getText().toString();
                // Save new password in local database
                savePasswordLocally( Email ,newPassword);

                // Save new password in Firebase database
                savePasswordInFirebase(newPassword);

                // Proceed to the login activity
                Intent intent = new Intent(ForgetPasswordActivity.this, loginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void savePasswordLocally(String email ,String newPassword) {
        FirebaseUser currentUser = authProfile.getCurrentUser();
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(getApplicationContext());
        String  x= myDatabaseHelper.updatePassword( email ,newPassword);

        Toast.makeText(getApplicationContext() , x , Toast.LENGTH_LONG).show();
    }
    private void savePasswordInFirebase(String newPassword) {
        // Save the new password in the Firebase database
        // Replace this with your own implementation based on your Firebase database setup
        FirebaseUser currentUser = authProfile.getCurrentUser();
        readUserDetails = new UserModel();
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    readUserDetails =  task.getResult().toObject(UserModel.class);
                    if(readUserDetails!=null){
                        readUserDetails.setTextPwd(newPassword);
                        currentUser.delete() ;
                        FirebaseUtil.currentUserDetails().set(readUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext() , "well done!" , Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Check your network connection", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
//            databaseReference.child("textPwd").setValue(newPassword);
//        }
    }


}


