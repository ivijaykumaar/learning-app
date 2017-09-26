package com.learning_app.user.chathamkulam;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.listener.FetchListener;
import com.tonyodev.fetch.request.Request;
import com.tonyodev.fetch.request.RequestInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.learning_app.user.chathamkulam.Apis.API_SUBSCRIPTION;
import static com.learning_app.user.chathamkulam.Apis.PROJECT_CODE;
import static com.learning_app.user.chathamkulam.Apis.SEM_NO;
import static com.learning_app.user.chathamkulam.Apis.SUB_NAME;
import static com.learning_app.user.chathamkulam.Apis.SUB_NO;
import static com.learning_app.user.chathamkulam.Apis.TYPE;
import static com.learning_app.user.chathamkulam.Model.Constants.PHONENUMBER;
import static com.learning_app.user.chathamkulam.Registration.Registration.deleteCache;

/**
 * Created by User on 9/1/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class FetchDownloadManager implements Runnable {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET
    };

    private String downloadUrl, country, university, course, semester, subjectId, subjectNumber,
            subject, sub_cost, trial, duration, notesCount, qbankCount, videoCount, type, validityTill;

    private Context context;
    private String fileSizee;
    private String fileName;
    private String getUrls;
    private String statuss;
    private StoreEntireDetails storeEntireDetails;
    private Fetch fetch;
    private long downloadId;

    private NotificationService notificationService = new NotificationService();

    public FetchDownloadManager() {
    }

    public FetchDownloadManager(final String downloadUrl, final String country, final String university, final String course, final String semester,
                                final String subjectId, final String subjectNumber, final String subject, final String sub_cost, final String trial,
                                final String duration, final String notesCount, final String qbankCount, final String videoCount, final Context context, final String type) {
        this.downloadUrl = downloadUrl;
        this.country = country;
        this.university = university;
        this.course = course;
        this.semester = semester;
        this.subjectId = subjectId;
        this.subjectNumber = subjectNumber;
        this.subject = subject;
        this.sub_cost = sub_cost;
        this.trial = trial;
        this.duration = duration;
        this.notesCount = notesCount;
        this.qbankCount = qbankCount;
        this.videoCount = videoCount;
        this.context = context;
        this.type = type;
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }

            zipFile.delete();

        } finally {

            zipFile.delete();
            zis.close();
        }
    }

    @Override
    public void run() {

        deleteCache(context);
        storeEntireDetails = StoreEntireDetails.getInstance(context);

        fetch = Fetch.newInstance(context);
        getUrls = downloadUrl.trim().replaceAll("\\s+", "%20");
        fileName = downloadUrl.trim().substring(downloadUrl.lastIndexOf("/") + 1);

//        Save Internal Storage
        File mydir = context.getDir("Chathamkulam", Context.MODE_PRIVATE); //Creating an internal dir;
        if (!mydir.exists()) {
            mydir.mkdir();
        }

        Request request = new Request(getUrls, String.valueOf(mydir), fileName).setPriority(Fetch.PRIORITY_HIGH);
        RequestInfo requestInfo = fetch.get(request);
        downloadId = fetch.enqueue(request); //Save downloadId....

        if (downloadId != Fetch.ENQUEUE_ERROR_ID) {
            //Download was successfully queued for download.
            Log.d("downloadStatus", "Download was successfully queued for download.");

            if (!storeEntireDetails.ifExists(subject)) {
                if (type.equals("trial") || type.equals("buy")){
                    String semesterr = semester.replaceAll("[^0-9]", "");
                    subscription(String.valueOf(downloadId),getUrls, context, subjectId, semesterr, subjectNumber, subject, type);
                }
            }
        }

        Log.d("destination", getUrls + " " + fileName + " " + request.getFilePath());

        fetch.addFetchListener(new FetchListener() {

            @Override
            public void onUpdate(long id, int status, int progress, long downloadedBytes, long fileSize, int error) {

                if (downloadId == id && status == Fetch.STATUS_DOWNLOADING) {

                    String file = fetch.get(id).getFilePath();
                    String fileNamee = file.substring(file.lastIndexOf("/") + 1);

                    statuss = "onProgress";
                    fileSizee = String.valueOf((downloadedBytes / 1024) / 1024) + "/" + String.valueOf((fileSize / 1024) / 1024) + " MB";

                    Log.d("##status", "onProgress");
                    notificationService.myNotify(context, "Downloading", id, "Downloading_" + fileNamee, progress, fileSizee);

                    boolean IsEntry = storeEntireDetails.updateData(country, university, course, semester, subjectId, subjectNumber, subject, sub_cost,
                            trial, duration, notesCount, qbankCount, videoCount,getUrls, String.valueOf(progress), statuss, String.valueOf(id));

                    if (IsEntry) {
                        Log.d("##status", "Successfully Updated");

                    } else {
                        Log.d("##status", "Update failed");
                    }

                } else if (error != Fetch.NO_ERROR) {
                    try {

                        String file = fetch.get(id).getFilePath();
                        String fileNamee = file.substring(file.lastIndexOf("/") + 1);

                        statuss = "onFailed";
                        fileSizee = String.valueOf((downloadedBytes / 1024) / 1024) + "/" + String.valueOf((fileSize / 1024) / 1024) + " MB";

                        Log.d("NO_ERROR", String.valueOf(error));
                        notificationService.myNotify(context, "Failed", id, "Failed_" + fileNamee, progress, fileSizee);

                        boolean IsEntry = storeEntireDetails.updateData(country, university, course, semester, subjectId, subjectNumber, subject, sub_cost,
                                trial, duration, notesCount, qbankCount, videoCount,getUrls, String.valueOf(progress), statuss,String.valueOf(id));
                        if (IsEntry) {
                            Log.d("##status", "Successfully Updated");

                        } else {
                            Log.d("##status", "Update failed");
                        }

//                    fetch.removeRequest(id);

                        if (error == Fetch.ERROR_HTTP_NOT_FOUND) {

                            String filee = fetch.get(id).getFilePath();
                            String fileNameee = file.substring(file.lastIndexOf("/") + 1);

                            statuss = "onFailed";
                            fileSizee = String.valueOf((downloadedBytes / 1024) / 1024) + "/" + String.valueOf((fileSize / 1024) / 1024) + " MB";

                            Log.d("ERROR_HTTP_NOT_FOUND", String.valueOf(error));
                            notificationService.myNotify(context, "Failed", id, "Failed_" + fileNameee, progress, fileSizee);

                            String[] Namee = fileNameee.split("\\.");

                            StoreEntireDetails storeEntireDetails = StoreEntireDetails.getInstance(context);

                            if (storeEntireDetails.ifExists(Namee[0])) {
                                Log.d("subjectStatus", "Exists " + Namee[0]);
                                storeEntireDetails.removeExpireSubject(Namee[0]);
                            } else {

                                Log.d("subjectStatus", "not exists " + Namee[0]);
                            }
                            fetch.remove(id);
                        }
                    }
                    catch(Exception ex)
                    {
                        System.out.print("Exception:".concat( ex.getMessage()));
                    }
                }


                if (status == Fetch.STATUS_PAUSED) {

                    String file = fetch.get(id).getFilePath();
                    String fileNamee = file.substring(file.lastIndexOf("/") + 1);

                    statuss = "onDownloadPaused";
                    fileSizee = String.valueOf((downloadedBytes / 1024) / 1024) + "/" + String.valueOf((fileSize / 1024) / 1024) + " MB";

                    Log.d("##status", "onDownloadPaused");
                    notificationService.myNotify(context, "Paused", id, "Paused_ " + fileNamee, progress, fileSizee);

                    boolean IsEntry = storeEntireDetails.updateData(country, university, course, semester, subjectId, subjectNumber, subject, sub_cost,
                            trial, duration, notesCount, qbankCount, videoCount,getUrls, String.valueOf(progress), statuss,String.valueOf(id));

                    if (IsEntry) {
                        Log.d("##status", "Successfully Updated");
                    } else {
                        Log.d("##status", "Update failed");
                    }

                }

                if (status == Fetch.STATUS_DONE) {

                    String file = fetch.get(id).getFilePath();
                    final String fileNamee = file.substring(file.lastIndexOf("/") + 1);

                    statuss = "onCompleted";
                    fileSizee = String.valueOf((downloadedBytes / 1024) / 1024) + "/" + String.valueOf((fileSize / 1024) / 1024) + " MB";

                    Log.d("##status", "onCompleted  " + fileName);
                    notificationService.myNotify(context, "Completed", id, "Completed_" + fileNamee, progress, fileSizee);

                    boolean IsEntry = storeEntireDetails.updateData(country, university, course, semester, subjectId, subjectNumber, subject, sub_cost,
                            trial, duration, notesCount, qbankCount, videoCount, getUrls, String.valueOf(progress), statuss,String.valueOf(id));

                    if (IsEntry) {
                        Log.d("##status", "Successfully Updated");

                    } else {
                        Log.d("##status", "Update failed");
                    }

                    fetch.removeRequest(id);

                    Thread thread = new Thread() {
                        public void run() {

                            File mydir = context.getDir("Chathamkulam", Context.MODE_PRIVATE); //Creating an internal dir;
                            File myZipFile = new File(mydir, fileNamee + ".zip"); //Getting a file within the dir.

                            File myUnZipFile = new File(mydir, fileNamee);

                            if (myZipFile.exists()) {
//                        Log.d("fileDetails", "file exists!"+"  "+myZipFile);

                                try {
                                    unzip(myZipFile, myUnZipFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    thread.start();
                }

                if (status == Fetch.STATUS_ERROR) {

                    String file = fetch.get(id).getFilePath();
                    final String fileNamee = file.substring(file.lastIndexOf("/") + 1);

                    statuss = "onFailed";
                    fileSizee = String.valueOf((downloadedBytes / 1024) / 1024) + "/" + String.valueOf((fileSize / 1024) / 1024) + " MB";

                    Log.d("STATUS_ERROR", String.valueOf(error));
                    notificationService.myNotify(context, "Failed", id, "Failed_" + fileNamee, progress, fileSizee);

                    boolean IsEntry1 = storeEntireDetails.updateData(country, university, course, semester, subjectId, subjectNumber, subject, sub_cost,
                            trial, duration, notesCount, qbankCount, videoCount, getUrls, String.valueOf(progress), statuss,String.valueOf(id));
                    if (IsEntry1) {
                        Log.d("##status", "Successfully Updated");

                    } else {
                        Log.d("##status", "Update failed");
                    }
                }
            }
        });
    }

    private void myNotify(int id, Context context, Class resultAct, String title, int progress, String text) {

        Intent notificationIntent = new Intent(context, resultAct);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_download);

        mBuilder.setProgress(100, progress, false);
//        mNotifyManager.notify(id, mBuilder.build());

        if (progress == 100) {

            // When the loop is finished, updates the notification
            mBuilder.setContentText("Download complete")
                    .setContentTitle(title)
                    // Removes the progress bar
                    .setProgress(0, 0, false);

        }
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void subscription(final String id,final String downloadUrl, final Context myContext, final String subjectId, final String semNo,
                             final String subNo, final String subName, final String type) {

        deleteCache(myContext);
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
//        final ProgressDialog loading = ProgressDialog.show(context,"Authenticating","Please wait while we check subscription details", false,false);

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, API_SUBSCRIPTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

//                loading.dismiss();
                Log.v("subscriptionResponse", response);

                if (response != null) {

                    String[] separated = response.split("-");
                    String year = separated[0];
                    String month = separated[1];
                    String day = separated[2];

                    validityTill = day + "/" + month + "/" + year;

                    statuss = "onProgress";

                    if (type.equals("trial")){

                        boolean IsEntry = storeEntireDetails.addData(country, university, course, semester, subjectId, subjectNumber, subject, sub_cost,
                                trial, duration, notesCount, qbankCount, videoCount,downloadUrl, validityTill, "0", statuss,String.valueOf(id));
                        if (IsEntry) {
                            Log.d("##status", "Successfully Added");

                        } else {
                            Log.d("##status", "Add failed");
                        }
                    }

                    if (type.equals("buy")){

                        if (storeEntireDetails.ifExists(subject)){
                            storeEntireDetails.updateValidityDate(subject,validityTill);

                        } else {

                            boolean IsEntry = storeEntireDetails.addData(country, university, course, semester, subjectId, subjectNumber, subject, sub_cost,
                                    trial, duration, notesCount, qbankCount, videoCount,downloadUrl, validityTill, "0", statuss,String.valueOf(id));
                            if (IsEntry) {
                                Log.d("##status", "Successfully Added");
                            } else {
                                Log.d("##status", "Add failed");
                            }
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.v("volleyError", error.getMessage() + " ");
//                loading.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> Hashmap = new HashMap<>();

                RegisterMember registerMember = RegisterMember.getInstance(myContext);
                Cursor cursorResult = registerMember.getDetails();

                while (cursorResult.moveToNext()) {
                    String MobileNumber = cursorResult.getString(3);

                    if (MobileNumber != null) {

                        Hashmap.put(PHONENUMBER, MobileNumber);
                        Hashmap.put(PROJECT_CODE, subjectId);
                        Hashmap.put(SEM_NO, semNo);
                        Hashmap.put(SUB_NO, subNo);
                        Hashmap.put(SUB_NAME, subName);
                        Hashmap.put(TYPE, type);

                        Log.v("HashValues", Hashmap.toString());

                    } else {

                        Log.v("Number Filed", null);
                    }
                }
                return Hashmap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void verifyStoragePermissions(Context activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}