package com.example.garage;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;
import java.util.List;


public class RegisterActivity extends AppCompatActivity {
    EditText editTextRegisterFullNme ,editTextRegisterUserNme, editTextRegisterEmail ,
            editTextRegisterDoB , editTextRegisterMobil , editTextRegisterPwd, editTextRegisterConfirmPwd;
    ProgressBar progressBar;
    RadioGroup radioGroupRegisterGender;
    RadioButton radioButtonRegisterGenderSelected;
    ImageButton pickDate;
    String url =" " ;
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private static final int RC_CHOOSE_IMAGE = 101;  // Request code for image selection
    ImageView profileImageView;
    Button chooseImageButton, registerButton;
    Uri profileImageUri;
    private FirebaseStorage storage;
    MyDatabaseHelper helper = new MyDatabaseHelper(this);

    boolean isNetworkConnected = false;

    String textfullName;
    String textUserName;
    String textEmail ;
    String textDoB ;
    String textMobile ;
    String textPwd;
    String textConPwd ;
    String textGender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toast.makeText(RegisterActivity.this, "you can register now", Toast.LENGTH_LONG).show();

        editTextRegisterFullNme = findViewById(R.id.editText_register_full_name);
        editTextRegisterUserNme=findViewById(R.id.editText_register_user_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobil = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd =findViewById(R.id.editText_register_ConPassword);
        progressBar=findViewById(R.id.progressBar);
        //upload pic
        profileImageView = findViewById(R.id.profile_image_view);
        chooseImageButton = findViewById(R.id.choose_profile_image_button);
        // Get a reference to Firebase Storage
        storage = FirebaseStorage.getInstance();
        // Choose image button click listener

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(RegisterActivity.this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });



