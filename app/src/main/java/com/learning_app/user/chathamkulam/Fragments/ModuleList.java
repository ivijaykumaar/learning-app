package com.learning_app.user.chathamkulam.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Adapters.ModuleAdapter;
import com.learning_app.user.chathamkulam.Adapters.StoreCardAdapter;
import com.learning_app.user.chathamkulam.Model.BackgroundWork.AsyncUrl;
import com.learning_app.user.chathamkulam.Model.ModuleModel.ModuleItems;
import com.learning_app.user.chathamkulam.Model.ModuleModel.TopicItems;
import com.learning_app.user.chathamkulam.SearchFilters.ModuleCardFilterAdapter;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;
import com.learning_app.user.chathamkulam.Sqlite.VideoHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.learning_app.user.chathamkulam.Model.Constants.GET_URLS;

/**
 * Created by User on 6/1/2017.
 */

public class ModuleList extends AppCompatActivity {

    ListView moduleListView;
    ModuleAdapter moduleAdapter;

    RecyclerView moduleFilterView;
    ModuleCardFilterAdapter cardFilterAdapter;

    public static String currentSubjectName;
    TextView txtRibbon;
    VideoHandler videoHandler;

    //   Download AsyncUrl variables
    private JSONArray UrlResult = null;
    private String URL_JSON_ARRAY = "result";
    private String current_url = "file";
    private String current_name = "name";
    private ArrayList<String> url_arrayList;
    private ArrayList<String> name_arrayList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_list);

        txtRibbon = (TextView)findViewById(R.id.txtRibbon);
        videoHandler = VideoHandler.getInstance(getApplicationContext());
        moduleFilterView = (RecyclerView)findViewById(R.id.moduleFilterView);
        moduleListView = (ListView)findViewById(R.id.moduleList);
        moduleFilterView.setVisibility(View.GONE);

