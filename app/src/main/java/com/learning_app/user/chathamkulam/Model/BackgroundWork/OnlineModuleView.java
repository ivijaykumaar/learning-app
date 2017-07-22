package com.learning_app.user.chathamkulam.Model.BackgroundWork;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by User on 6/23/2017.
 */

public class OnlineModuleView extends AsyncTask<String, String, String> {

    private String requestURL;
    private HashMap postDataParams;
    private Activity context;
    private Class intentClass;
    private String subjectName;

    String moduleItems;
    String searchModuleItems;

    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog loading;

    public OnlineModuleView(String requestURL, HashMap postDataParams, Activity context, Class intentClass, String subjectName) {
        this.requestURL = requestURL;
        this.postDataParams = postDataParams;
        this.context = context;
        this.intentClass = intentClass;
        this.subjectName = subjectName;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        loading = ProgressDialog.show(context, "Processing.....","Please wait while we take you online page!!", false, false);
        loading.show();
    }

    @Override
    protected String doInBackground(String... strings) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            Log.d("Mapping values", String.valueOf(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("moduleResponse", response);
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        // Handle/Update UI Part

        mWakeLock.release();
        loading.dismiss();

        try {
            JSONObject json = new JSONObject(result);

            if (json.has("result1") || json.has("result2")){

                moduleItems = json.getString("result1");
                searchModuleItems = json.getString("result2");

                Log.d("moduleItems",moduleItems);
                Log.d("searchModuleItems",searchModuleItems);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (result != null){
            Intent intent = new Intent(context,intentClass);
            intent.putExtra("Key_OnlineJson",moduleItems);
            intent.putExtra("Key_OnlineSearchJson",searchModuleItems);
            intent.putExtra("Key_OnlineJsonVideo",result);
            intent.putExtra("Key_subjectName",subjectName);
            context.startActivity(intent);
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
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

