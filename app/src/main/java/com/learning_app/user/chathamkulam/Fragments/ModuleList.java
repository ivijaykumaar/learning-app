package com.learning_app.user.chathamkulam.Fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Adapters.ModuleAdapter;
import com.learning_app.user.chathamkulam.Model.ModuleModel.ModuleItems;
import com.learning_app.user.chathamkulam.Model.ModuleModel.TopicItems;
import com.learning_app.user.chathamkulam.R;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_list);

//        Create Main Array
        JSONArray mainArray = new JSONArray();

        currentSubjectName = getIntent().getStringExtra("Key_video");
        getSupportActionBar().setTitle(currentSubjectName);

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

                        Log.d("Module Name Converted",moduleName);

                        mainJsonObject.put("moduleName",moduleName);

                        String SubDir = "Chathamkulam"+"/"+currentSubjectName+"/"+moduleName;
                        File subPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), SubDir);

                        File topicList[] = subPath.listFiles();

                        if (topicList != null && topicList.length > 0){
                            for (File aTopicList : topicList){

                                if (aTopicList.isFile()){

                                    String topicConvert = aTopicList.getName();
                                    String topicName = topicConvert.replaceAll("%20","  ");
                                    String[] sep = topicConvert.split("\\.");
                                    Log.d("Topic",topicName);
                                    String finalTopic = sep[0];

//                                    Create Sub Object
                                    JSONObject subJsonObject = new JSONObject();
                                    subJsonObject.put("topicName",finalTopic);
                                    subJsonObject.put("topicDuration","00.00.00 Sec");

//                                    Add SubObject To SubArray
                                    subArray.put(subJsonObject);

                                    Log.d("Topic Name Converted",finalTopic);
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

                Log.d("TopicName: ", entityObject.getTopicItems().get(i).getTopicName());
                topicItemsList.add(topicItems);
            }

            moduleItems.setTopicItems(topicItemsList);

            moduleItemsList.add(entityObject);

        }
//        Initialize views
        moduleListView = (ListView)findViewById(R.id.moduleList);
        moduleAdapter = new ModuleAdapter(getApplicationContext(),moduleItemsList);
        moduleListView.setAdapter(moduleAdapter);

    }
}
