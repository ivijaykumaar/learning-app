package com.learning_app.user.chathamkulam.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Adapters.DashMainListAdapter;
import com.learning_app.user.chathamkulam.Model.DashboardModel.DashEntityObjects;
import com.learning_app.user.chathamkulam.Model.DashboardModel.DashSubjectEntity;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FMDashboard extends Fragment {

    //    MainList Items
    ListView dashListView;
    DashMainListAdapter dashMainListAdapter;

    ArrayList<DashEntityObjects> mainList;
    JSONArray mainArray;

    View view;

    public FMDashboard() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fm_dash, container, false);

        mainList = new ArrayList<DashEntityObjects>();
        mainArray = new JSONArray();


        final MyAsyncTask myAsyncTask = new MyAsyncTask(mainArray,mainList,getActivity());
        myAsyncTask.execute();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Dashboard");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_share).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);

    }


    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        JSONArray mainArray;
        ArrayList<DashEntityObjects> mainList;
        ProgressDialog progressDialog;
        Context context;

        public MyAsyncTask(JSONArray mainArray, ArrayList<DashEntityObjects> mainList, Context context) {
            this.mainArray = mainArray;
            this.mainList = mainList;
            this.progressDialog = new ProgressDialog(context);
            this.context = context;
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
        protected Void doInBackground(Void... params) {

//            this method will be running on background thread so don't update UI frome here

//            Get Values From DataBase
            StoreEntireDetails storeEntireDetails = new StoreEntireDetails(context);
            Cursor mainCursor = storeEntireDetails.groupMainDetails();

//        Create Main Array
            mainArray = new JSONArray();
            if (mainCursor.getCount() != 0){

                if (mainCursor.moveToFirst()){

                    do {
                        try {
//                        Create Main Object
                            JSONObject mainJsonObject = new JSONObject();

//                        Create Sub Array
                            JSONArray subArray = new JSONArray();

                            mainJsonObject.put("country",mainCursor.getString(1));
                            mainJsonObject.put("university", mainCursor.getString(2));
                            mainJsonObject.put("course",mainCursor.getString(3));
                            mainJsonObject.put("semester",mainCursor.getString(4));

                            Cursor cursor = storeEntireDetails.groupSubDetails(mainCursor.getString(6));

                            if (cursor.getCount() != 0){

                                if (cursor.moveToFirst()){

                                    do {

//                                    Create Sub Object
                                        JSONObject subJsonObject = new JSONObject();
                                        subJsonObject.put("subject_name",cursor.getString(5));
                                        subJsonObject.put("subject_id",cursor.getString(6));
                                        subJsonObject.put("subject_no",cursor.getString(7));

//                                    Add SubObject To SubArray
                                        subArray.put(subJsonObject);
                                    }while (cursor.moveToNext());
                                }
                            }
//                        Add SubArray To MainObject
                            mainJsonObject.put("subject_details",subArray);

                            mainArray.put(mainJsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }while(mainCursor.moveToNext());

                    Log.d("jsonValue", String.valueOf(mainArray));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            this method will be running on UI thread
            if (progressDialog.isShowing()){
                this.progressDialog.dismiss();

//                ListOut the DataBase Values
                ObjectMapper mapper = new ObjectMapper();
                List<DashEntityObjects> dashEntityObjects = new ArrayList<>();

                List<DashSubjectEntity> subList = new ArrayList<DashSubjectEntity>();

                try {
                    dashEntityObjects =  mapper.readValue(String.valueOf(mainArray), new TypeReference<List<DashEntityObjects>>(){});
                } catch (IOException e) {
                    e.printStackTrace();
                }

                DashEntityObjects objects = new DashEntityObjects();
                DashSubjectEntity dashSubjectEntity = new DashSubjectEntity();

                for (DashEntityObjects entityObject : dashEntityObjects){

                    entityObject.setCountry(entityObject.getCountry());
                    entityObject.setUniversity(entityObject.getUniversity());
                    entityObject.setCourse(entityObject.getCourse());
                    entityObject.setSemester(entityObject.getSemester());

                    Log.d("CountryName: ", entityObject.getCountry());
                    Log.d("University: ", entityObject.getUniversity());
                    Log.d("Course: ", entityObject.getCourse());
                    Log.d("Semester: ",entityObject.getSemester());

                    for(int i=0;i<entityObject.getSubject_details().size();i++){

                        dashSubjectEntity.setSubject_name(entityObject.getSubject_details().get(i).getSubject_name());
                        dashSubjectEntity.setSubject_id(entityObject.getSubject_details().get(i).getSubject_id());
                        dashSubjectEntity.setSubject_no(entityObject.getSubject_details().get(i).getSubject_no());

                        Log.d("subjectName: ", entityObject.getSubject_details().get(i).getSubject_name());
                        subList.add(dashSubjectEntity);
                    }

                    objects.setSubject_details(subList);

                    mainList.add(entityObject);
                }

//                Initialize views

                if (getActivity()!=null){

                    dashListView = (ListView) view.findViewById(R.id.dashMainListView);
                    dashMainListAdapter = new DashMainListAdapter(getActivity(),mainList);
                    dashListView.setAdapter(dashMainListAdapter);
                    dashMainListAdapter.notifyDataSetChanged();
                }

            }
        }
    }
}
