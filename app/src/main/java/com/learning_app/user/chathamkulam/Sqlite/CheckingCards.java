package com.learning_app.user.chathamkulam.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Nelson Andrew on 03-02-2017.
 */

public class CheckingCards extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "checkCards.db";
    public static final String TABLE_NAME = "checkCards";
    //    StoreData Columns
    public static final String Col_Id = "ID";
    public static final String Col_CardPosition = "POSITION";
    public static final String Col_Country = "COUNTRY";
    public static final String Col_University = "UNIVERSITY";
    public static final String Col_Course = "COURSE";
    public static final String Col_Semester = "SEMESTER";
    public static final String Col_SubjectId = "SUBJECT_ID";
    public static final String Col_SubjectNumber = "SUBJECT_NO";
    public static final String Col_Subject = "SUBJECT";
    public static final String Col_SubCost = "SUB_COST";
    public static final String Col_Trial = "TRIAL";
    public static final String Col_Duration = "DURATION";
    public static final String Col_NotesCount = "NOTESCOUNT";
    public static final String Col_QbankCount = "QBANKCOUNT";
    public static final String Col_VideoCount = "VIDEOCOUNT";
    public static final String Col_ZipUrl = "ZIPURL";
    private static CheckingCards mInstance = null;

    public CheckingCards(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static CheckingCards getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new CheckingCards(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "POSITION TEXT,COUNTRY TEXT,UNIVERSITY TEXT,COURSE TEXT,SEMESTER TEXT,SUBJECT_ID TEXT,SUBJECT_NO TEXT,SUBJECT TEXT,SUB_COST TEXT,TRIAL TEXT," +
                "DURATION TEXT,NOTESCOUNT TEXT,QBANKCOUNT TEXT,VIDEOCOUNT TEXT,ZIPURL TEXT,VALIDITY_TILL TEXT,PROGRESS TEXT,STATUS TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP IF TABLE EXISTS" + TABLE_NAME);

    }

    public boolean addCheckData(String position, String country, String university, String course, String semester, String subjectId, String subjectNumber,
                                String subject, String sub_cost, String trial, String duration, String notesCount, String qbankCount, String videoCount, String zipUrl) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_CardPosition, position);
        contentValues.put(Col_Country, country);
        contentValues.put(Col_University, university);
        contentValues.put(Col_Course, course);
        contentValues.put(Col_Semester, semester);
        contentValues.put(Col_SubjectId, subjectId);
        contentValues.put(Col_SubjectNumber, subjectNumber);
        contentValues.put(Col_Subject, subject);
        contentValues.put(Col_SubCost, sub_cost);
        contentValues.put(Col_Trial, trial);
        contentValues.put(Col_Duration, duration);
        contentValues.put(Col_NotesCount, notesCount);
        contentValues.put(Col_QbankCount, qbankCount);
        contentValues.put(Col_VideoCount, videoCount);
        contentValues.put(Col_ZipUrl, zipUrl);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getCheckData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

    }

    public void removeUnCheckData(String subject) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();

        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + Col_Subject + "= '" + subject + "'");

        //Close the database
        database.close();
    }

    public void DeleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }

    public Cursor list(long id) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                //Toast.makeText(activityName.this, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
                Log.d("downloadStatus", "Table Name=> " + c.getString(0));

                c.moveToNext();
            }
        }
//        SQLiteDatabase database = this.getWritableDatabase();
//        database.execSQL("delete from requests where _id="+id);
        return null;
    }
}
