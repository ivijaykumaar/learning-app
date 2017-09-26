package com.learning_app.user.chathamkulam.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Adapters.DashMainListAdapter;
import com.learning_app.user.chathamkulam.Model.BackgroundWork.AlarmReceiver;
import com.learning_app.user.chathamkulam.Model.DashboardModel.DashEntityObjects;
import com.learning_app.user.chathamkulam.Model.DashboardModel.DashSubjectEntity;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.SearchFilters.DashCardFilterAdapter;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;
import com.tonyodev.fetch.Fetch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static com.learning_app.user.chathamkulam.Model.BackgroundWork.AlarmReceiver.deleteRecursive;

public class FMDashboard extends Fragment {

    //    MainList Items
    ListView dashListView;
    DashMainListAdapter dashMainListAdapter;

    TextView txtCount;

    ArrayList<DashEntityObjects> mainList;
    JSONArray mainArray;

    View view;

    RecyclerView dashFilterView;
    DashCardFilterAdapter filterAdapter;

    public FMDashboard() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fm_dash, container, false);

        dashListView = (ListView) view.findViewById(R.id.dashMainListView);
        dashFilterView = (RecyclerView) view.findViewById(R.id.dashFilterView);
        txtCount = (TextView)view.findViewById(R.id.countTxttt);

        dashFilterView.setVisibility(View.GONE);
        txtCount.setVisibility(View.GONE);

        mainList = new ArrayList<>();
        mainArray = new JSONArray();

        Fetch.startService(getActivity());

//        File mydir = getActivity().getDir("Chathamkulam", Context.MODE_PRIVATE);
//        File inFile = new File(mydir,"Operations Management");
//
//        File[] files = inFile.listFiles();
////        Arrays.sort(files);
//        Log.d("Files", "Size: "+ files.length);
//        for (File file : files) {
//            Log.d("Files", "FileName:" + file.getName());
//
//        }

//        File mydirr = getActivity().getDir("Chathamkulam", Context.MODE_PRIVATE);
//        File dir = new File(mydirr,"Operations Management");
//        deleteRecursive(dir);

//        StoreEntireDetails storeEntireDetails = StoreEntireDetails.getInstance(getActivity());
//        Cursor cursor = storeEntireDetails.getAllDetails();
//        Log.d("dashCursorCount", String.valueOf(cursor.getCount()));
//
//        if (cursor.getCount() != 0) {
//
//            if (cursor.moveToFirst()) {
//
//                do {
//
//                    Log.d("#country", cursor.getString(1));
//                    Log.d("#university", cursor.getString(2));
//                    Log.d("#course", cursor.getString(3));
//                    Log.d("#sem", cursor.getString(4));
//                    Log.d("#subjectId", cursor.getString(5));
//                    Log.d("#subjectNumber", cursor.getString(6));
//                    Log.d("#subject", cursor.getString(7));
//                    Log.d("#subjectCost", cursor.getString(8));
//                    Log.d("#trial", cursor.getString(9));
//                    Log.d("#duration", cursor.getString(10));
//                    Log.d("#notes_count", cursor.getString(11));
//                    Log.d("#qbank_count", cursor.getString(12));
//                    Log.d("#video_count", cursor.getString(13));
//                    Log.d("#zip_url", cursor.getString(14) + " ");
//                    Log.d("#validityTill", cursor.getString(15));
//                    Log.d("#progress", cursor.getString(16));
//                    Log.d("#status", cursor.getString(17) + " ");
//
//                    File mydir = getActivity().getDir("Chathamkulam", Context.MODE_PRIVATE);
//                    File fileWithinMyDir = new File(mydir, cursor.getString(7) + ".zip"); //Getting a file within the dir.
////                    Log.d("FolderReport", String.valueOf(fileWithinMyDir));
//                    if (!fileWithinMyDir.exists()) {
//                        Log.d("FolderReport", "not exists");
//                    } else {
//                        Log.d("FolderReport", String.valueOf(fileWithinMyDir));
////                        deleteRecursive(fileWithinMyDir);
//                    }
//
//                    File fileWithinMyDirr = new File(mydir, cursor.getString(7)); //Getting a file within the dir.
//                    if (!fileWithinMyDirr.exists()) {
//                        Log.d("FolderReport", "not exists");
//                    } else {
//                        Log.d("FolderReport", String.valueOf(fileWithinMyDirr));
//                    }
//
//
//                } while (cursor.moveToNext());
//            }
//        }

        MyAsyncTask myAsyncTask = new MyAsyncTask(mainArray, mainList, getActivity());
        myAsyncTask.execute();

