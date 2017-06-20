package com.learning_app.user.chathamkulam.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Adapters.ModuleAdapter;
import com.learning_app.user.chathamkulam.Model.ModuleModel.ModuleItems;
import com.learning_app.user.chathamkulam.Model.ModuleModel.TopicItems;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.VideoHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 6/1/2017.
 */

public class ModuleList extends AppCompatActivity {

    ListView moduleListView;
    ModuleAdapter moduleAdapter;

    public static String currentSubjectName;
    TextView txtRibbon;
    VideoHandler videoHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_list);

        txtRibbon = (TextView)findViewById(R.id.txtRibbon);
        videoHandler = VideoHandler.getInstance(getApplicationContext());

//        Create Main Array
        JSONArray mainArray = new JSONArray();

        currentSubjectName = getIntent().getStringExtra("Key_video");
        getSupportActionBar().setTitle(currentSubjectName);

        if (currentSubjectName != null){

            txtRibbon.setVisibility(View.GONE);
            String MainDir = "Chathamkulam"+"/"+currentSubjectName;
            File mainPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), MainDir);
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

                            mainJsonObject.put("moduleName",moduleName);

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
                                        Log.d("Topic",topicName);
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
                                                    subJsonObject.put("topicName",finalTopicName);
                                                    subJsonObject.put("topicDuration",finalTopicLength);
                                                    subJsonObject.put("totalDuration",mainCursor.getString(2));
                                                    subJsonObject.put("pauseDuration", mainCursor.getString(3));

                                                    subArray.put(subJsonObject);
                                                }
                                            }
                                            mainCursor.close();

                                            if (!videoHandler.ifExists(checkTopic)){

                                                JSONObject subJsonObject = new JSONObject();
                                                subJsonObject.put("topicName",finalTopicName);
                                                subJsonObject.put("topicDuration",finalTopicLength);
                                                subJsonObject.put("totalDuration",0);
                                                subJsonObject.put("pauseDuration",0);

                                                subArray.put(subJsonObject);

                                            }

                                        } else {

                                            JSONObject subJsonObject = new JSONObject();
                                            subJsonObject.put("topicName",finalTopicName);
                                            subJsonObject.put("topicDuration",finalTopicLength);
                                            subJsonObject.put("totalDuration",0);
                                            subJsonObject.put("pauseDuration",0);

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
                            mainJsonObject.put("topicItems",subArray);
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

                entityObject.setModuleName(entityObject.getModuleName());

                Log.d("Module Name", entityObject.getModuleName());

                for(int i = 0; i<entityObject.getTopicItems().size(); i++){

                    topicItems.setTopicName(entityObject.getTopicItems().get(i).getTopicName());
                    topicItems.setTopicDuration(entityObject.getTopicItems().get(i).getTopicDuration());
                    topicItems.setPauseDuration(entityObject.getTopicItems().get(i).getPauseDuration());
                    topicItems.setTotalDuration(entityObject.getTopicItems().get(i).getTotalDuration());

                    Log.d("TopicName ", entityObject.getTopicItems().get(i).getTopicName());
                    Log.d("TopicDuration ", String.valueOf(entityObject.getTopicItems().get(i).getTopicDuration()));
                    Log.d("PauseDuration ", String.valueOf(entityObject.getTopicItems().get(i).getPauseDuration()));
                    topicItemsList.add(topicItems);
                }

                moduleItems.setTopicItems(topicItemsList);

                moduleItemsList.add(entityObject);

            }
//        Initialize views
            moduleListView = (ListView)findViewById(R.id.moduleList);
            moduleAdapter = new ModuleAdapter(getApplicationContext(),moduleItemsList);
            moduleListView.setAdapter(moduleAdapter);

        } else {

            txtRibbon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

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

        }

        return super.onOptionsItemSelected(item);
    }
}
