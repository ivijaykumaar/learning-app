package com.learning_app.user.chathamkulam.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Adapters.StoreMainListAdapter;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreEntityObjects;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreSubjectEntity;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Registration.Registration;
import com.learning_app.user.chathamkulam.StoreFilterRecycler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    View storeCard,mainList;

    CheckBox checkSub;

    public FMStore() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.storecard_to_fmstore, container, false);
        storeCard = view.findViewById(R.id.incStore);
        mainList = view.findViewById(R.id.incMainList);

        Registration.deleteCache(getActivity());

        mainListView = (ListView) mainList.findViewById(R.id.storeMainListView);
        btnOnlineView = (Button) mainList.findViewById(R.id.btnOnline);
        btnTrial = (Button) mainList.findViewById(R.id.btnTrial);
        btnBuy = (Button) mainList.findViewById(R.id.btnBuy);

        checkSub = (CheckBox)storeCard.findViewById(R.id.checkSubjects);

//        checkSub.setVisibility(View.GONE);

//        storeCard.setVisibility(View.GONE);
//        mainList.setVisibility(View.VISIBLE);

        GetJsonResponse();

        btnOnlineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkSub.isChecked()){

                    Toast.makeText(getActivity(),"Hi there", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_share).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search : {
                //Write here what to do you on click

                startActivity(new Intent(getActivity(), StoreFilterRecycler.class));

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
            Log.d("Count: ",entityObject.getCount());

            for(int i=0;i<entityObject.getSubject_details().size();i++){

                storeSubjectEntity.setSubject_name(entityObject.getSubject_details().get(i).getSubject_name());
                storeSubjectEntity.setPrice_type(entityObject.getSubject_details().get(i).getPrice_type());
                storeSubjectEntity.setAmount(entityObject.getSubject_details().get(i).getAmount());
                storeSubjectEntity.setFile(entityObject.getSubject_details().get(i).getFile());
                storeSubjectEntity.setFree_validity(entityObject.getSubject_details().get(i).getFree_validity());
                storeSubjectEntity.setPaid_validity(entityObject.getSubject_details().get(i).getPaid_validity());

                Log.d("subjectName: ", entityObject.getSubject_details().get(i).getSubject_name());
                subList.add(storeSubjectEntity);

            }

            objects.setSubject_details(subList);

            mainList.add(entityObject);

        }
//        Initializing MainList Items
        storeMainListAdapter = new StoreMainListAdapter(getActivity(), mainList);
        mainListView.setAdapter(storeMainListAdapter);

    }
}