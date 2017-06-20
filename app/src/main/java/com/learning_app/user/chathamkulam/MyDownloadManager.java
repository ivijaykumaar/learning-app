package com.learning_app.user.chathamkulam;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ProgressBar;

import com.learning_app.user.chathamkulam.Model.AsyncUrl;

import java.io.File;

/**
 * Created by User on 6/12/2017.
 */

public class MyDownloadManager extends AsyncTask<String,Integer,String> {

    private PowerManager.WakeLock mWakeLock;
    public int dl_progress;

    private Context context;
    private ProgressBar progressBar;
    private String subject;

    public MyDownloadManager(Context context,String subject) {
        this.context = context;
//        this.progressBar = progressBar;
        this.subject = subject;
    }


    @Override
    protected String doInBackground(String... sUrl) {

        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        for (int i = 0; i < AsyncUrl.url_arrayList.size(); i++) {

            String file_Url = String.valueOf(AsyncUrl.url_arrayList.get(i));

            final String fileType = file_Url.substring(file_Url.lastIndexOf(".") + 1);
            String file_Name = String.valueOf(AsyncUrl.name_arrayList.get(i));

            if (fileType.equals("mp4")){

                String[] separated = file_Name.split("_");
                String fileName = separated[0];
                String ModuleName = separated[1];
                String TopicName = separated[2];

                String DNAME = "Chathamkulam"+"/"+subject+"/"+ModuleName;
                File rootPath = new File(Environment.getExternalStorageDirectory().toString(), DNAME);
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }

                if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Log.v("Cannot use storage","Cannot use storage");
                }

                final File myFinalDir = new File(rootPath,TopicName);
                Uri uri = Uri.parse(file_Url);

                Uri destination = Uri.fromFile(myFinalDir);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationUri(destination);
                request.setAllowedOverRoaming(false);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                final Long downloadId = downloadManager.enqueue(request);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        boolean downloading = true;

                        while(downloading){

                            DownloadManager.Query q = new DownloadManager.Query();
                            q.setFilterById(downloadId);

                            Cursor cursor = downloadManager.query(q);
                            cursor.moveToFirst();

                            int bytes_downloaded = cursor.getInt(cursor
                                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                                    == DownloadManager.STATUS_SUCCESSFUL) {
                                downloading = false;
                            }

                            dl_progress = (int) ((bytes_downloaded * 100L) / bytes_total);
                            Log.d("download percentage", String.valueOf(dl_progress));

                            cursor.close();
                        }
                    }
                }).start();

            } else {

                String DNAME = "Chathamkulam"+"/"+subject;
                File rootPath = new File(Environment.getExternalStorageDirectory().toString(), DNAME);
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }

                if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Log.v("Cannot use storage","Cannot use storage");
                }

                final File myFinalDir = new File(rootPath,file_Name);
                Uri uri = Uri.parse(file_Url);

                Uri destination = Uri.fromFile(myFinalDir);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationUri(destination);
                request.setAllowedOverRoaming(false);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                final Long downloadId = downloadManager.enqueue(request);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        boolean downloading = true;

                        while(downloading){

                            DownloadManager.Query q = new DownloadManager.Query();
                            q.setFilterById(downloadId);

                            Cursor cursor = downloadManager.query(q);
                            cursor.moveToFirst();

                            int bytes_downloaded = cursor.getInt(cursor
                                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                                    == DownloadManager.STATUS_SUCCESSFUL) {
                                downloading = false;
                            }

                            dl_progress = (int) ((bytes_downloaded * 100L) / bytes_total);

                            if (dl_progress == 100){

                                if (!fileType.equals("jpg")){}{
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            //TODO your background code

                                            try {
                                                FileCrypto.encrypt(myFinalDir, myFinalDir);
                                                Log.d("fileCrypto","Encrypted");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Log.d("fileCrypto Exception",e.getMessage());
                                            }

                                        }
                                    });
                                }

                            }
                            cursor.close();
                        }
                    }
                }).start();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        // if we get here, length is known, now set indeterminate to false
        progressBar.setProgress(dl_progress);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();

        if (result != null)
            Log.d("Download error: ",result);
        else
            Log.d("Download Result","File downloaded");

//        StoreEntireDetails storeEntireDetails = new StoreEntireDetails(context);
//        boolean IsEntry = storeEntireDetails.addData(country,university,course,semester,subject,subjectId,subjectNumber);
//        if (IsEntry) {
//            Toast.makeText(context, "Successfully Added", Toast.LENGTH_LONG).show();
//
//        } else {
//            Toast.makeText(context, "Added failed", Toast.LENGTH_LONG).show();
//        }
//        Log.d("databaseValue",country+university+course+semester+subject+subjectId+subjectNumber);
//        subscription(context);

    }

//    public static void subscription(final Context myContext){
//
//        deleteCache(myContext);
//        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
//
//        final ProgressDialog loading = ProgressDialog.show(myContext, "Authenticating","Please wait while we check subscription details", false,false);
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST,SUBSCRIPTION, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                Log.v("Subscription Response",response);
//                loading.dismiss();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Log.v("Volley Error",error.getMessage());
//                loading.dismiss();
//
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams()  {
//                Map<String,String> Hashmap = new HashMap<String, String>();
//
//                RegisterMember registerMember = RegisterMember.getInstance(myContext);;
//                Cursor cursorResult = registerMember.getDetails();
//
//                while (cursorResult.moveToNext()) {
//                    String MobileNumber= cursorResult.getString(2);
//
//                    if (MobileNumber != null && file_Url != null) {
//                        Hashmap.put(PHONENUMBER, MobileNumber);
//                        Hashmap.put(DOWNLOAD_URL, file_Url);
//
//                        Log.v("Number Filed",MobileNumber);
//                        Log.v("Url Filed",file_Url);
//
//                    }else {
//
//                        Log.v("Number Filed",null);
//                    }
//                }
//                return Hashmap;
//            }
//        };
//        requestQueue.add(stringRequest);
//    }
}
