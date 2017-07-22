package com.learning_app.user.chathamkulam.Model.BackgroundWork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.learning_app.user.chathamkulam.BuildConfig;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by User on 7/5/2017.
 */

public class VersionChecker extends AsyncTask<String, String, String> {

    private String newVersion;
    private Context context;

    public VersionChecker(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newVersion;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null) {

            String appVersion = BuildConfig.VERSION_NAME;

            if (!appVersion.equals(result)) {

                AlertDialog.Builder alertQb = new AlertDialog.Builder(context);
                RegisterMember registerMember = RegisterMember.getInstance(context);
                Cursor cursorResult = registerMember.getDetails();

                while (cursorResult.moveToNext()) {
                    String userName = cursorResult.getString(1);

                    String alertMessage = "Update " + result + " is available to download. " +
                            "Downloading the latest update you will get latest features and improvements!";
                    alertQb.setTitle("Hi " + userName + " new update available!");
                    alertQb.setMessage(alertMessage);
                    alertQb.setIcon(R.drawable.ic_question);
                    alertQb.setPositiveButton("UPDATE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }

                                }
                            });

                    alertQb.setNegativeButton("Remind Later",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertQb.show();
                }
                cursorResult.close();
            }
        }
    }
}