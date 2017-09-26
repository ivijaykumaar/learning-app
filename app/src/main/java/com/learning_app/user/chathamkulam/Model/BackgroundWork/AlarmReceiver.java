package com.learning_app.user.chathamkulam.Model.BackgroundWork;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;

import java.io.File;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by User on 5/4/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

//        Toast.makeText(context,"Alarm activated",Toast.LENGTH_LONG).show();

        Thread thread1 = new Thread() {
            public void run() {

                String currentSubjectName = intent.getStringExtra("Key_currentSub");
                File mydir = context.getDir("Chathamkulam", Context.MODE_PRIVATE); //Creating an internal dir;
                File rootPath = new File(mydir, currentSubjectName);

                if (rootPath.exists()) {

                    deleteRecursive(rootPath);
                    StoreEntireDetails storeEntireDetails = new StoreEntireDetails(context);
                    storeEntireDetails.removeExpireSubject(currentSubjectName);

                    Log.v("Delete Operation ", "Success");

                } else {

                    Log.v("Delete Operation ", "Failed");
                }

            }
        };
        thread1.start();
    }

//    public static void deleteRecursive(File fileOrDirectory) {
//
//        if (fileOrDirectory.isDirectory()) {
//            for (File child : fileOrDirectory.listFiles())
//                deleteRecursive(child);
//        }
//
//        fileOrDirectory.delete();
//    }

    public static boolean deleteRecursive(File path) {
        // TODO Auto-generated method stub
        if( path.exists() ) {
            if (path.isFile()){
                path.delete();
            } else {
                File[] files = path.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteRecursive(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return(path.delete());
    }
}
