package com.learning_app.user.chathamkulam.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 4/30/2017.
 */



public class VideoHandler extends SQLiteOpenHelper {

    private static VideoHandler mInstance = null;

    public static final String DATABASE_NAME = "VideoHandler.db";

    private static final String TABLE_VIDEO_HANDLER = "VideoHandler";
    private static final String Col_Id = "ID";
    private static final String Col_TopicName = "TOPIC_NAME";
    private static final String Col_Total_Time = "TOTAL_TIME";
    private static final String Col_Count = "COUNT";

    public static VideoHandler getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new VideoHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }


    public VideoHandler(Context context) {
        super(context, DATABASE_NAME,null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_VIDEO_HANDLER + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " TOPIC_NAME TEXT, TOTAL_TIME TEXT, COUNT TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP IF TABLE EXISTS" + TABLE_VIDEO_HANDLER);

    }

    public boolean AddTopicDetails(String topicName, int totalTime,int count){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_TopicName,topicName);
        contentValues.put(Col_Total_Time,totalTime);
        contentValues.put(Col_Count,count);

        long result = db.insert(TABLE_VIDEO_HANDLER,null,contentValues);

        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean UpdateData(String topicName, int totalTime,int count){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_TopicName,topicName);
        contentValues.put(Col_Total_Time,totalTime);
        contentValues.put(Col_Count,count);

        db.update(TABLE_VIDEO_HANDLER,contentValues," TOPIC_NAME = ? ",new String[] {topicName});
        return true;
    }

    public boolean ifExists(String topicName) {

        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String checkQuery = "SELECT " + Col_TopicName + " FROM " + TABLE_VIDEO_HANDLER + " WHERE " + Col_TopicName + "= '"+ topicName + "'";
        cursor= db.rawQuery(checkQuery,null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public Cursor getRow(String topicName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_VIDEO_HANDLER +" WHERE TOPIC_NAME = ?", new String[]{topicName});

    }


    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_VIDEO_HANDLER ,null);
    }

    public void DeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_VIDEO_HANDLER);
        db.close();
    }
}
