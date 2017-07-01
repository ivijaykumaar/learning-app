package com.learning_app.user.chathamkulam;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.learning_app.user.chathamkulam.Model.FileCrypto;

import java.io.File;

/**
 * Created by User on 6/30/2017.
 */

public class EncryptService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code

                try {
                    File myFinalDir = (File) intent.getExtras().get("Key_fileName");
                    FileCrypto.encrypt(myFinalDir, myFinalDir);
                    Log.d("fileCrypto","Encrypted");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("fileCrypto Exception",e.getMessage());
                }
            }

        });

        Log.d("serviceResult","serviceStarted");

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("serviceResult","serviceStopped");

    }
}