//        callAsynchronousTask();

        Log.d("packageName", getActivity().getPackageName());

        dashListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                txtCount.setVisibility(View.GONE);
            }

            @Override
            public void onScroll(AbsListView v, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mLastFirstVisibleItem<firstVisibleItem) {

                    int visibleChildCount = dashListView.getFirstVisiblePosition()+2;
                    txtCount.setVisibility(View.VISIBLE);
                    txtCount.setText("<"+visibleChildCount+"/"+totalItemCount+">");
                }

                if(mLastFirstVisibleItem>firstVisibleItem) {

                    int visibleChildCount = dashListView.getFirstVisiblePosition()+2;
                    txtCount.setVisibility(View.VISIBLE);
                    txtCount.setText("<"+visibleChildCount+"/"+totalItemCount+">");
                }

                mLastFirstVisibleItem=firstVisibleItem;
            }
        });

        dashListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                txtCount.setVisibility(View.GONE);
            }

            @Override
            public void onScroll(AbsListView v, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mLastFirstVisibleItem<firstVisibleItem) {

                    int visibleChildCount = dashListView.getFirstVisiblePosition()+2;
                    txtCount.setVisibility(View.VISIBLE);
                    txtCount.setText("<"+visibleChildCount+"/"+totalItemCount+">");
                }

                if(mLastFirstVisibleItem>firstVisibleItem) {

                    int visibleChildCount = dashListView.getFirstVisiblePosition()+2;
                    txtCount.setVisibility(View.VISIBLE);
                    txtCount.setText("<"+visibleChildCount+"/"+totalItemCount+">");
                }

                mLastFirstVisibleItem=firstVisibleItem;
            }
        });

        dashFilterView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

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
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.menu_submit).setVisible(false);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {

                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//                searchView.setImeOptions(R.drawable.ic_mic);
                dashListView.setVisibility(View.GONE);
                dashFilterView.setVisibility(View.VISIBLE);

//                item.setIcon(R.drawable.ic_mic).setVisible(true);

                MyAsyncTaskFilter myAsyncTaskFilter = new MyAsyncTaskFilter(mainArray, mainList, getActivity());
                myAsyncTaskFilter.execute();
                item.expandActionView();

                MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {

//                        Toast.makeText(getActivity(),"clicked",Toast.LENGTH_LONG).show();
//                        Write your code here
                        dashFilterView.setVisibility(View.GONE);
                        dashListView.setVisibility(View.VISIBLE);

                        return true;
                    }
                });

                search(searchView);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