//        Create Main Array
        JSONArray mainArray = new JSONArray();

        currentSubjectName = getIntent().getStringExtra("Key_video");
        getSupportActionBar().setTitle(currentSubjectName);

        if (currentSubjectName != null){

            txtRibbon.setVisibility(View.GONE);
            String MainDir = "Chathamkulam"+"/"+currentSubjectName;
            File mainPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), MainDir);

            if (mainPath.exists()){

                File moduleList[] = mainPath.listFiles();

//        Get Values frm SDCard
                try {

                    if (moduleList != null && moduleList.length > 0) {
                        for (File aModuleList : moduleList) {

//                Create Main Object
                            JSONObject mainJsonObject = new JSONObject();

//                    Create Sub Array
                            JSONArray subArray = new JSONArray();

                            if (aModuleList.isDirectory()) {

                                String moduleConvert = aModuleList.getName();
                                String moduleName = moduleConvert.replaceAll("%20","  ");

                                mainJsonObject.put("module_name",moduleName);

                                String SubDir = "Chathamkulam"+"/"+currentSubjectName+"/"+moduleName;
                                File subPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), SubDir);

                                File topicList[] = subPath.listFiles();

                                if (topicList != null && topicList.length > 0){

                                    for (File aTopicList : topicList){

                                        if (aTopicList.isFile()){

                                            String topicConvert = aTopicList.getName();
                                            String checkTopic = aTopicList.getAbsolutePath();
                                            String topicName = topicConvert.replaceAll("%20","  ");
                                            String[] sep = topicConvert.split("\\.");
                                            Log.d("###Topic",topicName);
                                            String Topic = sep[0];

                                            String[] sepLength = Topic.split("-");
                                            String topicLength = sepLength[0];
                                            String finalTopicName = sepLength[1];

                                            StringBuilder finalTopicLength = new StringBuilder(topicLength);
                                            // insert character value at offset 8
                                            finalTopicLength.insert(2, ':');
                                            finalTopicLength.insert(5, ':');

                                            Cursor mainCursor = videoHandler.getAllData();

                                            if (mainCursor.getCount() != 0){

                                                while (mainCursor.moveToNext()) {

                                                    String topic = mainCursor.getString(1);
                                                    Log.d("check",topic);

                                                    if (topic.equals(checkTopic)){

                                                        JSONObject subJsonObject = new JSONObject();
                                                        subJsonObject.put("topic_name",finalTopicName);
                                                        subJsonObject.put("topic_duration",finalTopicLength);
                                                        subJsonObject.put("totalDuration",mainCursor.getString(2));
                                                        subJsonObject.put("count", mainCursor.getString(3));

                                                        Log.d("CheckCount",mainCursor.getString(3));
                                                        subArray.put(subJsonObject);
                                                    }
                                                }
                                                mainCursor.close();

                                                if (!videoHandler.ifExists(checkTopic)){

                                                    JSONObject subJsonObject = new JSONObject();
                                                    subJsonObject.put("topic_name",finalTopicName);
                                                    subJsonObject.put("topic_duration",finalTopicLength);
                                                    subJsonObject.put("totalDuration",0);
                                                    subJsonObject.put("count",0);

                                                    subArray.put(subJsonObject);

                                                }

                                            } else {

                                                JSONObject subJsonObject = new JSONObject();
                                                subJsonObject.put("topic_name",finalTopicName);
                                                subJsonObject.put("topic_duration",finalTopicLength);
                                                subJsonObject.put("totalDuration",0);
                                                subJsonObject.put("count",0);

//                                            Add SubObject To SubArray
                                                subArray.put(subJsonObject);

                                                Log.d("Topic Name Converted",finalTopicName);
                                                Log.d("Topic Length Converted", String.valueOf(finalTopicLength));
                                            }
                                        }
                                    }

                                }else {
                                    Log.d("Topic List","Empty Content");
                                }

//                        Add SubArray To MainObject
                                mainJsonObject.put("topic_details",subArray);
                                mainArray.put(mainJsonObject);
                            }
                        }
                    } else {

                        Log.d("Module List","Empty Content");
                    }

                    Log.d("Module Json", String.valueOf(mainArray));
                }catch (JSONException e){

                    Log.d("JSONException",e.getMessage());

                }

            } else {

                Log.d("FolderInfo","Not found");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Restoration Process");
                builder.setMessage("Your file contents are missing do you want restore it, By free downloading?")
                        .setCancelable(false)
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                StoreEntireDetails storeEntireDetails = new StoreEntireDetails(getApplicationContext());
                                Cursor cursor = storeEntireDetails.getSubjectRow(currentSubjectName);
                                Log.d("cursorInfo", String.valueOf(cursor.getCount()));

                                if (cursor.getCount() != 0){

                                    if (cursor.moveToFirst()){

                                        do {

                                            Log.d("#country",cursor.getString(1)+"  ");
                                            Log.d("#university", cursor.getString(2)+"  ");
                                            Log.d("#course", cursor.getString(3)+"  ");
                                            Log.d("#sem", cursor.getString(4)+"  ");
                                            Log.d("#subject", cursor.getString(5)+"  ");
//                            subject = cursor.getString(5);
                                            Log.d("#sub_no", cursor.getString(6)+"  ");
                                            Log.d("#subject_id", cursor.getString(7)+"  ");
                                            Log.d("#free_validity", cursor.getString(8)+"  ");
//                            freeValidity = cursor.getString(8);
                                            Log.d("#paid_validity", cursor.getString(9)+"  ");
                                            Log.d("#duration", cursor.getString(10)+"  ");

                                            final String country = cursor.getString(1);
                                            final String university = cursor.getString(2);
                                            final String course = cursor.getString(3);
                                            final String semester = cursor.getString(4);
                                            final String subject = cursor.getString(5);
                                            final String subjectId = cursor.getString(6);
                                            final String subjectNumber = cursor.getString(7);
                                            final String freeValidity = cursor.getString(8);
                                            final String paidValidity = cursor.getString(9);
                                            final String duration = cursor.getString(10);
                                            final String videoCount = cursor.getString(11);
                                            final String notesCount = cursor.getString(12);
                                            final String qbankCount = cursor.getString(13);

//                            put values for download
                                            HashMap<String,String> params = new HashMap<String, String>();
                                            params.put("sem_no",semester);
                                            params.put("id",subjectId);
                                            params.put("sub_no",subjectNumber);
                                            params.put("type","url");

                                            Log.v("Hash Values",params.toString());

                                            url_arrayList = new ArrayList<String>();
                                            name_arrayList = new ArrayList<String>();

                                            AsyncUrl asyncUrl = new AsyncUrl(ModuleList.this,params,UrlResult,URL_JSON_ARRAY,
                                                    current_url,current_name,url_arrayList,name_arrayList,progressDialog);
                                            asyncUrl.execute(GET_URLS);

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                                @Override
                                                public void run() {
//                                    Do something after 100ms
                                                    Log.v("Check UrlArraylist", String.valueOf(AsyncUrl.url_arrayList.size()));
                                                    Log.v("Check NameArraylist", String.valueOf(AsyncUrl.name_arrayList.size()));

                                                    if (AsyncUrl.url_arrayList != null){

                                                        Log.v("Check Entry", String.valueOf(url_arrayList.size()));
                                                        final StoreCardAdapter adapter = new StoreCardAdapter(ModuleList.this);
                                                        adapter.DownloadFile(ModuleList.this,country,university,course,semester,subject,subjectId,
                                                                subjectNumber,freeValidity,paidValidity,duration,videoCount,notesCount,qbankCount);

                                                    }
                                                }
                                            }, 10000);

                                        }while (cursor.moveToNext());

                                    } cursor.close();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                                Intent intent = new Intent(ModuleList.this,Drawer.class);
                                startActivity(intent);
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }

