package com.learning_app.user.chathamkulam.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Adapters.StoreCardAdapter;
import com.learning_app.user.chathamkulam.Adapters.StoreMainListAdapter;
import com.learning_app.user.chathamkulam.Model.AsyncUrl;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreEntityObjects;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreSubjectEntity;
import com.learning_app.user.chathamkulam.MyBounceInterpolator;
import com.learning_app.user.chathamkulam.PaymentGateway.PaymentGateWay;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Registration.Registration;
import com.learning_app.user.chathamkulam.Sqlite.CheckingCards;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.learning_app.user.chathamkulam.AlarmReceiver.deleteRecursive;
import static com.learning_app.user.chathamkulam.Model.Constants.GET_URLS;
import static com.learning_app.user.chathamkulam.Model.Constants.STORE_DATA;

/**
 * Created by User on 5/18/2017.
 */

public class FMStore extends Fragment {

    //    MainList items
    private ListView mainListView;
    private StoreMainListAdapter storeMainListAdapter;

    Button btnOnlineView, btnTrial, btnBuy;
    View view;

    //   Download AsyncUrl variables
    private JSONArray UrlResult = null;
    private String URL_JSON_ARRAY = "result";
    private String current_url = "file";
    private String current_name = "name";
    private ArrayList<String> url_arrayList;
    private ArrayList<String> name_arrayList;
    private ProgressDialog progressDialog;

    CheckingCards checkingCards;
    ProgressBar checkingBar;

//    private Menu menu;

    private static final int REQUEST_WRITE_STORAGE = 112;

    public FMStore() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fm_store, container, false);
        Registration.deleteCache(getActivity());
        mainListView = (ListView) view.findViewById(R.id.storeMainListView);
//        btnOnlineView = (Button) view.findViewById(R.id.btnOnline);
        btnTrial = (Button) view.findViewById(R.id.btnTrial);
        btnBuy = (Button) view.findViewById(R.id.btnBuy);

        btnTrial.setEnabled(true);
        btnBuy.setEnabled(true);

        checkingBar =(ProgressBar)view.findViewById(R.id.checkingBar);
        checkingBar.setVisibility(View.GONE);

        GetJsonResponse();


        checkingCards = new CheckingCards(getActivity());

        checkingCards.DeleteAll();

