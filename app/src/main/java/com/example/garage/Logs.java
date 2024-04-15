package com.example.garage;

public class Logs {

    String Action;
    String TimeStamp;

    public Logs(String action, String timeStamp) {
        Action = action;
        TimeStamp = timeStamp;
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
}
