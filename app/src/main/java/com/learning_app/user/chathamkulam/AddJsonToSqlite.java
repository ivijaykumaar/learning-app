package com.learning_app.user.chathamkulam;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreEntityObjects;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreSubjectEntity;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 9/19/2017.
 */

public class AddJsonToSqlite extends AsyncTask<Object, Object, Boolean> {

    private ProgressDialog progressDialog;
    private Context context;
    private Class intentClass;
    private String subscribedDetails;

    private boolean isEntry;

    public AddJsonToSqlite(Context context, Class intentClass, String subscribedDetails) {
        this.progressDialog = new ProgressDialog(context);
        this.context = context;
        this.intentClass = intentClass;
        this.subscribedDetails = subscribedDetails;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //this method will be running on UI thread.
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setTitle("Fetching Data");
        this.progressDialog.setMessage("Please give a movement while we process your data...");
        this.progressDialog.show();
    }


    @Override
    protected Boolean doInBackground(Object... params) {

        StoreEntireDetails storeEntireDetails = StoreEntireDetails.getInstance(context);

        ObjectMapper mapper = new ObjectMapper();
        List<StoreEntityObjects> storeEntityObjects = new ArrayList<>();

        try {
            storeEntityObjects = mapper.readValue(String.valueOf(subscribedDetails), new TypeReference<List<StoreEntityObjects>>() {

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        StoreEntityObjects objects = new StoreEntityObjects();
        StoreSubjectEntity storeSubjectEntity = new StoreSubjectEntity();

        for (StoreEntityObjects entityObject : storeEntityObjects) {

            String country = entityObject.getCountry();
            String university = entityObject.getUniversity();
            String course = entityObject.getCourse();
            String semester = entityObject.getSem_no();
            String subjectId = entityObject.getId();
            String subject = entityObject.getSubject_name();
            String subjectNumber = entityObject.getSub_no();
            String sub_cost = entityObject.getSub_cost();
            String trial = entityObject.getTrial();
            String duration = entityObject.getSize();
            String notesCount = entityObject.getFile_count();
            String qbankCount = entityObject.getQa_count();
            String videoCount = entityObject.getVideo_count();
            String downloadUrl = entityObject.getUrl();
            String validityTill = entityObject.getExpiry_date();

            isEntry = storeEntireDetails.addData(country, university, course, semester, subjectId, subjectNumber, subject, sub_cost,
                    trial, duration, notesCount, qbankCount, videoCount, downloadUrl, validityTill,"100","onCompleted", String.valueOf(0));
            if (isEntry) {
                Log.d("##status", "Successfully Added");
            } else {
                Log.d("##status", "Add failed");
            }
//            Log.d("#country", country);
//            Log.d("#university", university);
//            Log.d("#course", course);
//            Log.d("#sem", semester);
//            Log.d("#subjectId", subjectId);
//            Log.d("#subjectNumber", subjectNumber);
//            Log.d("#subject", subject);
//            Log.d("#subjectCost", sub_cost);
//            Log.d("#trial", trial);
//            Log.d("#duration", duration);
//            Log.d("#notes_count", notesCount);
//            Log.d("#qbank_count", qbankCount);
//            Log.d("#video_count", videoCount);
//            Log.d("#zip_url", downloadUrl);
//            Log.d("#validityTill", validityTill);
        }

        return isEntry;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);

        if (isEntry){
            progressDialog.dismiss();
            Intent intent = new Intent(context, intentClass);
            context.startActivity(intent);
        } else {
            progressDialog.dismiss();
            Intent intent = new Intent(context, intentClass);
            context.startActivity(intent);
        }
    }
}