//        btnOnlineView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                RegisterMember registerMember = RegisterMember.getInstance(getActivity());;
//                final Cursor cursorResult = registerMember.getDetails();
//
//                Cursor cursor = checkingCards.getCheckData();
//
//                if (cursor != null && cursor.getCount() == 1){
//
//                    if (cursor.moveToFirst()) {
//                        while (!cursor.isAfterLast()) {
//
//                            String position = cursor.getString(1);
//                            final String country = cursor.getString(2);
//                            final String university = cursor.getString(3);
//                            final String course = cursor.getString(4);
//                            final String semester = cursor.getString(5);
//                            final String subject = cursor.getString(6);
//                            final String subjectId = cursor.getString(7);
//                            final String subjectNumber = cursor.getString(8);
//
//                            Log.d("final data", position + semester + subject + subjectId + subjectNumber);
//
//                            MenuBuilder menuBuilder = new MenuBuilder(getActivity());
//                            MenuInflater inflater = new MenuInflater(getActivity());
//                            inflater.inflate(R.menu.onlineview_options, menuBuilder);
//                            MenuPopupHelper optionsMenu = new MenuPopupHelper(getActivity(), menuBuilder, view);
//                            optionsMenu.setForceShowIcon(true);
//
////                Set Item Click Listener
//                            menuBuilder.setCallback(new MenuBuilder.Callback() {
//                                @Override
//                                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
//                                    switch (item.getItemId()) {
//
//                                        case R.id.menuNotes:
//
//                                            AlertDialog.Builder alertNotes = new AlertDialog.Builder(getActivity());
//
//                                            while (cursorResult.moveToNext()) {
//                                                String userName = cursorResult.getString(0);
//
//                                                alertNotes.setTitle("Dear  " + userName);
//                                                alertNotes.setMessage(R.string.viewMessage);
//                                                alertNotes.setIcon(R.drawable.ic_question);
//                                                alertNotes.setPositiveButton("Continue",
//                                                        new DialogInterface.OnClickListener() {
//                                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                                startActivity(new Intent(getActivity(), NSPDFViewer.class));
//
//                                                            }
//                                                        });
//
//                                                alertNotes.setNegativeButton("No",
//                                                        new DialogInterface.OnClickListener() {
//                                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                                dialog.cancel();
//
//                                                            }
//                                                        });
//                                                alertNotes.show();
//                                            }
//                                            break;
//
//                                        case R.id.menuVideos:
//
//                                            AlertDialog.Builder alertVideo = new AlertDialog.Builder(getActivity());
//
//                                            while (cursorResult.moveToNext()) {
//                                                String userName = cursorResult.getString(0);
//
//                                                alertVideo.setTitle("Dear  " + userName);
//                                                alertVideo.setMessage(R.string.viewMessage);
//                                                alertVideo.setIcon(R.drawable.ic_question);
//                                                alertVideo.setPositiveButton("Continue",
//                                                        new DialogInterface.OnClickListener() {
//                                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                                startActivity(new Intent(getActivity(), ModuleList.class));
//
//                                                            }
//                                                        });
//
//                                                alertVideo.setNegativeButton("No",
//                                                        new DialogInterface.OnClickListener() {
//                                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                                dialog.cancel();
//
//                                                            }
//                                                        });
//                                                alertVideo.show();
//                                            }
//                                            break;
//
//                                        case R.id.menuQb:
//
//                                            AlertDialog.Builder alertQb = new AlertDialog.Builder(getActivity());
//
//                                            while (cursorResult.moveToNext()) {
//                                                String userName = cursorResult.getString(0);
//
//                                                alertQb.setTitle("Dear  " + userName);
//                                                alertQb.setMessage(R.string.viewMessage);
//                                                alertQb.setIcon(R.drawable.ic_question);
//                                                alertQb.setPositiveButton("Continue",
//                                                        new DialogInterface.OnClickListener() {
//                                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                                startActivity(new Intent(getActivity(), QBPDFViewer.class));
//
//                                                            }
//                                                        });
//
//                                                alertQb.setNegativeButton("No",
//                                                        new DialogInterface.OnClickListener() {
//                                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                                dialog.cancel();
//
//                                                            }
//                                                        });
//                                                alertQb.show();
//                                            }
//                                            break;
//
//                                        default:
//                                            break;
//
//                                    }
//                                    return true;
//                                }
//
//                                @Override
//                                public void onMenuModeChange(MenuBuilder menu) {
//
//                                }
//                            });
//
//                            optionsMenu.show();
//
//                            cursor.moveToNext();
//                        }
//                    }
//                } else {
//
//                    Toast.makeText(getActivity()," Please select any one of this subjects below!! ",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        btnTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                btnTrial.startAnimation(myAnim);


                btnTrial.setEnabled(false);

                Cursor cursor = checkingCards.getCheckData();

                if (cursor.getCount() != 0){

                    while (cursor.moveToNext()) {

                        String position = cursor.getString(1);
                        final String country = cursor.getString(2);
                        final String university = cursor.getString(3);
                        final String course = cursor.getString(4);
                        final String semester = cursor.getString(5);
                        final String subject = cursor.getString(6);
                        final String subjectId = cursor.getString(7);
                        final String subjectNumber = cursor.getString(8);
                        final String freeValidity = cursor.getString(9);
                        final String paidValidity = cursor.getString(10);
                        final String duration = cursor.getString(11);
                        final String videoCount = cursor.getString(12);
                        final String notesCount = cursor.getString(13);
                        final String qbankCount = cursor.getString(14);

                        Log.d("Check data",position+semester+subject+subjectId+subjectNumber+"  "+freeValidity+"  "+paidValidity+"  "+duration);

//                            put values for download
                        HashMap<String,String> params = new HashMap<String, String>();
                        params.put("sem_no",semester);
                        params.put("id",subjectId);
                        params.put("sub_no",subjectNumber);
                        params.put("type","url");

                        Log.v("Subject Values",subject+subjectId+subjectNumber+semester);
                        Log.v("Hash Values",params.toString());
                        url_arrayList = new ArrayList<String>();
                        name_arrayList = new ArrayList<String>();

                        AsyncUrl asyncUrl = new AsyncUrl(getActivity(),params,UrlResult,URL_JSON_ARRAY,
                                current_url,current_name,url_arrayList,name_arrayList,progressDialog);
                        asyncUrl.execute(GET_URLS);

//                        progressDialog = ProgressDialog.show(getActivity(), "Please wait...", "While checking......", true);
//                        progressDialog.show();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void run() {
//                                    Do something after 100ms
                                Log.v("Check UrlArraylist", String.valueOf(AsyncUrl.url_arrayList.size()));
                                Log.v("Check NameArraylist", String.valueOf(AsyncUrl.name_arrayList.size()));

                                if (AsyncUrl.url_arrayList != null){
//                                    progressDialog.dismiss();

                                    String DNAME = "Chathamkulam"+"/"+subject;
                                    final File rootPath = new File(Environment.getExternalStorageDirectory().toString(), DNAME);

                                    final StoreCardAdapter adapter = new StoreCardAdapter(getActivity());
                                    if (!rootPath.exists()){

//                                        MyDownloadManager manager = new MyDownloadManager(getActivity(),subject);
//                                        manager.execute();
                                        adapter.DownloadFile(getActivity(),country,university,course,semester,subject,subjectId,
                                                subjectNumber,freeValidity,paidValidity,duration,videoCount,notesCount,qbankCount);

                                    } else {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("This file already downloaded!!!");
                                        builder.setMessage("If you want overwrite this file?")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        deleteRecursive(rootPath);

//                                                        MyDownloadManager manager = new MyDownloadManager(getActivity(),subject);
//                                                        manager.execute()

                                                        adapter.DownloadFile(getActivity(),country,university,course,semester,subject,
                                                                subjectId,subjectNumber,freeValidity,paidValidity,duration,
                                                                videoCount,notesCount,qbankCount);

                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        btnTrial.setEnabled(true);
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                }
                            }
                        }, 5000);
                    }

                } else {

                    Toast.makeText(getActivity()," Please select subjects below!! ",Toast.LENGTH_SHORT).show();
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

                btnBuy.setEnabled(false);

                Cursor cursor = checkingCards.getCheckData();
                if (cursor != null) {

                    startActivity(new Intent(getActivity(), PaymentGateWay.class));
                    Toast.makeText(getActivity()," Please buy our subjects !! ",Toast.LENGTH_SHORT).show();

                }else{

                    Toast.makeText(getActivity()," Please select subjects below!! ",Toast.LENGTH_SHORT).show();
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

//    @Override
//    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
//
//        inflater.inflate(R.menu.subject_menu, menu);
//
//        btnTrial.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                menu.findItem(R.id.cardTrial).setEnabled(true);
//            }
//        });
//
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_share).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        btnTrial.setEnabled(true);
        btnBuy.setEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search : {
                //Write here what to do you on click

//                startActivity(new Intent(getActivity(), StoreFilterRecycler.class));

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
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(STORE_DATA, new Response.Listener<JSONArray>() {
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

                Log.d("Store Response", response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Dismissing progress dialog
                loading.dismiss();

                Toast.makeText(getActivity(),"Check Your Network Connection !! ", Toast.LENGTH_SHORT).show();
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
    private void parseData(JSONArray mainArray) throws JSONException {

        ObjectMapper mapper = new ObjectMapper();
        List<StoreEntityObjects> storeEntityObjects = new ArrayList<>();

        try {
            storeEntityObjects =  mapper.readValue(String.valueOf(mainArray), new TypeReference<List<StoreEntityObjects>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        StoreEntityObjects objects = new StoreEntityObjects();
        StoreSubjectEntity storeSubjectEntity = new StoreSubjectEntity();

        ArrayList<StoreEntityObjects> mainList = new ArrayList<StoreEntityObjects>();
        List<StoreSubjectEntity> subList = new ArrayList<StoreSubjectEntity>();

        for (StoreEntityObjects entityObject : storeEntityObjects){

            entityObject.setCountry(entityObject.getCountry());
            entityObject.setUniversity(entityObject.getUniversity());
            entityObject.setCourse(entityObject.getCourse());
            entityObject.setSem_no(entityObject.getSem_no());

            Log.d("CountryName: ", entityObject.getCountry());
            Log.d("University: ", entityObject.getUniversity());
            Log.d("Course: ", entityObject.getCourse());
            Log.d("Semester: ",entityObject.getSem_no());

            for(int i=0;i<entityObject.getSubject_details().size();i++){

                storeSubjectEntity.setSubject_name(entityObject.getSubject_details().get(i).getSubject_name());
                storeSubjectEntity.setAmount(entityObject.getSubject_details().get(i).getAmount());
                storeSubjectEntity.setPrice_type(entityObject.getSubject_details().get(i).getPrice_type());
                storeSubjectEntity.setFile(entityObject.getSubject_details().get(i).getFile());
                storeSubjectEntity.setFree_validity(entityObject.getSubject_details().get(i).getFree_validity());
                storeSubjectEntity.setFree_validity_date(entityObject.getSubject_details().get(i).getFree_validity_date());
                storeSubjectEntity.setPaid_validity(entityObject.getSubject_details().get(i).getPaid_validity());
                storeSubjectEntity.setPaid_validity_date(entityObject.getSubject_details().get(i).getPaid_validity_date());
                storeSubjectEntity.setSize(entityObject.getSubject_details().get(i).getSize());
                storeSubjectEntity.setSub_no(entityObject.getSubject_details().get(i).getSub_no());
                storeSubjectEntity.setVideo_count(entityObject.getSubject_details().get(i).getVideo_count());
                storeSubjectEntity.setFile_count(entityObject.getSubject_details().get(i).getFile_count());
                storeSubjectEntity.setQa_count(entityObject.getSubject_details().get(i).getQa_count());

                Log.d("subjectName: ", String.valueOf(entityObject.getSubject_details()));
                subList.add(storeSubjectEntity);

            }

            objects.setSubject_details(subList);

            mainList.add(entityObject);

        }
//        Initializing MainList Items
        storeMainListAdapter = new StoreMainListAdapter(getActivity(), mainList);
        mainListView.setAdapter(storeMainListAdapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
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