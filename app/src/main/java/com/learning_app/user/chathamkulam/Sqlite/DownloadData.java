package com.learning_app.user.chathamkulam.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nelson Andrew on 03-02-2017.
 */

public class DownloadData extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "downloadData.db";
    private static final String TABLE_NAME = "downloadData";
    public static final String Col_Id = "ID";
    public static final String Col_DownloadId = "DOWNLOAD_ID";
    public static final String Col_Size = "SIZE";
    public static final String Col_Pause = "PAUSE";
    public static final String Col_Resume = "RESUME";
    public static final String Col_Cancel = "CANCEL";

    public DownloadData(Context context) {
        super(context, DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " DOWNLOAD_ID TEXT,SIZE TEXT, PAUSE TEXT, RESUME TEXT,CANCEL TEXT )";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP IF TABLE EXISTS" + TABLE_NAME);

    }

    public boolean addDownloadDetails(String downloadId,String size,String pause,String resume,String cancel){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_DownloadId,downloadId);
        contentValues.put(Col_Size,size);
        contentValues.put(Col_Pause,pause);
        contentValues.put(Col_Resume,resume);
        contentValues.put(Col_Cancel,cancel);

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

    public void DeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);
        db.close();
    }
}
