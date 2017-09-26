package com.learning_app.user.chathamkulam.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Adapters.StoreMainListAdapter;
import com.learning_app.user.chathamkulam.FetchDownloadManager;
import com.learning_app.user.chathamkulam.Model.BackgroundWork.OnlineModuleView;
import com.learning_app.user.chathamkulam.Model.MyBounceInterpolator;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreEntityObjects;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreSubjectEntity;
import com.learning_app.user.chathamkulam.PaymentGateway.PaymentGateActivity;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Registration.Registration;
import com.learning_app.user.chathamkulam.SearchFilters.StoreCardFilterAdapter;
import com.learning_app.user.chathamkulam.Sqlite.CheckingCards;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;
import com.learning_app.user.chathamkulam.Viewer.NSPDFViewer;
import com.learning_app.user.chathamkulam.Viewer.QBPDFViewer;
import com.tonyodev.fetch.Fetch;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.learning_app.user.chathamkulam.Apis.API_ONLINE_MODULE_DATA;
import static com.learning_app.user.chathamkulam.Apis.API_ONLINE_VIEW;
import static com.learning_app.user.chathamkulam.Apis.API_STORE;
import static com.learning_app.user.chathamkulam.Apis.API_STORE_SEARCH;
import static com.learning_app.user.chathamkulam.Model.BackgroundWork.AlarmReceiver.deleteRecursive;
import static com.learning_app.user.chathamkulam.Registration.Registration.deleteCache;

/**
 * Created by User on 5/18/2017.
 */

public class FMStore extends Fragment {

    private static final int REQUEST_WRITE_STORAGE = 112;
    Button btnOnlineView, btnTrial, btnBuy;
    View view;

    CheckingCards checkingCards;
    RecyclerView storeFilterView;
    StoreCardFilterAdapter storeCardFilterAdapter;
    String viewResponse;
    String searchResponse;
    String country, university, course, semester, subjectId, subjectNumber, subject,
            sub_cost, trial, duration, notesCount, qbankCount, videoCount, zipUrl;
    //    MainList items
    private ListView mainListView;

    TextView txtCount;

