package com.example.garage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SubscriptionDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Subscription.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Subscription";
    public static final String COLUMN_Spot_ID = "spot_id";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_EMAIL = "email";


    private static final String CREATE_SPOTS_TABLE = "CREATE TABLE " +
            TABLE_NAME + " (" +
            COLUMN_Spot_ID+ " Text, " +
            COLUMN_EMAIL + " TEXT, " +
            COLUMN_PRICE + " TEXT);";

    public SubscriptionDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SPOTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public long addSpot(String spot_id ,  String mail , String price) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_Spot_ID, spot_id);
        values.put(COLUMN_EMAIL, mail);
        values.put(COLUMN_PRICE, price);
        long newRowId = db.insert(TABLE_NAME, null, values);
        return newRowId;
    }
    public Cursor getAllSpots(String email) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " where " + COLUMN_EMAIL + " = ? "  , new String[]{email} );


    }

    public boolean updateSpot (String spot_id, String mail ,String price ){
        SQLiteDatabase db  = getWritableDatabase();
        ContentValues values = new  ContentValues();
        values.put(COLUMN_Spot_ID, spot_id);
        values.put(COLUMN_EMAIL, mail);
        values.put(COLUMN_PRICE, price);

        long result =db.update(TABLE_NAME, values, COLUMN_EMAIL + " = ?", new String[] {mail});
        return result != -1 ;
    }
}