        pickDate = findViewById(R.id.imageView_date_picker);
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(calendar.YEAR);
                int month = calendar.get(calendar.MONTH);
                int day  =calendar.get(calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText( (year+"/"+month+"/"+dayOfMonth) );
                    }
                },year,month,day);
                datePickerDialog.setTitle("Select date");
                datePickerDialog.show();
            }
        });


        //radiobutton for gender
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);


                textfullName = editTextRegisterFullNme.getText().toString();
                textUserName=editTextRegisterUserNme.getText().toString();
                textEmail = editTextRegisterEmail.getText().toString();
                textDoB = editTextRegisterDoB.getText().toString();
                textMobile = editTextRegisterMobil.getText().toString();
                textPwd = editTextRegisterPwd.getText().toString();
                textConPwd = editTextRegisterConfirmPwd.getText().toString();



                if (TextUtils.isEmpty(textfullName)) {
                    Toast.makeText(RegisterActivity.this, "please enter your full name ", Toast.LENGTH_LONG).show();
                    editTextRegisterFullNme.setError("full name is required");
                    editTextRegisterFullNme.requestFocus();

                }
                else if(TextUtils.isEmpty(textUserName)){
                    Toast.makeText(RegisterActivity.this, "please enter your user name ", Toast.LENGTH_LONG).show();
                    editTextRegisterUserNme.setError("user name is required");
                    editTextRegisterUserNme.requestFocus();

                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "please enter your email ", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("email is required");
                    editTextRegisterEmail.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "please re-enter your email ", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();

                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this, "please enter you date of birth ", Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Date of birth is required");
                    editTextRegisterDoB.requestFocus();

                }else if (radioGroupRegisterGender.getCheckedRadioButtonId()== -1){
                    Toast.makeText(RegisterActivity.this, "please select your gender ", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError(" gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }else if(TextUtils.isEmpty(textMobile)){
                    Toast.makeText(RegisterActivity.this, "please enter your mobile number ", Toast.LENGTH_LONG).show();
                    editTextRegisterMobil.setError("mobile number is required");
                    editTextRegisterMobil.requestFocus();

                }else if(textMobile.length() != 11){
                    Toast.makeText(RegisterActivity.this, "please re-enter your mobile number ", Toast.LENGTH_LONG).show();
                    editTextRegisterMobil.setError("mobile number should be 11 digits");
                    editTextRegisterMobil.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(RegisterActivity.this, "please enter your password ", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("password is required");
                    editTextRegisterPwd.requestFocus();

                }else if(textPwd.length() < 6){
                    Toast.makeText(RegisterActivity.this, "please re-enter your password with at least 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("password is too weak");
                    editTextRegisterPwd.requestFocus();
                }else if(TextUtils.isEmpty(textConPwd)){
                    Toast.makeText(RegisterActivity.this, "please confirm your password ", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();

                }else if(!textPwd.equals(textConPwd)){
                    Toast.makeText(RegisterActivity.this, "please same same password ", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("password confirmation and password aren't same");
                    editTextRegisterConfirmPwd.requestFocus();

                    //clear
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                }
                else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    registerUser(textfullName,textUserName,textEmail,textDoB,textPwd,textGender,textMobile , url);

                }

                if (isNetworkConnected) {
                    // Register user with Firebase
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    registerUser(textfullName,textUserName,  textEmail,  textDoB,  textPwd, textGender,  textMobile,   url );
                    registerNetworkCallback();
                } else {
                    // Save user information to local database
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    registerUser(textfullName,textUserName,textEmail,textDoB,textPwd,textGender,textMobile , url);
                    registerNetworkCallback();
                    Toast.makeText(RegisterActivity.this, "No internet connection. User information saved locally.", Toast.LENGTH_LONG).show();
                }

            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        profileImageUri = data.getData();
        profileImageView.setImageURI(profileImageUri);
        url = profileImageUri.toString();

    }
    private void registerUser(String textfullName,String textUserName, String textEmail, String textDoB, String textPwd,String textGender, String textMobile,  String url ) {

        FirebaseAuth auth =FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    //enter user info to database
                    UserModel writeUserDetails = new UserModel(textfullName , textUserName , textEmail,textDoB  , textGender, textMobile , textPwd ,FirebaseUtil.currentUserId() , url);
                    setUsername(writeUserDetails);
                    saveUserToLocalCache(textfullName , textUserName , textEmail,textDoB , textGender , textMobile , textPwd,url ,FirebaseUtil.currentUserId());

                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "User registered failed on firebase ", Toast.LENGTH_LONG).show();
                }
            }
        });
        if (FirebaseUtil.currentUserId() == null )
            saveUserToLocalCache(textfullName , textUserName , textEmail,textDoB , textGender , textMobile , textPwd,url , null);

    }

    private void setUsername( UserModel userModel ){

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext() , "well done!" , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, loginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this, "Check your network connection", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void saveUserToLocalCache(String textfullName,String textUserName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd, String url , String firebase_id){

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_FULL_NAME, textfullName);
        values.put(MyDatabaseHelper.COLUMN_USER_NAME, textUserName);
        values.put(MyDatabaseHelper.COLUMN_EMAIL, textEmail);
        values.put(MyDatabaseHelper.COLUMN_DOB, textDoB);
        values.put(MyDatabaseHelper.COLUMN_MOBILE, textMobile);
        values.put(MyDatabaseHelper.COLUMN_GENDER, textGender);
        values.put(MyDatabaseHelper.COLUMN_PWD, textPwd);
        values.put(MyDatabaseHelper.COLUMN_PIC_URL, url);
        values.put(MyDatabaseHelper.COLUMN_USER_ID_FIREBASE, firebase_id);
        db.insert(MyDatabaseHelper.TABLE_NAME, null, values);
        db.close();

    }
    private void registerNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    isNetworkConnected = true;
                    Toast.makeText(RegisterActivity.this, "Internet connection available.", Toast.LENGTH_SHORT).show();

                    // Send user information to Firebase database when internet connection is restored
                    sendUserInfoToFirebase();
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    isNetworkConnected = false;
                    Toast.makeText(RegisterActivity.this, "Internet connection lost.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendUserInfoToFirebase() {
        // Retrieve saved user information from the local database
        List<UserModel> userList = helper.getAllUsers();

        // Iterate through the user list and send each user's information to Firebase database
        for (UserModel user : userList) {
            registerUser(textfullName,textUserName,textEmail,textDoB,textPwd,textGender,textMobile , url);
        }

//        // Clear the local database after sending the user information to Firebase
//        helper.deleteAllUsers();
    }

}