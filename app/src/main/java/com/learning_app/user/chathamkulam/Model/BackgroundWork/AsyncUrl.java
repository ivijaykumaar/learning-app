package com.learning_app.user.chathamkulam.Model.BackgroundWork;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by User on 3/31/2017.
 */

public class AsyncUrl extends AsyncTask<String, String, String> {

    public static ArrayList url_arrayList = new ArrayList<String>();
    private Context context;
    private HashMap<String, String> params;
    private ArrayList<String> arrayList;
    private ProgressDialog loading;
    private PowerManager.WakeLock mWakeLock;

    public AsyncUrl(Context context, HashMap<String, String> params, ArrayList urlArrayList, ProgressDialog loading) {

        this.context = context;
        this.params = params;
        arrayList = urlArrayList;
        this.loading = loading;
    }


    @Override
    protected String doInBackground(String... strings) {

        StringBuilder result = null;
        URL url;
        try {
            url = new URL(strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(params));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }

                if (result != null) {

                    url_arrayList.add(result);
                    arrayList.add(String.valueOf(result));
                    Log.v("FileUrl Result", url_arrayList.toString());

                } else {
                    Toast.makeText(context, "Bad internet connection", Toast.LENGTH_LONG).show();
                }

                Log.v("Response", String.valueOf(result));

            } else {

                loading.dismiss();
                Log.v("Error Response", String.valueOf(result));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        loading = ProgressDialog.show(context, "Processing....", "Loading please wait....", false, false);
        loading.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mWakeLock.release();
        loading.dismiss();

        Log.v("PostExecute", "Success");

    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}