    public FMStore() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fm_store, container, false);
        Registration.deleteCache(getActivity());

        mainListView = (ListView) view.findViewById(R.id.storeMainListView);
        btnOnlineView = (Button) view.findViewById(R.id.btnOnline);
        btnTrial = (Button) view.findViewById(R.id.btnTrial);
        btnBuy = (Button) view.findViewById(R.id.btnBuy);
        txtCount = (TextView)view.findViewById(R.id.countTxttt);
        txtCount.setVisibility(View.GONE);

        storeFilterView = (RecyclerView) view.findViewById(R.id.storeFilterView);
        storeFilterView.setVisibility(View.GONE);

        btnTrial.setEnabled(true);
        btnBuy.setEnabled(true);
        btnOnlineView.setEnabled(true);

        String unique_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("DeviceId",unique_id);

        GetJsonResponse();

        Fetch.startService(getActivity());

        checkingCards = CheckingCards.getInstance(getActivity());
        checkingCards.DeleteAll();
        // checkingCards.list();

        mainListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                txtCount.setVisibility(View.GONE);
            }

            @Override
            public void onScroll(AbsListView v, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mLastFirstVisibleItem<firstVisibleItem) {

                    int visibleChildCount = mainListView.getFirstVisiblePosition()+2;
                    txtCount.setVisibility(View.VISIBLE);
                    txtCount.setText("<"+visibleChildCount+"/"+totalItemCount+">");
                }

                if(mLastFirstVisibleItem>firstVisibleItem) {

                    int visibleChildCount = mainListView.getFirstVisiblePosition()+2;
                    txtCount.setVisibility(View.VISIBLE);
                    txtCount.setText("<"+visibleChildCount+"/"+totalItemCount+">");
                }

                mLastFirstVisibleItem=firstVisibleItem;
            }
        });

        btnOnlineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                btnOnlineView.startAnimation(myAnim);

                RegisterMember registerMember = RegisterMember.getInstance(getActivity());
                final Cursor cursorResult = registerMember.getDetails();

                Cursor cursor = checkingCards.getCheckData();

                if (cursor != null && cursor.getCount() == 1) {

                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {

                            String position = cursor.getString(1);
                            final String country = cursor.getString(2);
                            final String university = cursor.getString(3);
                            final String course = cursor.getString(4);
                            final String semester = cursor.getString(5);
                            final String subjectId = cursor.getString(6);
                            final String subjectNumber = cursor.getString(7);
                            final String subject = cursor.getString(8);

                            Log.d("final data", position + semester + subject + subjectId + subjectNumber);

                            MenuBuilder menuBuilder = new MenuBuilder(getActivity());
                            MenuInflater inflater = new MenuInflater(getActivity());
                            inflater.inflate(R.menu.onlineview_options, menuBuilder);
                            MenuPopupHelper optionsMenu = new MenuPopupHelper(getActivity(), menuBuilder, view);
                            optionsMenu.setForceShowIcon(true);

//                Set Item Click Listener
                            menuBuilder.setCallback(new MenuBuilder.Callback() {
                                @Override
                                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                    switch (item.getItemId()) {

                                        case R.id.menuNotes:

                                            AlertDialog.Builder alertNotes = new AlertDialog.Builder(getActivity());

                                            while (cursorResult.moveToNext()) {
                                                String userName = cursorResult.getString(1);

                                                alertNotes.setTitle("Dear  " + userName);
                                                alertNotes.setMessage(R.string.viewMessage);
                                                alertNotes.setIcon(R.drawable.ic_question);
                                                alertNotes.setPositiveButton("Continue",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                HashMap<String, String> params = new HashMap<String, String>();
                                                                params.put("subject_id", subjectId);
                                                                params.put("sem_no", semester);
                                                                params.put("sub_no", subjectNumber);
                                                                params.put("type", "notes");

                                                                OnlineModuleView async = new OnlineModuleView(API_ONLINE_VIEW, params,
                                                                        getActivity(), NSPDFViewer.class, subject);
                                                                async.execute();

                                                            }
                                                        });

                                                alertNotes.setNegativeButton("No",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                dialog.cancel();
                                                                btnOnlineView.setEnabled(true);
                                                            }
                                                        });
                                                alertNotes.show();
                                            }
                                            break;

                                        case R.id.menuVideos:

                                            AlertDialog.Builder alertVideo = new AlertDialog.Builder(getActivity());

                                            while (cursorResult.moveToNext()) {
                                                String userName = cursorResult.getString(1);

                                                alertVideo.setTitle("Dear  " + userName);
                                                alertVideo.setMessage(R.string.viewMessage);
                                                alertVideo.setIcon(R.drawable.ic_question);
                                                alertVideo.setPositiveButton("Continue",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                HashMap<String, String> params = new HashMap<String, String>();

                                                                params.put("subject_id", subjectId);
                                                                params.put("sem_no", semester);
                                                                params.put("sub_no", subjectNumber);

                                                                OnlineModuleView async = new OnlineModuleView
                                                                        (API_ONLINE_MODULE_DATA, params, getActivity(), ModuleList.class, subject);
                                                                async.execute();

                                                            }
                                                        });

                                                alertVideo.setNegativeButton("No",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                dialog.cancel();
                                                                btnOnlineView.setEnabled(true);

                                                            }
                                                        });
                                                alertVideo.show();
                                            }
                                            break;

                                        case R.id.menuQb:

                                            AlertDialog.Builder alertQb = new AlertDialog.Builder(getActivity());

                                            while (cursorResult.moveToNext()) {
                                                String userName = cursorResult.getString(1);

                                                alertQb.setTitle("Dear  " + userName);
                                                alertQb.setMessage(R.string.viewMessage);
                                                alertQb.setIcon(R.drawable.ic_question);
                                                alertQb.setPositiveButton("Continue",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                HashMap<String, String> params = new HashMap<String, String>();
                                                                params.put("subject_id", subjectId);
                                                                params.put("sem_no", semester);
                                                                params.put("sub_no", subjectNumber);
                                                                params.put("type", "qa");

                                                                OnlineModuleView async = new OnlineModuleView(API_ONLINE_VIEW, params,
                                                                        getActivity(), QBPDFViewer.class, subject);
                                                                async.execute();

                                                            }
                                                        });

                                                alertQb.setNegativeButton("No",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                dialog.cancel();
                                                                btnOnlineView.setEnabled(true);

                                                            }
                                                        });
                                                alertQb.show();
                                            }
                                            break;

                                        default:
                                            break;

                                    }
                                    return true;
                                }

                                @Override
                                public void onMenuModeChange(MenuBuilder menu) {

                                }
                            });

                            optionsMenu.show();

                            cursor.moveToNext();
                            cursor.close();
                        }
                    }
                } else {

                    Toast.makeText(getActivity(), " Please select any one of this subjects below!! ", Toast.LENGTH_SHORT).show();
                    btnOnlineView.setEnabled(true);
                }
            }
        });

        btnTrial.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                deleteCache(getActivity());
