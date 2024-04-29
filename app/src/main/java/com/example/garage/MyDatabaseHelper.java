package com.example.garage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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
//        db.execSQL("drop table if exists " + TABLE_NAME );
//        onCreate(db);
    }

    public Cursor get_user_info(String email){

        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select "+ COLUMN_FULL_NAME + ","+ COLUMN_USER_NAME + "," + COLUMN_DOB + ", " + COLUMN_GENDER + " ,"
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
    public void deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
    public List<UserModel> getAllUsers() {
        List<UserModel> userList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            int fullNameIndex = cursor.getColumnIndex(COLUMN_FULL_NAME);
            int userNameIndex = cursor.getColumnIndex(COLUMN_USER_NAME);
            int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
            int dobIndex = cursor.getColumnIndex(COLUMN_DOB);
            int genderIndex = cursor.getColumnIndex(COLUMN_GENDER);
            int mobileIndex = cursor.getColumnIndex(COLUMN_MOBILE);
            int passwordIndex = cursor.getColumnIndex(COLUMN_PWD);
            int picUrlIndex = cursor.getColumnIndex(COLUMN_PIC_URL);
            int firebaseIdIndex = cursor.getColumnIndex(COLUMN_USER_ID_FIREBASE);

            do {
                String fullName = cursor.getString(fullNameIndex);
                String userName = cursor.getString(userNameIndex);
                String email = cursor.getString(emailIndex);
                String dob = cursor.getString(dobIndex);
                String gender = cursor.getString(genderIndex);
                String mobile = cursor.getString(mobileIndex);
                String password = cursor.getString(passwordIndex);
                String picUrl = cursor.getString(picUrlIndex);
                String firebaseId = cursor.getString(firebaseIdIndex);
                UserModel userModel = new UserModel(fullName, userName, email, dob, gender, mobile, password, firebaseId, picUrl);
                userList.add(userModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userList;
    }
    String updatePassword(String email , String newPassword){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
//        contentValues.put("ID",ID);
        contentValues.put(COLUMN_PWD,newPassword);

        Cursor cursor = DB.rawQuery("select * from "+ TABLE_NAME + " where " + COLUMN_EMAIL +" = ? " , new String[] {email});
        if(cursor.getCount()>0)
        {
            long result =DB.update(TABLE_NAME, contentValues,COLUMN_EMAIL + " =? ",new String[] {email});
            if (result==-1){
                return "can't update from data";
            } else {
                return "true";
            }
        } else {
            return  "can't update out data " + email ;
        }

    }
}