//        ListOUt the SDCard Values

            ObjectMapper mapper = new ObjectMapper();
            List<ModuleItems> moduleItemses = new ArrayList<>();


            ArrayList<ModuleItems> moduleItemsList = new ArrayList<ModuleItems>();
            List<TopicItems> topicItemsList = new ArrayList<TopicItems>();

            try {
                moduleItemses =  mapper.readValue(String.valueOf(mainArray), new TypeReference<List<ModuleItems>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }

            ModuleItems moduleItems = new ModuleItems();
            TopicItems topicItems = new TopicItems();

            for (ModuleItems entityObject : moduleItemses){

                entityObject.setModule_name(entityObject.getModule_name());

                Log.d("Module Name", entityObject.getModule_name());

                for(int i = 0; i<entityObject.getTopic_details().size(); i++){

                    topicItems.setTopic_name(entityObject.getTopic_details().get(i).getTopic_name());
                    topicItems.setTopic_duration(entityObject.getTopic_details().get(i).getTopic_duration());
                    topicItems.setTotalDuration(entityObject.getTopic_details().get(i).getTotalDuration());
                    topicItems.setCount(entityObject.getTopic_details().get(i).getCount());


                    Log.d("TopicName ", entityObject.getTopic_details().get(i).getTopic_name());
                    Log.d("TopicDuration ", String.valueOf(entityObject.getTopic_details().get(i).getTopic_duration()));
                    Log.d("Count", String.valueOf(entityObject.getTopic_details().get(i).getCount()));
                    topicItemsList.add(topicItems);
                }

                moduleItems.setTopic_details(topicItemsList);

                moduleItemsList.add(entityObject);

            }
//        Initialize views
            moduleAdapter = new ModuleAdapter(this,moduleItemsList);
            moduleListView.setAdapter(moduleAdapter);
            moduleListView.deferNotifyDataSetChanged();

        } else {

            currentSubjectName = getIntent().getStringExtra("Key_subjectName");
            getSupportActionBar().setTitle(currentSubjectName);

            txtRibbon.setVisibility(View.VISIBLE);

//            ListOUt the Online values

            ObjectMapper mapper = new ObjectMapper();
            List<ModuleItems> moduleItemses = new ArrayList<>();

            ArrayList<ModuleItems> moduleItemsList = new ArrayList<ModuleItems>();
            List<TopicItems> topicItemsList = new ArrayList<TopicItems>();

            try {
                String onlineJsonArray = getIntent().getStringExtra("Key_OnlineJson");
                Log.d("OnlineJson",onlineJsonArray+"  ");

                moduleItemses =  mapper.readValue(String.valueOf(onlineJsonArray), new TypeReference<List<ModuleItems>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }

            ModuleItems moduleItems = new ModuleItems();
            TopicItems topicItems = new TopicItems();

            for (ModuleItems entityObject : moduleItemses){

                entityObject.setModule_name(entityObject.getModule_name());

                Log.d("Module Name", entityObject.getModule_name());

                for(int i = 0; i<entityObject.getTopic_details().size(); i++){

                    topicItems.setSubject_id(entityObject.getTopic_details().get(i).getSubject_id());
                    topicItems.setSem_no(entityObject.getTopic_details().get(i).getSem_no());
                    topicItems.setSub_no(entityObject.getTopic_details().get(i).getSub_no());
                    topicItems.setModule_no(entityObject.getTopic_details().get(i).getModule_no());
                    topicItems.setTopic_no(entityObject.getTopic_details().get(i).getTopic_no());
                    topicItems.setTopic_name(entityObject.getTopic_details().get(i).getTopic_name());
                    topicItems.setTopic_duration(entityObject.getTopic_details().get(i).getTopic_duration());

                    Log.d("subjectId ", entityObject.getTopic_details().get(i).getSubject_id());
                    Log.d("Sem_no", entityObject.getTopic_details().get(i).getSem_no());
                    Log.d("sub_no ", entityObject.getTopic_details().get(i).getSub_no());
                    Log.d("module_no ", entityObject.getTopic_details().get(i).getModule_no());
                    Log.d("topic_no ", entityObject.getTopic_details().get(i).getTopic_no());
                    Log.d("TopicName ", entityObject.getTopic_details().get(i).getTopic_name());
                    Log.d("TopicDuration ", String.valueOf(entityObject.getTopic_details().get(i).getTopic_duration()));
                    topicItemsList.add(topicItems);
                }

                moduleItems.setTopic_details(topicItemsList);

                moduleItemsList.add(entityObject);

            }
//        Initialize views
            moduleAdapter = new ModuleAdapter(this,moduleItemsList);
            moduleListView.setAdapter(moduleAdapter);
            moduleListView.deferNotifyDataSetChanged();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_submit).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_share:

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Select your option";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;

            case R.id.action_search : {

                String onlineSubject = getIntent().getStringExtra("Key_subjectName");
                String offlineSubject = getIntent().getStringExtra("Key_video");

                if (offlineSubject != null) {

                    SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                    moduleListView.setVisibility(View.GONE);
                    moduleFilterView.setVisibility(View.VISIBLE);
                    item.expandActionView();

                    filterView();

                    MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {

//                        Toast.makeText(getActivity(),"clicked",Toast.LENGTH_LONG).show();
//                        Write your code here
                            moduleFilterView.setVisibility(View.GONE);
                            moduleListView.setVisibility(View.VISIBLE);

                            return true;
                        }
                    });

                    search(searchView);
                }


                if (onlineSubject != null) {

                    SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                    moduleListView.setVisibility(View.GONE);
                    moduleFilterView.setVisibility(View.VISIBLE);
                    item.expandActionView();

                    filterView();

                    MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {

//                        Toast.makeText(getActivity(),"clicked",Toast.LENGTH_LONG).show();
//                        Write your code here
                            moduleFilterView.setVisibility(View.GONE);
                            moduleListView.setVisibility(View.VISIBLE);

                            return true;
                        }
                    });

                    search(searchView);
                }
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                cardFilterAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    public void filterView(){

        String onlineSubject = getIntent().getStringExtra("Key_subjectName");
        String offlineSubject = getIntent().getStringExtra("Key_video");

        if (offlineSubject != null){

            JSONArray mainArray = new JSONArray();
            txtRibbon.setVisibility(View.GONE);
            String MainDir = "Chathamkulam"+"/"+currentSubjectName;
            File mainPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), MainDir);

            if (mainPath.exists()){

                File moduleList[] = mainPath.listFiles();

//        Get Values frm SDCard
                try {

                    if (moduleList != null && moduleList.length > 0) {
                        for (File aModuleList : moduleList) {

                            if (aModuleList.isDirectory()) {

                                String moduleConvert = aModuleList.getName();
                                String moduleName = moduleConvert.replaceAll("%20","  ");

                                String SubDir = "Chathamkulam"+"/"+currentSubjectName+"/"+moduleName;
                                File subPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), SubDir);

                                File topicList[] = subPath.listFiles();

                                if (topicList != null && topicList.length > 0){

                                    for (File aTopicList : topicList){

                                        if (aTopicList.isFile()){

                                            String topicConvert = aTopicList.getName();
                                            String checkTopic = aTopicList.getAbsolutePath();
                                            String topicName = topicConvert.replaceAll("%20","  ");
                                            String[] sep = topicConvert.split("\\.");
                                            Log.d("###Topic",topicName);
                                            String Topic = sep[0];

                                            String[] sepLength = Topic.split("-");
                                            String topicLength = sepLength[0];
                                            String finalTopicName = sepLength[1];

                                            StringBuilder finalTopicLength = new StringBuilder(topicLength);
                                            // insert character value at offset 8
                                            finalTopicLength.insert(2, ':');
                                            finalTopicLength.insert(5, ':');

                                            Cursor mainCursor = videoHandler.getAllData();

                                            if (mainCursor.getCount() != 0){

                                                while (mainCursor.moveToNext()) {

                                                    String topic = mainCursor.getString(1);
                                                    Log.d("check",topic);

                                                    if (topic.equals(checkTopic)){

                                                        JSONObject mainJsonObject = new JSONObject();
                                                        mainJsonObject.put("module_name",moduleName);
                                                        mainJsonObject.put("topic_name",finalTopicName);
                                                        mainJsonObject.put("topic_duration",finalTopicLength);
                                                        mainJsonObject.put("totalDuration",mainCursor.getString(2));
                                                        mainJsonObject.put("count", mainCursor.getString(3));
                                                        mainArray.put(mainJsonObject);
                                                    }
                                                }
                                                mainCursor.close();

                                                if (!videoHandler.ifExists(checkTopic)){

                                                    JSONObject mainJsonObject = new JSONObject();
                                                    mainJsonObject.put("module_name",moduleName);
                                                    mainJsonObject.put("topic_name",finalTopicName);
                                                    mainJsonObject.put("topic_duration",finalTopicLength);
                                                    mainJsonObject.put("totalDuration",0);
                                                    mainJsonObject.put("count",0);
                                                    mainArray.put(mainJsonObject);

                                                }

                                            } else {

                                                JSONObject mainJsonObject = new JSONObject();
                                                mainJsonObject.put("module_name",moduleName);
                                                mainJsonObject.put("topic_name",finalTopicName);
                                                mainJsonObject.put("topic_duration",finalTopicLength);
                                                mainJsonObject.put("totalDuration",0);
                                                mainJsonObject.put("count",0);

//                                            Add SubObject To SubArray
                                                mainArray.put(mainJsonObject);

                                                Log.d("Topic Name Converted",finalTopicName);
                                                Log.d("Topic Length Converted", String.valueOf(finalTopicLength));
                                            }
                                        }
                                    }

                                }else {
                                    Log.d("Topic List","Empty Content");
                                }
                            }
                        }
                    } else {

                        Log.d("ModuleList","Empty Content");
                    }

                    Log.d("ModuleJson", String.valueOf(mainArray));
                }catch (JSONException e){

                    Log.d("JSONException",e.getMessage());

                }

            } else {

                Log.d("FolderInfo","Not found");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Restoration Process");
                builder.setMessage("Your file contents are missing do you want restore it, By free downloading?")
                        .setCancelable(false)
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                StoreEntireDetails storeEntireDetails = new StoreEntireDetails(getApplicationContext());
                                Cursor cursor = storeEntireDetails.getSubjectRow(currentSubjectName);
                                Log.d("cursorInfo", String.valueOf(cursor.getCount()));

                                if (cursor.getCount() != 0){

                                    if (cursor.moveToFirst()){

                                        do {

                                            Log.d("#country",cursor.getString(1)+"  ");
                                            Log.d("#university", cursor.getString(2)+"  ");
                                            Log.d("#course", cursor.getString(3)+"  ");
                                            Log.d("#sem", cursor.getString(4)+"  ");
                                            Log.d("#subject", cursor.getString(5)+"  ");
//                            subject = cursor.getString(5);
                                            Log.d("#sub_no", cursor.getString(6)+"  ");
                                            Log.d("#subject_id", cursor.getString(7)+"  ");
                                            Log.d("#free_validity", cursor.getString(8)+"  ");
//                            freeValidity = cursor.getString(8);
                                            Log.d("#paid_validity", cursor.getString(9)+"  ");
                                            Log.d("#duration", cursor.getString(10)+"  ");

                                            final String country = cursor.getString(1);
                                            final String university = cursor.getString(2);
                                            final String course = cursor.getString(3);
                                            final String semester = cursor.getString(4);
                                            final String subject = cursor.getString(5);
                                            final String subjectId = cursor.getString(6);
                                            final String subjectNumber = cursor.getString(7);
                                            final String freeValidity = cursor.getString(8);
                                            final String paidValidity = cursor.getString(9);
                                            final String duration = cursor.getString(10);
                                            final String videoCount = cursor.getString(11);
                                            final String notesCount = cursor.getString(12);
                                            final String qbankCount = cursor.getString(13);

//                            put values for download
                                            HashMap<String,String> params = new HashMap<String, String>();
                                            params.put("sem_no",semester);
                                            params.put("id",subjectId);
                                            params.put("sub_no",subjectNumber);
                                            params.put("type","url");

                                            Log.v("Hash Values",params.toString());

                                            url_arrayList = new ArrayList<String>();
                                            name_arrayList = new ArrayList<String>();

                                            AsyncUrl asyncUrl = new AsyncUrl(ModuleList.this,params,UrlResult,URL_JSON_ARRAY,
                                                    current_url,current_name,url_arrayList,name_arrayList,progressDialog);
                                            asyncUrl.execute(GET_URLS);

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                                @Override
                                                public void run() {
//                                    Do something after 100ms
                                                    Log.v("Check UrlArraylist", String.valueOf(AsyncUrl.url_arrayList.size()));
                                                    Log.v("Check NameArraylist", String.valueOf(AsyncUrl.name_arrayList.size()));

                                                    if (AsyncUrl.url_arrayList != null){

                                                        Log.v("Check Entry", String.valueOf(url_arrayList.size()));
                                                        final StoreCardAdapter adapter = new StoreCardAdapter(ModuleList.this);
                                                        adapter.DownloadFile(ModuleList.this,country,university,course,semester,subject,subjectId,
                                                                subjectNumber,freeValidity,paidValidity,duration,videoCount,notesCount,qbankCount);

                                                    }
                                                }
                                            }, 10000);

                                        }while (cursor.moveToNext());

                                    } cursor.close();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                                Intent intent = new Intent(ModuleList.this,Drawer.class);
                                startActivity(intent);
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }

