package com.learning_app.user.chathamkulam.Adapters;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.learning_app.user.chathamkulam.Model.StoreModel.StoreEntityObjects;
import com.learning_app.user.chathamkulam.R;

import java.util.ArrayList;

/**
 * Created by User on 5/22/2017.
 */

public class StoreMainListAdapter extends BaseAdapter {

    public static ArrayList<StoreEntityObjects> REStoreList;
    private Activity context;
    private LayoutInflater inflater;
    private ArrayList<StoreEntityObjects> mainArrayList = new ArrayList();

    private boolean loading = true;
//    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;

    public StoreMainListAdapter(Activity context, ArrayList<StoreEntityObjects> mainArrayList) {
        this.context = context;
        this.mainArrayList = mainArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mainArrayList.size();
    }

    @Override
    public StoreEntityObjects getItem(int position) {
        return mainArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.store_mainlist_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        StoreEntityObjects storeEntityObjects = getItem(position);

        mViewHolder.txtCountry.setText(storeEntityObjects.getCountry());
        mViewHolder.txtUniversity.setText(storeEntityObjects.getUniversity());
        mViewHolder.txtCourse.setText(storeEntityObjects.getCourse());

        if (storeEntityObjects.getSem_no().equals("0")) {
            mViewHolder.txtSemester.setText("Semester  N/A");
        } else {
            mViewHolder.txtSemester.setText("Semester  "+ storeEntityObjects.getSem_no());
        }

//        Finally initializing our adapter
        REStoreList = new ArrayList<>();

        for (int i = 0; i < storeEntityObjects.getSubject_details().size(); i++) {
            REStoreList.add(storeEntityObjects);
        }

        StoreCardAdapter adapter = new StoreCardAdapter(context, REStoreList);
        mViewHolder.recyclerView.setAdapter(adapter);

        mViewHolder.countTxt.setText("<"+adapter.getItemCount()+">");

        return convertView;
    }

    private class MyViewHolder {
        TextView txtCountry, txtUniversity, txtCourse, txtSemester,countTxt;
        RecyclerView recyclerView;

        MyViewHolder(View view) {

            //Initializing Views
            txtCountry = (TextView) view.findViewById(R.id.txtCountry);
            txtUniversity = (TextView) view.findViewById(R.id.txtUniversity);
            txtCourse = (TextView) view.findViewById(R.id.txtCourse);
            txtSemester = (TextView) view.findViewById(R.id.txtSemester);
            recyclerView = (RecyclerView) view.findViewById(R.id.store_recycler_view);
            countTxt = (TextView)view.findViewById(R.id.countTxt);
        }
    }
}
