package com.learning_app.user.chathamkulam.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nelson Andrew on 03-02-2017.
 */

public class StoreEntireDetails extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "StoreData.db";
    public static final String TABLE_NAME = "StoreData";
    public static final String Col_Id = "ID";
    public static final String Col_Country = "COUNTRY";
    public static final String Col_University = "UNIVERSITY";
    public static final String Col_Course = "COURSE";
    public static final String Col_Semester = "SEMESTER";
    public static final String Col_Subject = "SUBJECT";
    public static final String Col_Subject_Id = "SUBJECT_ID";
    public static final String Col_Subject_No = "SUBJECT_NO";
    public static final String Col_Price_type = "PRICE_TYPE";
    public static final String Col_Amount = "AMOUNT";
    public static final String Col_Image = "IMAGE";
    public static final String Col_Validity = "VALIDITY";

    public StoreEntireDetails(Context context) {
        super(context, DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " COUNTRY TEXT, UNIVERSITY TEXT, COURSE TEXT, SEMESTER TEXT, SUBJECT TEXT, SUBJECT_ID TEXT, SUBJECT_NO TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP IF TABLE EXISTS" + TABLE_NAME);

    }

    public boolean addData(String country,String university,String course, String semester,
                           String subject,String subject_id,String subject_no){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_Country,country);
        contentValues.put(Col_University,university);
        contentValues.put(Col_Course,course);
        contentValues.put(Col_Semester,semester);
        contentValues.put(Col_Subject,subject);
        contentValues.put(Col_Subject_Id,subject_id);
        contentValues.put(Col_Subject_No,subject_no);

        long result = db.insert(TABLE_NAME,null,contentValues);

        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getAllDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME ,null);
    }

    public Cursor groupMainDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " GROUP BY COUNTRY,COURSE,SEMESTER" ,null);
    }

    public Cursor groupSubDetails(String subject_id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE SUBJECT_ID = ? " + " GROUP BY SUBJECT_NO" ,new String[]{subject_id});
    }

    public Cursor getSubjectFromTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT SUBJECT FROM " + TABLE_NAME,null);
    }

    public Cursor getVideoPathFromTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT VIDEO FROM " + TABLE_NAME, null);
    }

    public Cursor getRow(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_NAME +" WHERE ID = ?", new String[]{id});

    }

    public void DeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);
        db.close();
    }

    public Integer DeleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "Id = ?", new String[]{id});
    }
}
