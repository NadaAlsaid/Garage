package com.example.demofirebase;
class Spot_firebase {
    public String is_available;
    public String  spot_id;
    public String user_id ;
    public String time_in;
    public String time_out;

}
public class Spot {
    private int spot_id ;
    private int user_id ;
    private String time_in;
    private String time_out;
    private boolean spot_availablility ;

    public int getUser_id() {
        return user_id;
    }

    public String getTime_in() {
        return time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    public int getSpot_id() {
        return spot_id;
    }

    public boolean isSpot_availablility() {
        return spot_availablility;
    }
    public void setSpot_id(int spot_id) {
        this.spot_id = spot_id;
    }


    public void setSpot_availablility(boolean spot_availablility) {
        this.spot_availablility = spot_availablility;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }
//    @Override
//    public void refreshActivity(){
//        recreate();
//    }
}
