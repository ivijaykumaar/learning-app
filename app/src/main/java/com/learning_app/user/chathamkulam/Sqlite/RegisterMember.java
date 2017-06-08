package com.learning_app.user.chathamkulam.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 4/30/2017.
 */



public class RegisterMember extends SQLiteOpenHelper {

    private static RegisterMember mInstance = null;

    public static final String DATABASE_NAME = "MemberRegister.db";

    private static final String TABLE_NAME = "MemberRegister";
    private static final String Col_UserId = "ID";
    private static final String Col_UserName = "USERNAME";
    private static final String Col_EmailId = "EMAIL_ID";
    private static final String Col_MobileNo = "MOBILE_NUMBER";
    private static final String Col_ProfilePic = "PROFILE_PIC";


    public static RegisterMember getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new RegisterMember(ctx.getApplicationContext());
        }
        return mInstance;
    }


    private RegisterMember(Context context) {
        super(context, DATABASE_NAME,null,1);
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

    public boolean addMember(String userName,String emailId,String mobileNo){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_UserName,userName);
        contentValues.put(Col_EmailId,emailId);
        contentValues.put(Col_MobileNo,mobileNo);

        long result = db.insert(TABLE_NAME,null,contentValues);

        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean setProfile(byte[] image) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new  ContentValues();
        contentValues.put(Col_ProfilePic, image);

        long result = db.insert(TABLE_NAME,null,contentValues);

        if (result == -1){
            return false;
        }else {
            return true;
        }
    }


    public Cursor getDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT USERNAME,EMAIL_ID,MOBILE_NUMBER FROM " + TABLE_NAME,null);
        return data;
    }

    public boolean ifExists(String member) {

        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String checkQuery = "SELECT " + Col_EmailId + " FROM " + TABLE_NAME + " WHERE " + Col_EmailId + "= '"+ member + "'";
        cursor= db.rawQuery(checkQuery,null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}
