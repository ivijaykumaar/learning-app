package com.learning_app.user.chathamkulam.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 4/30/2017.
 */


public class RegisterMember extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MemberRegister.db";
    private static final String TABLE_NAME = "MemberRegister";
    private static final String Col_UserId = "ID";
    private static final String Col_UserName = "USERNAME";
    private static final String Col_EmailId = "EMAIL_ID";
    private static final String Col_MobileNo = "MOBILE_NUMBER";
    private static final String Col_ProfilePic = "PROFILE_PIC";
    private static RegisterMember mInstance = null;


    private RegisterMember(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static RegisterMember getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new RegisterMember(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " USERNAME TEXT ,EMAIL_ID TEXT,MOBILE_NUMBER TEXT,PROFILE_PIC BLOB)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP IF TABLE EXISTS" + TABLE_NAME);

    }

    public boolean addMember(String userName, String emailId, String mobileNo, byte[] image) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_UserName, userName);
        contentValues.put(Col_EmailId, emailId);
        contentValues.put(Col_MobileNo, mobileNo);
        contentValues.put(Col_ProfilePic, image);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateProfile(String name, String email, String number, byte[] image) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_UserName, name);
        contentValues.put(Col_EmailId, email);
        contentValues.put(Col_MobileNo, number);
        contentValues.put(Col_ProfilePic, image);

        db.update(TABLE_NAME, contentValues, null, null);
        return true;

    }

    public boolean updateDp(byte[] image) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Col_ProfilePic, image);

        db.update(TABLE_NAME, cv, null, null);
        return true;
    }

    public Cursor getDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }

    public boolean ifExists(String member) {

        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String checkQuery = "SELECT " + Col_EmailId + " FROM " + TABLE_NAME + " WHERE " + Col_EmailId + "= '" + member + "'";
        cursor = db.rawQuery(checkQuery, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}
