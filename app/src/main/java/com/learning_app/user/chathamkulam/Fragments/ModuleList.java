package com.learning_app.user.chathamkulam.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
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
import com.learning_app.user.chathamkulam.FetchDownloadManager;
import com.learning_app.user.chathamkulam.Model.ModuleModel.ModuleItems;
import com.learning_app.user.chathamkulam.Model.ModuleModel.TopicItems;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.SearchFilters.ModuleCardFilterAdapter;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;
import com.learning_app.user.chathamkulam.Sqlite.VideoHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 6/1/2017.
 */

public class ModuleList extends AppCompatActivity {

    public static String currentSubjectName;
    ListView moduleListView;
    ModuleAdapter moduleAdapter;
    RecyclerView moduleFilterView;
    ModuleCardFilterAdapter cardFilterAdapter;
    TextView txtRibbon;
    VideoHandler videoHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_list);

        txtRibbon = (TextView) findViewById(R.id.txtRibbon);
        videoHandler = VideoHandler.getInstance(getApplicationContext());
        moduleFilterView = (RecyclerView) findViewById(R.id.moduleFilterView);
        moduleListView = (ListView) findViewById(R.id.moduleList);
        moduleFilterView.setVisibility(View.GONE);

//        Create Main Array
        final JSONArray mainArray = new JSONArray();

        currentSubjectName = getIntent().getStringExtra("Key_video");
        getSupportActionBar().setTitle(currentSubjectName);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            public void run() {

                if (currentSubjectName != null) {

                    txtRibbon.setVisibility(View.GONE);

                    File mydir = getApplicationContext().getDir("Chathamkulam", Context.MODE_PRIVATE);
                    File mainPath = new File(mydir, currentSubjectName);

                    if (mainPath.exists()) {

                        File[] moduleList = mainPath.listFiles();
                        Arrays.sort(moduleList);

//        Get Values frm SDCard
                        try {

                            if (moduleList != null && moduleList.length > 0) {
                                for (File aModuleList : moduleList) {

//                Create Main Object
                                    JSONObject mainJsonObject = new JSONObject();

//                    Create Sub Array
                                    JSONArray subArray = new JSONArray();

                                    if (aModuleList.isDirectory()) {

                                        String moduleName = aModuleList.getName();

                                        mainJsonObject.put("module_name", moduleName);
//                                        Log.d("#check",moduleName);

                                        File mydirr = getApplicationContext().getDir("Chathamkulam", Context.MODE_PRIVATE);
                                        File mainPathh = new File(mydirr, currentSubjectName);
                                        File subPath = new File(mainPathh, moduleName);

                                        File[] topicList = subPath.listFiles();
                                        Arrays.sort(topicList);

                                        if (topicList != null && topicList.length > 0) {

                                            for (File aTopicList : topicList) {

                                                if (aTopicList.isFile()) {

                                                    String topicConvert = aTopicList.getName();
                                                    String checkTopic = aTopicList.getAbsolutePath();

//                                                    String data = "00-01-00-Finance Toic1.mp4";
//                                                    Log.d("#check",topicConvert);
                                                    String[] sep = topicConvert.split("-", 4);
                                                    String topicDuration = sep[0] + ":" + sep[1] + ":" + sep[2];
                                                    String fileName = sep[3];

//                                                    Log.d("#check",fileName+" ");
                                                    String[] sepp = fileName.split("\\.");
                                                    String topicName = sepp[0];
                                                    String type = sepp[1];

                                                    Cursor mainCursor = videoHandler.getAllData();

                                                    if (mainCursor.getCount() != 0) {

                                                        while (mainCursor.moveToNext()) {

                                                            String topic = mainCursor.getString(1);

                                                            if (topic.equals(checkTopic)) {

                                                                JSONObject subJsonObject = new JSONObject();
                                                                subJsonObject.put("topic_name", topicName);
                                                                subJsonObject.put("topic_duration", topicDuration);
                                                                subJsonObject.put("totalDuration", mainCursor.getString(2));
                                                                subJsonObject.put("count", mainCursor.getString(3));

                                                                subArray.put(subJsonObject);
                                                            }
                                                        }
                                                        mainCursor.close();

                                                        if (!videoHandler.ifExists(checkTopic)) {

                                                            JSONObject subJsonObject = new JSONObject();
                                                            subJsonObject.put("topic_name", topicName);
                                                            subJsonObject.put("topic_duration", topicDuration);
                                                            subJsonObject.put("totalDuration", 0);
                                                            subJsonObject.put("count", 0);

                                                            subArray.put(subJsonObject);

                                                        }

                                                    } else {

                                                        JSONObject subJsonObject = new JSONObject();
                                                        subJsonObject.put("topic_name", topicName);
                                                        subJsonObject.put("topic_duration", topicDuration);
                                                        subJsonObject.put("totalDuration", 0);
                                                        subJsonObject.put("count", 0);

//                                            Add SubObject To SubArray
                                                        subArray.put(subJsonObject);
                                                    }
                                                }
                                            }

                                        } else {
                                            Log.d("Topic List", "Empty Content");
                                        }

//                        Add SubArray To MainObject
                                        mainJsonObject.put("topic_details", subArray);
                                        mainArray.put(mainJsonObject);
                                    }
                                }
                            } else {

                                Log.d("Module List", "Empty Content");
                            }

                            Log.d("Module Json", String.valueOf(mainArray));
                        } catch (JSONException e) {

                            Log.d("JSONException", e.getMessage());

                        }

                    } else {

                        Log.d("FolderInfo", "Not found");

                        final AlertDialog.Builder builder = new AlertDialog.Builder(ModuleList.this);
                        builder.setTitle("Restoration Process");
                        builder.setMessage("Your file contents are missing do you want restore it, By free downloading?")
                                .setCancelable(false)
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    public void onClick(DialogInterface dialog, int id) {

                                        StoreEntireDetails storeEntireDetails = new StoreEntireDetails(getApplicationContext());
                                        Cursor cursor = storeEntireDetails.getSubjectRow(currentSubjectName);
                                        Log.d("cursorInfo", String.valueOf(cursor.getCount()));

                                        if (cursor.getCount() != 0) {

                                            if (cursor.moveToFirst()) {

                                                do {

                                                    String country = cursor.getString(1);
                                                    String university = cursor.getString(2);
                                                    String course = cursor.getString(3);
                                                    String semester = cursor.getString(4);
                                                    String subjectId = cursor.getString(5);
                                                    String subjectNumber = cursor.getString(6);
                                                    String subject = cursor.getString(7);
                                                    String subjectCost = cursor.getString(8);
                                                    String trial = cursor.getString(9);
                                                    String duration = cursor.getString(10);
                                                    String notes_count = cursor.getString(11);
                                                    String qbank_count = cursor.getString(12);
                                                    String video_count = cursor.getString(13);
                                                    String zip_url = cursor.getString(14);
                                                    String validityTill = cursor.getString(15);
                                                    String progress = cursor.getString(16);
                                                    String status = cursor.getString(17);

                                                    new Thread(new FetchDownloadManager(zip_url, country, university, course, semester, subjectId, subjectNumber, subject,
                                                            subjectCost, trial, duration, notes_count, qbank_count, video_count, getApplicationContext(), "restore")).start();

                                                    startActivity(new Intent(getApplicationContext(), Drawer.class));

                                                } while (cursor.moveToNext());

                                            }
                                            cursor.close();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.cancel();
                                        Intent intent = new Intent(ModuleList.this, Drawer.class);
                                        startActivity(intent);
                                    }
                                });

                        handler.post(new Runnable() {
                            public void run() {

                                AlertDialog alert = builder.create();
                                alert.show();

                            }
                        });
                    }

//        ListOUt the SDCard Values

                    ObjectMapper mapper = new ObjectMapper();
                    List<ModuleItems> moduleItemses = new ArrayList<>();

                    final ArrayList<ModuleItems> moduleItemsList = new ArrayList<ModuleItems>();
                    List<TopicItems> topicItemsList = new ArrayList<TopicItems>();

                    try {
                        moduleItemses = mapper.readValue(String.valueOf(mainArray), new TypeReference<List<ModuleItems>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ModuleItems moduleItems = new ModuleItems();
                    TopicItems topicItems = new TopicItems();

                    for (ModuleItems entityObject : moduleItemses) {

                        entityObject.setModule_name(entityObject.getModule_name());

                        for (int i = 0; i < entityObject.getTopic_details().size(); i++) {

                            topicItems.setTopic_name(entityObject.getTopic_details().get(i).getTopic_name());
                            topicItems.setTopic_duration(entityObject.getTopic_details().get(i).getTopic_duration());
                            topicItems.setTotalDuration(entityObject.getTopic_details().get(i).getTotalDuration());
                            topicItems.setCount(entityObject.getTopic_details().get(i).getCount());

                            topicItemsList.add(topicItems);
                        }

                        moduleItems.setTopic_details(topicItemsList);

                        moduleItemsList.add(entityObject);

                    }

                    handler.post(new Runnable() {
                        public void run() {

                            //        Initialize views
                            moduleAdapter = new ModuleAdapter(ModuleList.this, moduleItemsList);
                            moduleListView.setAdapter(moduleAdapter);
                            moduleListView.deferNotifyDataSetChanged();

                        }
                    });


                } else {

                    currentSubjectName = getIntent().getStringExtra("Key_subjectName");
                    getSupportActionBar().setTitle(currentSubjectName);

                    txtRibbon.setVisibility(View.VISIBLE);

//            ListOUt the Online values

                    ObjectMapper mapper = new ObjectMapper();
                    List<ModuleItems> moduleItemses = new ArrayList<>();

                    final ArrayList<ModuleItems> moduleItemsList = new ArrayList<ModuleItems>();
                    List<TopicItems> topicItemsList = new ArrayList<TopicItems>();

                    try {
                        String onlineJsonArray = getIntent().getStringExtra("Key_OnlineJson");
                        Log.d("OnlineJson", onlineJsonArray + "  ");

                        moduleItemses = mapper.readValue(String.valueOf(onlineJsonArray), new TypeReference<List<ModuleItems>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ModuleItems moduleItems = new ModuleItems();
                    TopicItems topicItems = new TopicItems();

                    for (ModuleItems entityObject : moduleItemses) {

                        entityObject.setModule_name(entityObject.getModule_name());
                        entityObject.setSubject_id(entityObject.getSubject_id());
                        entityObject.setSem_no(entityObject.getSem_no());
                        entityObject.setSub_no(entityObject.getSub_no());

//                        Log.d("moduleName", entityObject.getModule_name());
//                        Log.d("subjectId",entityObject.getSubject_id());
//                        Log.d("semNo",entityObject.getSem_no());
//                        Log.d("subjectNo",entityObject.getSub_no());

                        for (int i = 0; i < entityObject.getTopic_details().size(); i++) {

                            topicItems.setModule_no(entityObject.getTopic_details().get(i).getModule_no());
                            topicItems.setTopic_no(entityObject.getTopic_details().get(i).getTopic_no());
                            topicItems.setTopic_name(entityObject.getTopic_details().get(i).getTopic_name());
                            topicItems.setTopic_duration(entityObject.getTopic_details().get(i).getTopic_duration());

//                            Log.d("moduleNo",entityObject.getTopic_details().get(i).getModule_no());
//                            Log.d("topicNo",entityObject.getTopic_details().get(i).getTopic_no());
//                            Log.d("topicName",entityObject.getTopic_details().get(i).getTopic_name());
//                            Log.d("topicDuration",entityObject.getTopic_details().get(i).getTopic_duration());

                            topicItemsList.add(topicItems);
                        }

                        moduleItems.setTopic_details(topicItemsList);
                        moduleItemsList.add(entityObject);

                    }

                    handler.post(new Runnable() {
                        public void run() {

                            //        Initialize views
                            moduleAdapter = new ModuleAdapter(ModuleList.this, moduleItemsList);
                            moduleListView.setAdapter(moduleAdapter);
                            moduleListView.deferNotifyDataSetChanged();

                        }
                    });
                }
            }

        };
        new Thread(runnable).start();
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

            case R.id.action_search: {

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

    public void filterView() {

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            public void run() {

                String onlineSubject = getIntent().getStringExtra("Key_subjectName");
                String offlineSubject = getIntent().getStringExtra("Key_video");

                if (offlineSubject != null) {

                    JSONArray mainArray = new JSONArray();
                    txtRibbon.setVisibility(View.GONE);
                    File mydir = getApplicationContext().getDir("Chathamkulam", Context.MODE_PRIVATE);
                    File mainPath = new File(mydir, currentSubjectName);

                    if (mainPath.exists()) {

                        File[] moduleList = mainPath.listFiles();
                        Arrays.sort(moduleList);

//        Get Values frm SDCard
                        try {

                            if (moduleList != null && moduleList.length > 0) {
                                for (File aModuleList : moduleList) {

                                    if (aModuleList.isDirectory()) {

                                        String moduleName = aModuleList.getName();

                                        File mydirr = getApplicationContext().getDir("Chathamkulam", Context.MODE_PRIVATE);
                                        File mainPathh = new File(mydirr, currentSubjectName);
                                        File subPath = new File(mainPathh, moduleName);

                                        File[] topicList = subPath.listFiles();
                                        Arrays.sort(topicList);

                                        if (topicList != null && topicList.length > 0) {

                                            for (File aTopicList : topicList) {

                                                if (aTopicList.isFile()) {

                                                    String topicConvert = aTopicList.getName();
                                                    String checkTopic = aTopicList.getAbsolutePath();

                                                    String[] sep = topicConvert.split("-", 4);
                                                    String topicDuration = sep[0] + ":" + sep[1] + ":" + sep[2];
                                                    String fileName = sep[3];

//                                                    Log.d("#check",fileName+" ");
                                                    String[] sepp = fileName.split("\\.");
                                                    String topicName = sepp[0];
                                                    String type = sepp[1];

                                                    Cursor mainCursor = videoHandler.getAllData();

                                                    if (mainCursor.getCount() != 0) {

                                                        while (mainCursor.moveToNext()) {

                                                            String topic = mainCursor.getString(1);

                                                            if (topic.equals(checkTopic)) {

                                                                JSONObject mainJsonObject = new JSONObject();
                                                                mainJsonObject.put("module_name", moduleName);
                                                                mainJsonObject.put("topic_name", topicName);
                                                                mainJsonObject.put("topic_duration", topicDuration);
                                                                mainJsonObject.put("totalDuration", mainCursor.getString(2));
                                                                mainJsonObject.put("count", mainCursor.getString(3));
                                                                mainArray.put(mainJsonObject);
                                                            }
                                                        }
                                                        mainCursor.close();

                                                        if (!videoHandler.ifExists(checkTopic)) {

                                                            JSONObject mainJsonObject = new JSONObject();
                                                            mainJsonObject.put("module_name", moduleName);
                                                            mainJsonObject.put("topic_name", topicName);
                                                            mainJsonObject.put("topic_duration", topicDuration);
                                                            mainJsonObject.put("totalDuration", 0);
                                                            mainJsonObject.put("count", 0);
                                                            mainArray.put(mainJsonObject);

                                                        }

                                                    } else {

                                                        JSONObject mainJsonObject = new JSONObject();
                                                        mainJsonObject.put("module_name", moduleName);
                                                        mainJsonObject.put("topic_name", topicName);
                                                        mainJsonObject.put("topic_duration", topicDuration);
                                                        mainJsonObject.put("totalDuration", 0);
                                                        mainJsonObject.put("count", 0);

//                                            Add SubObject To SubArray
                                                        mainArray.put(mainJsonObject);

                                                    }
                                                }
                                            }

                                        } else {
                                            Log.d("Topic List", "Empty Content");
                                        }
                                    }
                                }
                            } else {

                                Log.d("ModuleList", "Empty Content");
                            }

                            Log.d("ModuleJson", String.valueOf(mainArray));
                        } catch (JSONException e) {

                            Log.d("JSONException", e.getMessage());

                        }

                    } else {

                        Log.d("FolderInfo", "Not found");

                        final AlertDialog.Builder builder = new AlertDialog.Builder(ModuleList.this);
                        builder.setTitle("Restoration Process");
                        builder.setMessage("Your file contents are missing do you want restore it, By free downloading?")
                                .setCancelable(false)
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    public void onClick(DialogInterface dialog, int id) {

                                        StoreEntireDetails storeEntireDetails = new StoreEntireDetails(getApplicationContext());
                                        Cursor cursor = storeEntireDetails.getSubjectRow(currentSubjectName);
                                        Log.d("cursorInfo", String.valueOf(cursor.getCount()));

                                        if (cursor.getCount() != 0) {

                                            if (cursor.moveToFirst()) {

                                                do {

                                                    String country = cursor.getString(1);
                                                    String university = cursor.getString(2);
                                                    String course = cursor.getString(3);
                                                    String semester = cursor.getString(4);
                                                    String subjectId = cursor.getString(5);
                                                    String subjectNumber = cursor.getString(6);
                                                    String subject = cursor.getString(7);
                                                    String subjectCost = cursor.getString(8);
                                                    String trial = cursor.getString(9);
                                                    String duration = cursor.getString(10);
                                                    String notes_count = cursor.getString(11);
                                                    String qbank_count = cursor.getString(12);
                                                    String video_count = cursor.getString(13);
                                                    String zip_url = cursor.getString(14);
                                                    String validityTill = cursor.getString(15);
                                                    String progress = cursor.getString(16);
                                                    String status = cursor.getString(17);

                                                    new Thread(new FetchDownloadManager(zip_url, country, university, course, semester, subjectId, subjectNumber, subject,
                                                            subjectCost, trial, duration, notes_count, qbank_count, video_count, getApplicationContext(), "restore")).start();

                                                    startActivity(new Intent(getApplicationContext(), Drawer.class));

                                                } while (cursor.moveToNext());

                                            }
                                            cursor.close();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.cancel();
                                        Intent intent = new Intent(ModuleList.this, Drawer.class);
                                        startActivity(intent);
                                    }
                                });

                        handler.post(new Runnable() {
                            public void run() {

                                AlertDialog alert = builder.create();
                                alert.show();

                            }
                        });
                    }

//        ListOUt the SDCard Values

                    ObjectMapper mapper = new ObjectMapper();
                    List<ModuleItems> moduleItemses = new ArrayList<>();

                    final ArrayList<ModuleItems> moduleItemsList = new ArrayList<ModuleItems>();

                    try {
                        moduleItemses = mapper.readValue(String.valueOf(mainArray), new TypeReference<List<ModuleItems>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (ModuleItems entityObject : moduleItemses) {

                        entityObject.setModule_name(entityObject.getModule_name());
                        entityObject.setTopic_name(entityObject.getTopic_name());
                        entityObject.setTopic_duration(entityObject.getTopic_duration());
                        entityObject.setTotalDuration(entityObject.getTotalDuration());
                        entityObject.setCount(entityObject.getCount());

//                        Log.d("moduleName", entityObject.getModule_name());
//                        Log.d("topicName",entityObject.getTopic_name());
//                        Log.d("topicDuration",entityObject.getTopic_duration());
//                        Log.d("totalDuration",entityObject.getTotalDuration());
//                        Log.d("count",entityObject.getCount());

                        moduleItemsList.add(entityObject);

                    }

                    handler.post(new Runnable() {
                        public void run() {

                            //        Initialize views
                            cardFilterAdapter = new ModuleCardFilterAdapter(ModuleList.this, moduleItemsList);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            moduleFilterView.setHasFixedSize(true);
                            moduleFilterView.setLayoutManager(layoutManager);
                            moduleFilterView.setAdapter(cardFilterAdapter);

                        }
                    });
                }

                if (onlineSubject != null) {

                    txtRibbon.setVisibility(View.VISIBLE);

//            ListOUt the Online values

                    ObjectMapper mapper = new ObjectMapper();
                    List<ModuleItems> moduleItemses = new ArrayList<>();

                    final ArrayList<ModuleItems> moduleItemsList = new ArrayList<ModuleItems>();
                    List<TopicItems> topicItemsList = new ArrayList<TopicItems>();

                    try {
                        String onlineJsonArray = getIntent().getStringExtra("Key_OnlineSearchJson");
                        moduleItemses = mapper.readValue(String.valueOf(onlineJsonArray), new TypeReference<List<ModuleItems>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ModuleItems moduleItems = new ModuleItems();
                    TopicItems topicItems = new TopicItems();

                    for (ModuleItems entityObject : moduleItemses) {

                        entityObject.setModule_name(entityObject.getModule_name());
                        entityObject.setTopic_name(entityObject.getTopic_name());
                        entityObject.setTopic_duration(entityObject.getTopic_duration());
                        entityObject.setPauseDuration(entityObject.getPauseDuration());
                        entityObject.setTotalDuration(entityObject.getTotalDuration());

                        moduleItemsList.add(entityObject);

                    }

                    handler.post(new Runnable() {
                        public void run() {

                            //        Initialize views
                            cardFilterAdapter = new ModuleCardFilterAdapter(ModuleList.this, moduleItemsList);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            moduleFilterView.setHasFixedSize(true);
                            moduleFilterView.setLayoutManager(layoutManager);
                            moduleFilterView.setAdapter(cardFilterAdapter);

                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }
}
