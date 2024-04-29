package com.example.garage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class LogSQL extends SQLiteOpenHelper {
    private static String dbname="GarageDbase";
    SQLiteDatabase dp;
    SQLiteDatabase Garage;

    public LogSQL(Context context) {
        super(context, dbname,null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+dbname+"(Email TEXT NOT NULL, Activity TEXT NOT NULL, TimeStamp TEXT NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE if exists "+dbname);
        onCreate(db);
    }
    public long insertEvent(String Email, String action, String timestamp) {
        if (Email == null || Email.isEmpty()) {
            // Handle empty username
            return -2;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Email", Email);
        values.put("Activity", action);
        values.put("TimeStamp", timestamp);
        db.insert(dbname, null, values);

        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        long rowId = -1;
        if (cursor.moveToFirst()) {
            rowId = cursor.getLong(0);
        }
        cursor.close();

        db.close();
        return rowId;
    }

    public Cursor getLogsByEmail(String email) {
        dp = getWritableDatabase();
        return dp.rawQuery("SELECT * FROM " + dbname + " where Email = ? "  , new String[]{email} );
    }

    public void deleteAlllogs() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(dbname, null, null);
    }
    public  Cursor search(SQLiteDatabase sqLiteDatabase , String action){
        return sqLiteDatabase.rawQuery("SELECT * FROM " + dbname + " where Activity like '%" + action + "%' ", null);
    }
}
