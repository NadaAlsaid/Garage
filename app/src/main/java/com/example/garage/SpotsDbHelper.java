package com.example.garage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SpotsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "spots.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "spots";
    public static final String COLUMN_Spot_ID = "spot_id";
    public static final String COLUMN_AVAILABLE = "is_available";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_TIME_IN = "time_in";
    public static final String COLUMN_TIME_OUT = "time_out";

    private static final String CREATE_SPOTS_TABLE = "CREATE TABLE " +
            TABLE_NAME + " (" +
            COLUMN_Spot_ID+ " INTEGER PRIMARY KEY, " +
            COLUMN_AVAILABLE + " TEXT NOT NULL, " +
            COLUMN_EMAIL + " TEXT, " +
            COLUMN_TIME_IN+ " TEXT, " +
            COLUMN_TIME_OUT + " TEXT);";

    public SpotsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SPOTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades if needed (optional)
    }
    public long addSpot(int spot_id ,  String mail , Boolean is_avaliable, String time_in , String time_out) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_Spot_ID, spot_id);
        if (is_avaliable== true){
            values.put(COLUMN_AVAILABLE, "true");
        }else if(is_avaliable == false){
            values.put(COLUMN_AVAILABLE, "false");
        }
        values.put(COLUMN_EMAIL, mail);
        values.put(COLUMN_TIME_IN, time_in);
        values.put(COLUMN_TIME_OUT, time_out);
        long newRowId = db.insert(TABLE_NAME, null, values);
        return newRowId;
    }
    public ArrayList<Spot> getAllSpots() {
        ArrayList<Spot> spots = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int spot_id = 5;
            String mail  = "";
            String name ="000" , time_in = "0:0", time_out="0:0";
            Boolean available = false;
            int w = cursor.getColumnIndex(COLUMN_Spot_ID);
            if (w>-1){
                spot_id = cursor.getInt(w);
            }
            int y = cursor.getColumnIndex(COLUMN_AVAILABLE);
            if (y>-1){
                if(cursor.getString(y).equalsIgnoreCase("true")){
                    available = true;
                }else if(cursor.getString(y).equalsIgnoreCase("false")){
                    available=false;
                }
            }
            // Assuming these additional columns exist in your Spots table
            int z = cursor.getColumnIndex(COLUMN_EMAIL);
            if (z>-1){
                mail = cursor.getString(z);  // Replace with actual column name for user ID
            }
            int m = cursor.getColumnIndex(COLUMN_TIME_IN);
            if (m>-1){
                time_in = cursor.getString(m);  // Replace with actual column name for time_in
            }
            int n = cursor.getColumnIndex(COLUMN_TIME_OUT);
            if (n>-1){
                time_out = cursor.getString(n);  // Replace with actual column name for time_out
            }

            Spot s= new Spot();
            s.setSpot_id(spot_id);
            s.setUser_id(mail);
            s.setSpot_availablility(available);
            s.setTime_in(time_in);
            s.setTime_out(time_out);
            spots.add(s);
        }
        cursor.close();
        return spots;
    }
    public void deleteAllSpots() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    public Spot  get_spot(String mail) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM spots WHERE " + COLUMN_EMAIL + " = ?", new String[] {String.valueOf(mail)});
            int count = cursor.getCount();
            if (count == 0) {
                return null;
            }
            ArrayList<Spot> elementList = new ArrayList<Spot>();
            Spot s= new Spot();
//            int i = 0;
            if (cursor.moveToFirst()) {
//                do {
                    /*
                    *    TABLE_NAME + " (" +
                        COLUMN_Spot_ID+ " INTEGER PRIMARY KEY, " +
                        COLUMN_AVAILABLE + " INTEGER NOT NULL, " +
                        COLUMN_USER_ID + " INTEGER, " +
                        COLUMN_TIME_IN+ " TEXT, " +
                        COLUMN_TIME_OUT + " TEXT);";
                    * */
                int spot_id = cursor.getInt(0);
                String available = cursor.getString(1);
                int user_id = cursor.getInt(2);
                String time_in = cursor.getString(3);
                String time_out = cursor.getString(4);

                s.setSpot_id(spot_id);
                s.setUser_id(mail);
                if(available.equalsIgnoreCase("true")){
                    s.setSpot_availablility(true);

                }else if(available.equalsIgnoreCase("false")){
                    s.setSpot_availablility(false);
                }
                s.setTime_in(time_in);
                s.setTime_out(time_out);
                elementList.add(s);
//                    i += 1;
//                } while (cursor.moveToNext());
            }
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }
    public ArrayList<Spot> getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM spots ", null);
            int count = cursor.getCount();
            if (count == 0) {
                return null;
            }
            ArrayList<Spot> elementList = new ArrayList<Spot>();
            int i = 0;
            if (cursor.moveToFirst()) {
                do {
                    /*
                    *    TABLE_NAME + " (" +
                        COLUMN_Spot_ID+ " INTEGER PRIMARY KEY, " +
                        COLUMN_AVAILABLE + " INTEGER NOT NULL, " +
                        COLUMN_USER_ID + " INTEGER, " +
                        COLUMN_TIME_IN+ " TEXT, " +
                        COLUMN_TIME_OUT + " TEXT);";
                    * */
                    int spot_id = cursor.getInt(0);
                    String available = cursor.getString(1);
//                    int user_id = cursor.getInt(2);
                    String mail  = cursor.getString(2);
                    String time_in = cursor.getString(3);
                    String time_out = cursor.getString(4);
                    Spot s= new Spot();
                    s.setSpot_id(spot_id);
                    s.setUser_id(mail);
                    if(available.equalsIgnoreCase("true")){
                        s.setSpot_availablility(true);

                    }else if(available.equalsIgnoreCase("false")){
                        s.setSpot_availablility(false);

                    }
                    s.setTime_in(time_in);
                    s.setTime_out(time_out);
                    elementList.add(s);
                    i += 1;
                } while (cursor.moveToNext());
            }
            return elementList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }
}
//final class SpotsContract {
//
//    private SpotsContract() {}
//
//    public static final class SpotsEntry {
//        public static final String TABLE_NAME = "spots";
//        public static final String _ID = "_id";
//        public static final String COLUMN_NAME = "name";
//        public static final String COLUMN_AVAILABLE = "available";
//        public static final String COLUMN_USER_ID = "user_id";
//        public static final String COLUMN_TIME_IN = "time_in";
//        public static final String COLUMN_TIME_OUT = "time_out";
//    }
//}