//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                btnTrial.startAnimation(myAnim);

                btnTrial.setEnabled(false);

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

                        File mydir = getActivity().getDir("Chathamkulam", Context.MODE_PRIVATE);
                        final File rootPath = new File(mydir, subject); //Getting a file within the dir.

                        if (!rootPath.exists()) {

                            StoreEntireDetails databaseHelperStore = StoreEntireDetails.getInstance(getActivity());
                            if (databaseHelperStore.ifExists(subject)) {

                                Cursor InCursor = databaseHelperStore.getSubjectRow(subject);
                                if (InCursor.getCount() != 0) {
                                    if (InCursor.moveToFirst()) {
                                        do {

                                            String id = InCursor.getString(18);
                                            String statuss = InCursor.getString(17);

                                            Log.d("getId", id +"   "+statuss);

                                            if (!statuss.equals("onCompleted")){

                                                Fetch fetch = Fetch.newInstance(getActivity());
                                                Log.d("getId", id);
                                                int status = fetch.get(Long.parseLong(id)).getStatus();

                                                if (status == Fetch.STATUS_DOWNLOADING
                                                        || status == Fetch.STATUS_QUEUED || status == Fetch.STATUS_ERROR) {

                                                    Toast.makeText(getActivity(), "Downloading process not complete", Toast.LENGTH_SHORT).show();
                                                    btnTrial.setEnabled(true);

                                                } else if (status == Fetch.STATUS_PAUSED){

                                                    fetch.resume(Long.parseLong(id));
                                                    Toast.makeText(getActivity(), "File resumed on background", Toast.LENGTH_LONG).show();

                                                } else {

                                                    new Thread(new FetchDownloadManager(zipUrl, country, university, course, semester, subjectId, subjectNumber, subject,
                                                            sub_cost, trial, duration, notesCount, qbankCount, videoCount, getActivity(), "trial")).start();

                                                }
                                            } else {

                                                Toast.makeText(getActivity(), "This subject already downloaded!!", Toast.LENGTH_SHORT).show();
                                                btnTrial.setEnabled(true);
                                            }

                                        } while (InCursor.moveToNext());
                                        InCursor.close();
                                    }
                                }
                            } else {

                                new Thread(new FetchDownloadManager(zipUrl, country, university, course, semester, subjectId, subjectNumber, subject,
                                        sub_cost, trial, duration, notesCount, qbankCount, videoCount, getActivity(), "trial")).start();
                            }

                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("This file already downloaded!!!");
                            builder.setMessage("If you want overwrite this file?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                        public void onClick(DialogInterface dialog, int id) {

                                            deleteRecursive(rootPath);
                                            new Thread(new FetchDownloadManager(zipUrl, country, university, course, semester, subjectId, subjectNumber, subject,
                                                    sub_cost, trial, duration, notesCount, qbankCount, videoCount, getActivity(), "trial")).start();

                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            dialog.cancel();
                                            btnTrial.setEnabled(true);
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }

                    cursor.close();
                    checkingCards.close();

                } else {

                    Toast.makeText(getActivity(), "Please select subjects below!! ", Toast.LENGTH_SHORT).show();
                    btnTrial.setEnabled(true);

                }
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                btnBuy.startAnimation(myAnim);

                btnBuy.setEnabled(true);

                Cursor cursor = checkingCards.getCheckData();
                if (cursor.getCount() != 0) {

                    StringBuilder stringSubject = new StringBuilder();
                    ArrayList<String> amountList = new ArrayList<String>();

                    while (cursor.moveToNext()) {

                        String country = cursor.getString(2);
                        String university = cursor.getString(3);
                        String course = cursor.getString(4);
                        String semester = cursor.getString(5);
                        String subjectId = cursor.getString(6);
                        String subjectNumber = cursor.getString(7);
                        String subject = cursor.getString(8);
                        String sub_cost = cursor.getString(9);
                        String trial = cursor.getString(10);
                        String duration = cursor.getString(11);
                        String notesCount = cursor.getString(12);
                        String qbankCount = cursor.getString(13);
                        String videoCount = cursor.getString(14);
                        String zipUrl = cursor.getString(15);

//                        Log.d("checkData",position+semester+subject+subjectId+subjectNumber+"   "+amount+
//                                "  "+freeValidity+"  "+paidValidity+"  "+duration);

                        String[] cost = sub_cost.split("\\s+");
                        Log.d("SubCost", cost[0].substring(3));

                        stringSubject.append(subject).append(", ");
                        amountList.add(cost[0].substring(3));
//                        amountList.add("1.00");
                    }

                    Double totalAmount = 0.00;
                    for (int i = 0; i < amountList.size(); i++) {
                        totalAmount += Double.parseDouble(amountList.get(i));
                    }

                    String subjectName = stringSubject.substring(0, stringSubject.length() - 2);
                    Log.d("concatValue", subjectName + "   " + totalAmount);

                    if (!subjectName.equals(null) && totalAmount != 0) {

                        Intent intent = new Intent(getActivity(), PaymentGateActivity.class);
                        intent.putExtra("key_subjectName", subjectName);
                        intent.putExtra("key_amount", totalAmount);
                        startActivity(intent);

                        //   Toast.makeText(getActivity(),"PAYMENT integration working process, We activate will be soon!! ",Toast.LENGTH_LONG).show();

                    }

                    cursor.close();
                    checkingCards.close();

                } else {

                    Toast.makeText(getActivity(), " Please select subjects below!! ", Toast.LENGTH_SHORT).show();
                    btnBuy.setEnabled(true);
                }
            }
        });


        boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Store");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_share).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.menu_submit).setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        btnTrial.setEnabled(true);
        btnBuy.setEnabled(true);
        btnOnlineView.setEnabled(true);

    }

    @Override
    public void onPause() {
        super.onPause();

        btnTrial.setEnabled(true);
        btnBuy.setEnabled(true);
        btnOnlineView.setEnabled(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {

                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                mainListView.setVisibility(View.GONE);
                storeFilterView.setVisibility(View.VISIBLE);

                item.expandActionView();
                getFilterValue();

                MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        storeFilterView.setVisibility(View.GONE);
                        mainListView.setVisibility(View.VISIBLE);

                        return true;
                    }
                });

                search(searchView);
                return true;

            }
        }
        return super.onOptionsItemSelected(item);
    }


    //        This method will get data from the web api
    public void GetJsonResponse() {

//        Clear cache files
        Registration.deleteCache(getActivity());
//        Showing progress
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Loading Data", "Please wait...", false, false);

//        Creating a json array request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(API_STORE, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray response) {
                //Dismissing progress dialog
                loading.dismiss();
                //calling method to parse json array
                try {

                    parseData(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("storeResponse", response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Dismissing progress dialog
                loading.dismiss();

                Toast.makeText(getActivity(), "Check Your Network Connection !! ", Toast.LENGTH_SHORT).show();
                Log.d("VolleyError", error.toString());

            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(jsonArrayRequest);

    }

    //This method will parse json data
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void parseData(final JSONArray mainArray) throws JSONException {

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                ObjectMapper mapper = new ObjectMapper();
                List<StoreEntityObjects> storeEntityObjects = new ArrayList<>();

                try {
                    storeEntityObjects = mapper.readValue(String.valueOf(mainArray), new TypeReference<List<StoreEntityObjects>>() {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StoreEntityObjects objects = new StoreEntityObjects();
                StoreSubjectEntity storeSubjectEntity = new StoreSubjectEntity();

                final ArrayList<StoreEntityObjects> mainList = new ArrayList<StoreEntityObjects>();
                List<StoreSubjectEntity> subList = new ArrayList<StoreSubjectEntity>();

                for (StoreEntityObjects entityObject : storeEntityObjects) {

                    entityObject.setCountry(entityObject.getCountry());
                    entityObject.setUniversity(entityObject.getUniversity());
                    entityObject.setCourse(entityObject.getCourse());
                    entityObject.setSem_no(entityObject.getSem_no());

                    for (int i = 0; i < entityObject.getSubject_details().size(); i++) {

                        storeSubjectEntity.setSubject_name(entityObject.getSubject_details().get(i).getSubject_name());
                        storeSubjectEntity.setSub_no(entityObject.getSubject_details().get(i).getSub_no());
                        storeSubjectEntity.setSub_cost(entityObject.getSubject_details().get(i).getSub_cost());
                        storeSubjectEntity.setTrial(entityObject.getSubject_details().get(i).getTrial());
                        storeSubjectEntity.setFile(entityObject.getSubject_details().get(i).getFile());
                        storeSubjectEntity.setSize(entityObject.getSubject_details().get(i).getSize());
                        storeSubjectEntity.setFile_count(entityObject.getSubject_details().get(i).getFile_count());
                        storeSubjectEntity.setQa_count(entityObject.getSubject_details().get(i).getQa_count());
                        storeSubjectEntity.setVideo_count(entityObject.getSubject_details().get(i).getVideo_count());
                        storeSubjectEntity.setUrl(entityObject.getSubject_details().get(i).getUrl());

                        subList.add(storeSubjectEntity);

                    }

                    objects.setSubject_details(subList);

                    mainList.add(entityObject);

                }


                handler.post(new Runnable() {
                    public void run() {

//                        Initializing MainList Items
                        StoreMainListAdapter storeMainListAdapter = new StoreMainListAdapter(getActivity(), mainList);
                        mainListView.setAdapter(storeMainListAdapter);
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    public void getFilterValue() {

//        Clear cache files
        Registration.deleteCache(getActivity());
//        Showing progress
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Loading Data", "Please wait...", false, false);

//        Creating a json array request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(API_STORE_SEARCH, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray response) {
                //Dismissing progress dialog
                loading.dismiss();

                //calling method to parse json array
                try {
                    parseFilterData(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("Store Response", response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Dismissing progress dialog
                loading.dismiss();

                Toast.makeText(getActivity(), "Check Your Network Connection !! ", Toast.LENGTH_SHORT).show();
                Log.d("VolleyError", error.toString());

            }
        });

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(jsonArrayRequest);

    }

    //This method will parse json data
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void parseFilterData(final JSONArray mainArray) throws JSONException {

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            public void run() {


                ObjectMapper mapper = new ObjectMapper();
                List<StoreEntityObjects> storeEntityObjects = new ArrayList<>();

                try {
                    storeEntityObjects = mapper.readValue(String.valueOf(mainArray), new TypeReference<List<StoreEntityObjects>>() {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final ArrayList<StoreEntityObjects> mainList = new ArrayList<StoreEntityObjects>();


                for (StoreEntityObjects entityObject : storeEntityObjects) {

                    entityObject.setCountry(entityObject.getCountry());
                    entityObject.setUniversity(entityObject.getUniversity());
                    entityObject.setCourse(entityObject.getCourse());
                    entityObject.setSem_no(entityObject.getSem_no());
                    entityObject.setSubject_name(entityObject.getSubject_name());
                    entityObject.setSub_no(entityObject.getSub_no());
                    entityObject.setSub_cost(entityObject.getSub_cost());
                    entityObject.setTrial(entityObject.getTrial());
                    entityObject.setFile(entityObject.getFile());
                    entityObject.setSize(entityObject.getSize());
                    entityObject.setFile_count(entityObject.getFile_count());
                    entityObject.setQa_count(entityObject.getQa_count());
                    entityObject.setVideo_count(entityObject.getVideo_count());
                    entityObject.setUrl(entityObject.getUrl());

                    mainList.add(entityObject);

                }

                handler.post(new Runnable() {
                    public void run() {

//                        Initializing MainList Items
                        if (getActivity() != null) {

                            storeCardFilterAdapter = new StoreCardFilterAdapter(getActivity(), mainList);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                            storeFilterView = (RecyclerView) view.findViewById(R.id.storeFilterView);
                            storeFilterView.setLayoutManager(mLayoutManager);
                            storeFilterView.setHasFixedSize(true);
                            storeFilterView.setAdapter(storeCardFilterAdapter);
                            storeCardFilterAdapter.notifyDataSetChanged();
                        }

                    }
                });
            }

        };
        new Thread(runnable).start();
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onQueryTextChange(String newText) {

                storeCardFilterAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //reload my activity with permission granted or use the features what required the permission

                } else {

                    Toast.makeText(getActivity(), "The app was not allowed to write to your storage. " +
                            "Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }
}