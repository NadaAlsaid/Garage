package com.example.garage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogSQL extends SQLiteOpenHelper {
    private static String dbname="GarageDbase";
    SQLiteDatabase Garage;

    public LogSQL(Context context) {
        super(context, dbname,null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+dbname+"(UserName TEXT NOT NULL, Activity TEXT NOT NULL, TimeStamp TEXT NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE if exists "+dbname);
        onCreate(db);
    }
    public void insertEvent(String uname,String action, String timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UserName", uname);
        values.put("Activity", action);
        values.put("TimeStamp",timestamp);
        db.insert(dbname, null, values);
        db.close();
    }
}
