package com.learning_app.user.chathamkulam.Model.BackgroundWork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;

import java.io.File;

/**
 * Created by User on 5/4/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        Toast.makeText(context,"Alarm activated",Toast.LENGTH_LONG).show();

        String currentSubjectName = intent.getStringExtra("Key_currentSub");
        String rootFile = "Chathamkulam"+"/"+currentSubjectName;
        File rootPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),rootFile);

        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.v("Cannot use storage","Cannot use storage");
        }

        if(rootPath.exists()) {

            deleteRecursive(rootPath);
            StoreEntireDetails storeEntireDetails = new StoreEntireDetails(context);
            storeEntireDetails.removeExpireSubject(currentSubjectName);

            Log.v("Delete Operation ","Success");
        }else {

            Log.v("Delete Operation ","Failed");
        }
    }

   public static void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()){
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        }

        fileOrDirectory.delete();
    }
}