//        ListOUt the SDCard Values

            ObjectMapper mapper = new ObjectMapper();
            List<ModuleItems> moduleItemses = new ArrayList<>();

            ArrayList<ModuleItems> moduleItemsList = new ArrayList<ModuleItems>();

            try {
                moduleItemses =  mapper.readValue(String.valueOf(mainArray), new TypeReference<List<ModuleItems>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (ModuleItems entityObject : moduleItemses){

                entityObject.setModule_name(entityObject.getModule_name());
                entityObject.setTopic_name(entityObject.getTopic_name());
                entityObject.setTopic_duration(entityObject.getTopic_duration());
                entityObject.setTotalDuration(entityObject.getTotalDuration());
                entityObject.setCount(entityObject.getCount());

//                Log.d("Module Name", entityObject.getModule_name());
                Log.d("TopicName ", entityObject.getTopic_name());
                Log.d("TopicDuration ", String.valueOf(entityObject.getTopic_duration()));
                Log.d("count ", String.valueOf(entityObject.getCount()));

                moduleItemsList.add(entityObject);

            }
//        Initialize views
            cardFilterAdapter = new ModuleCardFilterAdapter(this,moduleItemsList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            moduleFilterView.setHasFixedSize(true);
            moduleFilterView.setLayoutManager(layoutManager);
            moduleFilterView.setAdapter(cardFilterAdapter);

        }

        if (onlineSubject != null) {

            txtRibbon.setVisibility(View.VISIBLE);

//            ListOUt the Online values

            ObjectMapper mapper = new ObjectMapper();
            List<ModuleItems> moduleItemses = new ArrayList<>();

            ArrayList<ModuleItems> moduleItemsList = new ArrayList<ModuleItems>();
            List<TopicItems> topicItemsList = new ArrayList<TopicItems>();

            try {
                String onlineJsonArray = getIntent().getStringExtra("Key_OnlineSearchJson");
                moduleItemses =  mapper.readValue(String.valueOf(onlineJsonArray), new TypeReference<List<ModuleItems>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }

            ModuleItems moduleItems = new ModuleItems();
            TopicItems topicItems = new TopicItems();

            for (ModuleItems entityObject : moduleItemses){

                entityObject.setModule_name(entityObject.getModule_name());
                entityObject.setTopic_name(entityObject.getTopic_name());
                entityObject.setTopic_duration(entityObject.getTopic_duration());
                entityObject.setPauseDuration(entityObject.getPauseDuration());
                entityObject.setTotalDuration(entityObject.getTotalDuration());

//                Log.d("Module Name", entityObject.getModule_name());
                Log.d("TopicName ", entityObject.getTopic_name());
                Log.d("TopicDuration ", String.valueOf(entityObject.getTopic_duration()));
                Log.d("PauseDuration ", String.valueOf(entityObject.getPauseDuration()));

                moduleItemsList.add(entityObject);

            }

//        Initialize views
            cardFilterAdapter = new ModuleCardFilterAdapter(this,moduleItemsList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            moduleFilterView.setHasFixedSize(true);
            moduleFilterView.setLayoutManager(layoutManager);
            moduleFilterView.setAdapter(cardFilterAdapter);
        }
    }
}
