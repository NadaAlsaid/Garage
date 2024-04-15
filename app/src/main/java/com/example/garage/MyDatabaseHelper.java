package com.example.garage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "your_database_name.db";
    private static final int DATABASE_VERSION = 1;

    // Define table and column names
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_FULL_NAME = "fullname"; // Corrected typo
    public static final String COLUMN_USER_NAME = "username";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_EMAIL = "mail";
    public static final String COLUMN_MOBILE = "mobile";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_PWD = "password"; // Consider hashing before storing
    public static final String COLUMN_PIC_URL = "pic_url";

    public static final String COLUMN_USER_ID_FIREBASE = "id_firebase";
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME +
                " ( USER_ID INTEGER primary key AUTOINCREMENT, " +COLUMN_FULL_NAME + " TEXT, " +
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_DOB + " TEXT, " +
                COLUMN_MOBILE + " TEXT, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_PWD + " TEXT ," +
                COLUMN_PIC_URL + " TEXT ," +
                COLUMN_USER_ID_FIREBASE + " TEXT " + ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades (optional)
    }

    public Cursor get_user_info(String email){

        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select "+ COLUMN_FULL_NAME + "," + COLUMN_DOB + ", " + COLUMN_GENDER + " ,"
                + COLUMN_PIC_URL + ", " + COLUMN_MOBILE + " , " + COLUMN_EMAIL +" from " +TABLE_NAME+" where " + COLUMN_EMAIL + " = ? ", new String[] {email});
        return cursor;

    }
    public boolean checkEmail(String email)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ TABLE_NAME +  " where " + COLUMN_EMAIL+ "=? "  , new String[]{email});
        if(cursor.getCount()>0)
            return true;
        else
            return false;

    }
    public Cursor checkPasswordAndEmail(String email, String password)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select USER_ID INTEGER from "+ TABLE_NAME +  " where " + COLUMN_EMAIL+ "=? " + " and " + COLUMN_PWD + " =? "  , new String[]{email,password});
        return cursor;
    }

}