//    public void callAsynchronousTask() {
//        final Handler handler = new Handler();
//        Timer timer = new Timer();
//        TimerTask doAsynchronousTask = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        try {
//                            MyAsyncTask myAsyncTask = new MyAsyncTask(mainArray,mainList,getActivity());
//                            // PerformBackgroundTask this class is the class that extends AsynchTask
//                            myAsyncTask.execute();
//
//                        } catch (Exception e) {
//                        }
//                    }
//                });
//            }
//        };
//        timer.schedule(doAsynchronousTask, 0, 2000); //execute in every 50000 ms
//    }

    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        JSONArray mainArray;
        ArrayList<DashEntityObjects> mainList;
        ProgressDialog progressDialog;
        Context context;

        MyAsyncTask(JSONArray mainArray, ArrayList<DashEntityObjects> mainList, Context context) {
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
            if (mainCursor.getCount() != 0) {

                if (mainCursor.moveToFirst()) {

                    do {
                        try {
//                        Create Main Object
                            JSONObject mainJsonObject = new JSONObject();

//                        Create Sub Array
                            JSONArray subArray = new JSONArray();

                            mainJsonObject.put("country", mainCursor.getString(1));
                            mainJsonObject.put("university", mainCursor.getString(2));
                            mainJsonObject.put("course", mainCursor.getString(3));
                            mainJsonObject.put("semester", mainCursor.getString(4));

                            Cursor cursor = storeEntireDetails.groupSubDetails(mainCursor.getString(5), mainCursor.getString(4));

                            if (cursor.getCount() != 0) {

                                if (cursor.moveToFirst()) {

                                    do {

//                                    Create Sub Object
                                        JSONObject subJsonObject = new JSONObject();

                                        subJsonObject.put("subject_id", cursor.getString(5));
                                        subJsonObject.put("subject_no", cursor.getString(6));
                                        subJsonObject.put("subject_name", cursor.getString(7));
                                        subJsonObject.put("subject_cost", cursor.getString(8));
                                        subJsonObject.put("trial", cursor.getString(9));
                                        subJsonObject.put("duration", cursor.getString(10));
                                        subJsonObject.put("notes_count", cursor.getString(11));
                                        subJsonObject.put("qbank_count", cursor.getString(12));
                                        subJsonObject.put("video_count", cursor.getString(13));
                                        subJsonObject.put("zip_url", cursor.getString(14));
                                        subJsonObject.put("validityTill", cursor.getString(15));
                                        subJsonObject.put("progress", cursor.getString(16));
                                        subJsonObject.put("status", cursor.getString(17));
                                        subJsonObject.put("download_id", cursor.getString(18));

//                                    Add SubObject To SubArray
                                        subArray.put(subJsonObject);
                                    } while (cursor.moveToNext());

                                    cursor.close();
                                }
                            }
//                        Add SubArray To MainObject
                            mainJsonObject.put("subject_details", subArray);

                            mainArray.put(mainJsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } while (mainCursor.moveToNext());

                    mainCursor.close();

                    Log.d("jsonValue", String.valueOf(mainArray));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            this method will be running on UI thread
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {

                public void run() {

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();

//                ListOut the DataBase Values
                        ObjectMapper mapper = new ObjectMapper();
                        List<DashEntityObjects> dashEntityObjects = new ArrayList<>();

                        List<DashSubjectEntity> subList = new ArrayList<>();

                        try {
                            dashEntityObjects = mapper.readValue(String.valueOf(mainArray), new TypeReference<List<DashEntityObjects>>() {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        DashEntityObjects objects = new DashEntityObjects();
                        DashSubjectEntity dashSubjectEntity = new DashSubjectEntity();

                        for (DashEntityObjects entityObject : dashEntityObjects) {

                            entityObject.setCountry(entityObject.getCountry());
                            entityObject.setUniversity(entityObject.getUniversity());
                            entityObject.setCourse(entityObject.getCourse());
                            entityObject.setSemester(entityObject.getSemester());

                            for (int i = 0; i < entityObject.getSubject_details().size(); i++) {

                                dashSubjectEntity.setSubject_id(entityObject.getSubject_details().get(i).getSubject_id());
                                dashSubjectEntity.setSubject_no(entityObject.getSubject_details().get(i).getSubject_no());
                                dashSubjectEntity.setSubject_name(entityObject.getSubject_details().get(i).getSubject_name());
                                dashSubjectEntity.setSubject_cost(entityObject.getSubject_details().get(i).getSubject_cost());
                                dashSubjectEntity.setTrial(entityObject.getSubject_details().get(i).getTrial());
                                dashSubjectEntity.setDuration(entityObject.getSubject_details().get(i).getDuration());
                                dashSubjectEntity.setNotes_count(entityObject.getSubject_details().get(i).getNotes_count());
                                dashSubjectEntity.setQbank_count(entityObject.getSubject_details().get(i).getQbank_count());
                                dashSubjectEntity.setVideo_count(entityObject.getSubject_details().get(i).getVideo_count());
                                dashSubjectEntity.setZip_url(entityObject.getSubject_details().get(i).getZip_url());
                                dashSubjectEntity.setValidityTill(entityObject.getSubject_details().get(i).getValidityTill());
                                dashSubjectEntity.setProgress(entityObject.getSubject_details().get(i).getProgress());
                                dashSubjectEntity.setStatus(entityObject.getSubject_details().get(i).getStatus());
                                dashSubjectEntity.setDownload_id(entityObject.getSubject_details().get(i).getDownload_id());

                                subList.add(dashSubjectEntity);
                            }

                            objects.setSubject_details(subList);

                            mainList.add(entityObject);
                        }

//                Initialize views
                        handler.post(new Runnable() {
                            public void run() {

                                if (getActivity() != null) {

                                    dashMainListAdapter = new DashMainListAdapter(getActivity(), mainList);
                                    dashListView.setAdapter(dashMainListAdapter);
                                    dashMainListAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }

                }
            };
            new Thread(runnable).start();
        }
    }

    public class MyAsyncTaskFilter extends AsyncTask<Void, Void, Void> {

        JSONArray mainArray;
        ArrayList<DashEntityObjects> mainList;
        ProgressDialog progressDialog;
        Context context;

        MyAsyncTaskFilter(JSONArray mainArray, ArrayList<DashEntityObjects> mainList, Context context) {
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
            Cursor mainCursor = storeEntireDetails.getAllDetails();

            try {

//        Create Main Array
                mainArray = new JSONArray();
                if (mainCursor.getCount() != 0) {

                    if (mainCursor.moveToFirst()) {

                        do {
//                        Create Main Object
                            JSONObject mainJsonObject = new JSONObject();

//                            Log.d("##subject", mainCursor.getString(7));
                            mainJsonObject.put("country", mainCursor.getString(1));
                            mainJsonObject.put("university", mainCursor.getString(2));
                            mainJsonObject.put("course", mainCursor.getString(3));
                            mainJsonObject.put("semester", mainCursor.getString(4));
                            mainJsonObject.put("subject_id", mainCursor.getString(5));
                            mainJsonObject.put("subject_no", mainCursor.getString(6));
                            mainJsonObject.put("subject_name", mainCursor.getString(7));
                            mainJsonObject.put("subject_cost", mainCursor.getString(8));
                            mainJsonObject.put("trial", mainCursor.getString(9));
                            mainJsonObject.put("duration", mainCursor.getString(10));
                            mainJsonObject.put("notes_count", mainCursor.getString(11));
                            mainJsonObject.put("qbank_count", mainCursor.getString(12));
                            mainJsonObject.put("video_count", mainCursor.getString(13));
                            mainJsonObject.put("zip_url", mainCursor.getString(14));
                            mainJsonObject.put("validityTill", mainCursor.getString(15));
                            mainJsonObject.put("progress", mainCursor.getString(16));
                            mainJsonObject.put("status", mainCursor.getString(17));
                            mainJsonObject.put("download_id", mainCursor.getString(18));
                            mainArray.put(mainJsonObject);

                        } while (mainCursor.moveToNext());
                        mainCursor.close();
                    }
                    Log.d("jsonValue", String.valueOf(mainArray));
                }

            } catch (JSONException e) {

                Log.d("JSONException", String.valueOf(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            this method will be running on UI thread
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                public void run() {

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();

//                ListOut the DataBase Values
                        ObjectMapper mapper = new ObjectMapper();
                        List<DashEntityObjects> dashEntityObjects = new ArrayList<>();

                        final ArrayList<DashEntityObjects> mainList = new ArrayList<>();

                        try {
                            dashEntityObjects = mapper.readValue(String.valueOf(mainArray), new TypeReference<List<DashEntityObjects>>() {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        for (DashEntityObjects entityObject : dashEntityObjects) {

                            entityObject.setCountry(entityObject.getCountry());
                            entityObject.setUniversity(entityObject.getUniversity());
                            entityObject.setCourse(entityObject.getCourse());
                            entityObject.setSemester(entityObject.getSemester());
                            entityObject.setSubject_id(entityObject.getSubject_id());
                            entityObject.setSubject_no(entityObject.getSubject_no());
                            entityObject.setSubject_name(entityObject.getSubject_name());
                            entityObject.setSubject_cost(entityObject.getSubject_cost());
                            entityObject.setTrial(entityObject.getTrial());
                            entityObject.setDuration(entityObject.getDuration());
                            entityObject.setNotes_count(entityObject.getNotes_count());
                            entityObject.setQbank_count(entityObject.getQbank_count());
                            entityObject.setVideo_count(entityObject.getVideo_count());
                            entityObject.setZip_url(entityObject.getZip_url());
                            entityObject.setValidityTill(entityObject.getValidityTill());
                            entityObject.setProgress(entityObject.getProgress());
                            entityObject.setStatus(entityObject.getStatus());
                            entityObject.setDownload_id(entityObject.getDownload_id());
                            mainList.add(entityObject);
                        }

//                Initialize views
                        handler.post(new Runnable() {
                            public void run() {

                                if (getActivity() != null) {

                                    filterAdapter = new DashCardFilterAdapter(getActivity(), mainList);
                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                                    dashFilterView = (RecyclerView) view.findViewById(R.id.dashFilterView);
                                    dashFilterView.setLayoutManager(mLayoutManager);
                                    dashFilterView.setHasFixedSize(true);
                                    dashFilterView.setAdapter(filterAdapter);
                                    filterAdapter.notifyDataSetChanged();

                                }
                            }
                        });
                    }
                }
            };
            new Thread(runnable).start();
        }
    }
}
