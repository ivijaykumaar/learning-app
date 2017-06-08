package com.learning_app.user.chathamkulam;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreEntityObjects;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreFilterItems;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreSubjectEntity;
import com.learning_app.user.chathamkulam.Registration.Registration;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.learning_app.user.chathamkulam.Model.Constants.STORE_DATA;

/**
 * Created by User on 6/5/2017.
 */

public class StoreFilterRecycler extends AppCompatActivity implements SearchView.OnQueryTextListener {

    RecyclerView recyclerView;
    StoreFilterAdapter storeFilterAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_recycler);


        String json = getString(R.string.jsonObject);

        ObjectMapper mapper = new ObjectMapper();
        List<StoreFilterItems> storeFilterItemses = new ArrayList<>();

        JSONArray jsonObject = new JSONArray();
        try {
            jsonObject = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("json ", jsonObject.toString());

        try {
            storeFilterItemses =  mapper.readValue(String.valueOf(jsonObject), new TypeReference<List<StoreFilterItems>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<StoreFilterItems> mainList = new ArrayList<StoreFilterItems>();

        for (StoreFilterItems entityObject : storeFilterItemses){

            entityObject.setSubject_name(entityObject.getSubject_name());
            entityObject.setAmount(entityObject.getAmount());
            entityObject.setPrice_type(entityObject.getPrice_type());
            entityObject.setFree_validity(entityObject.getFree_validity());
            entityObject.setPaid_validity(entityObject.getPaid_validity());
            entityObject.setFile(entityObject.getFile());
            entityObject.setSubject_number(entityObject.getSubject_number());
            entityObject.setSubject_id(entityObject.getSubject_id());
            entityObject.setSemester(entityObject.getSemester());

            Log.d("Subject ", entityObject.getSubject_name());
            Log.d("File ",entityObject.getFile());

            mainList.add(entityObject);

        }


        //        Initializing MainList Items
        recyclerView = (RecyclerView)findViewById(R.id.filterRecycler);
        storeFilterAdapter = new StoreFilterAdapter(this,mainList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(storeFilterAdapter);

//        GetJsonResponse();
    }

    //        This method will get data from the web api
    public void GetJsonResponse() {

//        Clear cache files
        Registration.deleteCache(this);
//        Showing progress
        final ProgressDialog loading = ProgressDialog.show(this, "Loading Data", "Please wait...", false, false);

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

                Toast.makeText(getApplicationContext(),"Check Your Network Connection !! ", Toast.LENGTH_SHORT).show();
                Log.d("VolleyError", error.toString());

            }
        });

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

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
////        Initializing MainList Items
//        recyclerView = (RecyclerView)findViewById(R.id.filterRecycler);
//        storeCardAdapter = new StoreCardAdapter(this,mainList);
//
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setAdapter(storeCardAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

//        final MenuItem item = menu.findItem(R.id.action_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//        searchView.setOnQueryTextListener(this);
//
//        MenuItemCompat.setOnActionExpandListener(item,
//                new MenuItemCompat.OnActionExpandListener() {
//                    @Override
//                    public boolean onMenuItemActionCollapse(MenuItem item) {
//// Do something when collapsed
//                        storeCardAdapter.setFilter(mainList);
//                        return true; // Return true to collapse action view
//                    }
//
//                    @Override
//                    public boolean onMenuItemActionExpand(MenuItem item) {
//// Do something when expanded
//                        return true; // Return true to expand action view
//                    }
//                });

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

//        final List<StoreEntityObjects> filteredModelList = filter(mainList, newText);
//
//        storeCardAdapter.setFilter(filteredModelList);

        return true;
    }

    private List<StoreEntityObjects> filter(List<StoreEntityObjects> models, String query) {
        query = query.toLowerCase();
        final List<StoreEntityObjects> filteredModelList = new ArrayList<>();
        for (StoreEntityObjects model : models) {
            final String text = model.getCourse().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
