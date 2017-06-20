package com.learning_app.user.chathamkulam.Model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by User on 3/31/2017.
 */

public class AsyncUrl extends AsyncTask<String, String, String> {

    private Context context;
    private HashMap<String, String> params;
    private JSONArray jsonArray;
    private String Current_JsonArray;
    private String file_url;
    private String file_name;
    public static ArrayList  url_arrayList = new ArrayList<String>();
    public static ArrayList  name_arrayList = new ArrayList<String>();

    private ProgressDialog loading;


    private PowerManager.WakeLock mWakeLock;

    public AsyncUrl(Context context, HashMap<String, String> params, JSONArray jsonArray,
                    String current_JsonArray,String fileUrl,String fileName,
                    ArrayList urlArrayList,ArrayList nameArrayList,ProgressDialog loading) {
        this.context = context;
        this.params = params;
        this.jsonArray = jsonArray;
        this.Current_JsonArray = current_JsonArray;
        this.file_url = fileUrl;
        this.file_name = fileName;
        url_arrayList = urlArrayList;
        name_arrayList = nameArrayList;
        this.loading = loading;
    }


    @Override
    protected String doInBackground(String... strings) {

        String response = "";
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
                while ((line = br.readLine()) != null) {
                    response += line;
                    Log.v("Start",response);
                    JSONObject j = null;

                    try {
                        j = new JSONObject(response);
//                        String result = getJSONUrl(url);
                        jsonArray = j.getJSONArray(Current_JsonArray);

                        if (jsonArray != null){

                            for (int i = 0; i < jsonArray.length(); i++) {

                                try {
                                    JSONObject json = jsonArray.getJSONObject(i);

                                    url_arrayList.add(json.getString(file_url));
                                    name_arrayList.add(json.getString(file_name));

                                    Log.v("FileUrl Result",url_arrayList.toString());
                                    Log.v("FileName Result",name_arrayList.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    Log.v("AsyncUrl Error",e.getMessage());
                                }
                            }
                            Log.v("FileUrl Size", String.valueOf(url_arrayList.size()));
                            Log.v("FileName Size", String.valueOf(name_arrayList.size()));

                        }else {
                            Toast.makeText(context,"Bad internet connection",Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.v("JSONException", e.toString());
                    }
                    Log.v("Response",response);
                }
            } else {

                loading.dismiss();
                response = "Network Error";
                Log.v("Error Response", response);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        loading = ProgressDialog.show(context, "Processing.....","Loading please wait.....", false, false);
        loading.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mWakeLock.release();
        loading.dismiss();

        Log.v("PostExecute","Success");

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