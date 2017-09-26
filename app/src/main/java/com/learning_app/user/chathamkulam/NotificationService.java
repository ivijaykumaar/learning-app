package com.learning_app.user.chathamkulam;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.learning_app.user.chathamkulam.Fragments.Drawer;

/**
 * Created by User on 9/6/2017.
 */

public class NotificationService {

    public static String ID = "id";

    // our actions for our buttons
    public static String ACTION_WIDGET_PAUSE = "pause";
    public static String ACTION_WIDGET_RESUME = "resume";
    public static String ACTION_WIDGET_CANCEL = "cancel";
    public static String ACTION_WIDGET_RETRY = "retry";

    void myNotify(Context context, String status, long id, String title, int progress, String text) {

        Log.d("###Am entered", "coool");

        String[] fileName = title.split("\\.");
        int uniqueId = (int) System.currentTimeMillis();

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.download);

        Intent pauseIntent = new Intent(context, switchButtonListener.class);
        pauseIntent.setAction(ACTION_WIDGET_PAUSE);
        pauseIntent.putExtra(ID, id);
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(context, uniqueId, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnPause, pendingPauseIntent);

        Intent resumeIntent = new Intent(context, switchButtonListener.class);
        resumeIntent.setAction(ACTION_WIDGET_RESUME);
        resumeIntent.putExtra(ID, id);
        PendingIntent pendingResumeIntent = PendingIntent.getBroadcast(context, uniqueId, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnResume, pendingResumeIntent);

        Intent cancelIntent = new Intent(context, switchButtonListener.class);
        cancelIntent.setAction(ACTION_WIDGET_CANCEL);
        cancelIntent.putExtra("subject", fileName[0]);
        cancelIntent.putExtra(ID, id);
        PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(context, uniqueId, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnCancel, pendingCancelIntent);

        Intent retryIntent = new Intent(context, switchButtonListener.class);
        retryIntent.setAction(ACTION_WIDGET_RETRY);
        retryIntent.putExtra(ID, id);
        PendingIntent pendingRetryIntent = PendingIntent.getBroadcast(context, uniqueId, retryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnRetry, pendingRetryIntent);

        Intent notificationIntent = new Intent(context, Drawer.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContent(remoteViews)
                .setSmallIcon(R.drawable.ic_home_app)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true);

        if (status.equals("Downloading")) {

            remoteViews.setTextViewText(R.id.txtStatus, fileName[0]);
            remoteViews.setProgressBar(R.id.number_progress_bar, 100, progress, false);
            remoteViews.setTextViewText(R.id.txtSize, text);
            remoteViews.setViewVisibility(R.id.btnResume, View.GONE);
            remoteViews.setViewVisibility(R.id.btnRetry, View.GONE);
            remoteViews.setViewVisibility(R.id.btnPause, View.VISIBLE);
        }

        if (status.equals("Paused")) {

            remoteViews.setTextViewText(R.id.txtStatus, fileName[0]);
            remoteViews.setProgressBar(R.id.number_progress_bar, 100, progress, false);
            remoteViews.setTextViewText(R.id.txtSize, text);
            remoteViews.setViewVisibility(R.id.btnResume, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.btnPause, View.GONE);
            remoteViews.setViewVisibility(R.id.btnRetry, View.GONE);
        }

        if (status.equals("Failed")) {

            remoteViews.setTextViewText(R.id.txtStatus, fileName[0]);
            remoteViews.setProgressBar(R.id.number_progress_bar, 100, progress, false);
            remoteViews.setTextViewText(R.id.txtSize, text);
            remoteViews.setViewVisibility(R.id.btnPause, View.GONE);
            remoteViews.setViewVisibility(R.id.btnResume, View.GONE);
            remoteViews.setViewVisibility(R.id.btnRetry, View.VISIBLE);
        }

        if (status.equals("Completed")) {

            remoteViews.setTextViewText(R.id.txtStatus, fileName[0]);
            remoteViews.setProgressBar(R.id.number_progress_bar, 0, 0, false);
            remoteViews.setTextViewText(R.id.txtSize, "Download complete");
            remoteViews.setViewVisibility(R.id.number_progress_bar, View.GONE);
            remoteViews.setViewVisibility(R.id.btnPause, View.GONE);
            remoteViews.setViewVisibility(R.id.btnCancel, View.GONE);
            remoteViews.setViewVisibility(R.id.btnResume, View.GONE);
            remoteViews.setViewVisibility(R.id.btnRetry, View.GONE);
            mBuilder.setOngoing(false);
        }

        Notification notification = mBuilder.build();
        mNotifyManager.notify((int) id, notification);
    }
}
