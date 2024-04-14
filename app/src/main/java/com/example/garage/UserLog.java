package com.example.garage;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.NetworkInfo;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserLog {
    private FirebaseAnalytics myAnalytics;
    FirebaseDatabase myGarage=FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    LogSQL logger;
    Network net;
    ConnectivityManager cm ;
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    private String action;
    private String timestamp;
    private String uname;
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getUname() {
        return uname;
    }
    public void setUname(String uname) {
        this.uname = uname;
    }

    public UserLog(String action, String uname) {
        this.action = action;
        this.uname = uname;
    }
    public void LogEvent(String action, String uname){
        this.timestamp=Long.toString(System.currentTimeMillis());
        if(networkInfo.isConnected()){

        }
    }
}
