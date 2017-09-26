package com.learning_app.user.chathamkulam.PaymentGateway;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.learning_app.user.chathamkulam.FetchDownloadManager;
import com.learning_app.user.chathamkulam.Fragments.Drawer;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.CheckingCards;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.type;
import static com.learning_app.user.chathamkulam.Apis.API_SUBSCRIPTION;
import static com.learning_app.user.chathamkulam.Apis.PROJECT_CODE;
import static com.learning_app.user.chathamkulam.Apis.SEM_NO;
import static com.learning_app.user.chathamkulam.Apis.SUB_NAME;
import static com.learning_app.user.chathamkulam.Apis.SUB_NO;
import static com.learning_app.user.chathamkulam.Apis.TYPE;
import static com.learning_app.user.chathamkulam.Model.Constants.PHONENUMBER;
import static com.learning_app.user.chathamkulam.Model.TestUrls.TEST_PAYMENT;

public class StatusActivity extends Activity {

    CheckingCards checkingCards;
    StoreEntireDetails storeEntireDetails;

    String country, university, course, semester, subjectId, subjectNumber, subject,
            sub_cost, trial, duration, notesCount, qbankCount, videoCount, zipUrl;

    String status;

    RequestQueue requestQueue;
    ProgressDialog loading;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_status);

        checkingCards = CheckingCards.getInstance(this);
        storeEntireDetails = StoreEntireDetails.getInstance(this);

        Intent mainIntent = getIntent();
        TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
        status = mainIntent.getStringExtra("transStatus");
        txtStatus.setText(status);

        if (status.equals("Transaction Successful!")) {

            Cursor cursor = checkingCards.getCheckData();
            if (cursor.getCount() != 0) {

                while (cursor.moveToNext()) {

                    country = cursor.getString(2);
                    university = cursor.getString(3);
                    course = cursor.getString(4);
                    semester = cursor.getString(5);
                    subjectId = cursor.getString(6);
                    subjectNumber = cursor.getString(7);
                    subject = cursor.getString(8);
                    sub_cost = cursor.getString(9);
                    trial = cursor.getString(10);
                    duration = cursor.getString(11);
                    notesCount = cursor.getString(12);
                    qbankCount = cursor.getString(13);
                    videoCount = cursor.getString(14);
                    zipUrl = cursor.getString(15);

//                    requestQueue = Volley.newRequestQueue(this);
//
//                    loading = ProgressDialog.show(StatusActivity.this,
//                            "Checking", "Please wait your detail will be check", false, false);
//                    StringRequest stringRequest = new StringRequest(Request.Method.POST, API_SUBSCRIPTION, new Response.Listener<String>() {
//                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//                        @Override
//                        public void onResponse(String response) {
//
//                            loading.dismiss();
//                            Log.d("Response", response);
//
//                            if (response != null){
//
                                if (storeEntireDetails.ifExists(subject)){

//                                    Update validity date
                                    FetchDownloadManager fetch = new FetchDownloadManager();
                                    fetch.subscription(String.valueOf(0),zipUrl,getApplicationContext(),subjectId,semester,subjectNumber,subject,"buy");

//                                    storeEntireDetails.updateValidityDate(subject,response);

                                } else {

//                                    boolean IsEntry = storeEntireDetails.addData(country, university, course, semester, subjectId, subjectNumber, subject, sub_cost,
//                                            trial, duration, notesCount, qbankCount, videoCount, "0", response, "0","null");
//                                    if (IsEntry) {
//                                        Log.d("##status", "Successfully Added");
//
//                                    } else {
//                                        Log.d("##status", "Add failed");
//                                    }
////
                                    new Thread(new FetchDownloadManager(zipUrl, country, university, course, semester, subjectId, subjectNumber, subject,
                                            sub_cost, trial, duration, notesCount, qbankCount, videoCount,getApplicationContext(),"buy")).start();
//
                                }
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//
//                            loading.dismiss();
//                            Log.d("Response", error.getMessage());
//
//                        }
//                    }) {
//
//                        @Override
//                        protected Map<String, String> getParams() {
//                            Map<String, String> Hashmap = new HashMap<>();
//
//                            RegisterMember registerMember = RegisterMember.getInstance(getApplicationContext());
//                            Cursor cursorResult = registerMember.getDetails();
//
//                            while (cursorResult.moveToNext()) {
//                                String MobileNumber = cursorResult.getString(3);
//
//                                if (MobileNumber != null) {
//
//                                    Hashmap.put(PHONENUMBER, MobileNumber);
//                                    Hashmap.put(PROJECT_CODE, subjectId);
//                                    Hashmap.put(SEM_NO, semester);
//                                    Hashmap.put(SUB_NO, subjectNumber);
//                                    Hashmap.put(SUB_NAME, subject);
//                                    Hashmap.put(TYPE, "buy");
//
//                                    Log.v("HashValues", Hashmap.toString());
//
//                                } else {
//
//                                    Log.v("Number Filed", null);
//                                }
//                            }
//
//                            Log.d("mappingValue", Hashmap.toString());
//
//                            return Hashmap;
//                        }
//                    };
//                    requestQueue.add(stringRequest);
//
                }

            }
            cursor.close();
        }

        startActivity(new Intent(this, Drawer.class));
        showToast(status);
    }

    public void showToast(String msg) {
        Toast.makeText(this,msg, Toast.LENGTH_LONG).show();
    }
} 