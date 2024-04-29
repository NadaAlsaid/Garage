package com.example.garage;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Logs {
    String Email;
    String Action;
    String TimeStamp;
    DatabaseReference myRef;
    FirebaseDatabase database;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }
    public Logs( String action, String timeStamp) {
        Action = action;
        TimeStamp = timeStamp;
    }
    public Logs(String Email, String action, String timeStamp) {
        this.Email=Email;
        Action = action;
        TimeStamp = timeStamp;
    }
    public void CreateLog(String email, String action, String time) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Logs");

        // Create a reference to the user's logs node
//        DatabaseReference ref = database.getReference("Spots");

        DatabaseReference userRef = database.getReference("Logs");

        // Create a new Logs object
        Logs log = new Logs(email, action, time);

        // Push the log object to the user's logs node, generating a unique key
        userRef.push().setValue(log)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseResult", "Log created successfully for user: " + email);
                            // ... handle successful log creation (optional)
                        } else {
                            Log.w("FirebaseResult", "Error creating log: ", task.getException());
                            // ... handle error (optional)
                        }
                    }
                });
    }

}
class log_firebase {
    public String email;
    public String timeStamp;
    public String action;
}
