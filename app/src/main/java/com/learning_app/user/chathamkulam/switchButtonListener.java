package com.learning_app.user.chathamkulam;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;
import com.tonyodev.fetch.Fetch;

import static com.learning_app.user.chathamkulam.NotificationService.ACTION_WIDGET_CANCEL;
import static com.learning_app.user.chathamkulam.NotificationService.ACTION_WIDGET_PAUSE;
import static com.learning_app.user.chathamkulam.NotificationService.ACTION_WIDGET_RESUME;
import static com.learning_app.user.chathamkulam.NotificationService.ACTION_WIDGET_RETRY;
import static com.learning_app.user.chathamkulam.NotificationService.ID;

/**
 * Created by User on 9/6/2017.
 */

public class switchButtonListener extends BroadcastReceiver {

    String[] fileName;

    public switchButtonListener() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Fetch fetch = Fetch.newInstance(context);
        long id = intent.getLongExtra(ID, 0);
        String subject = intent.getStringExtra("subject");

        if (subject != null) {
            fileName = subject.split("_");
        }

        if (id != 0) {

            if (intent.getAction().equals(ACTION_WIDGET_PAUSE)) {

                fetch.pause(id);
                Toast.makeText(context, "Paused", Toast.LENGTH_SHORT).show();

            } else if (intent.getAction().equals(ACTION_WIDGET_RESUME)) {

                fetch.resume(id);
                Toast.makeText(context, "Resumed", Toast.LENGTH_SHORT).show();

            } else if (intent.getAction().equals(ACTION_WIDGET_CANCEL)) {

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel((int) id);
                StoreEntireDetails storeEntireDetails = StoreEntireDetails.getInstance(context);

                if (storeEntireDetails.ifExists(fileName[1])) {
                    Log.d("subjectStatus", "Exists " + fileName[1]);
                    storeEntireDetails.removeExpireSubject(fileName[1]);
                } else {

                    Log.d("subjectStatus", "not exists " + fileName[1]);
                }

                fetch.remove(id);
                Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show();

            } else if (intent.getAction().equals(ACTION_WIDGET_RETRY)) {

                fetch.retry(id);
                Toast.makeText(context, "Retrying", Toast.LENGTH_SHORT).show();

            }
        }
    }
}

