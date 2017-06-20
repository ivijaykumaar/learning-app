package com.learning_app.user.chathamkulam.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.learning_app.user.chathamkulam.Model.DashboardModel.DashEntityObjects;
import com.learning_app.user.chathamkulam.R;

import java.util.ArrayList;

/**
 * Created by User on 5/22/2017.
 */

public class DashMainListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<DashEntityObjects> mainArrayList = new ArrayList();

    public DashMainListAdapter(Context context, ArrayList<DashEntityObjects> mainArrayList) {
        this.context = context;
        this.mainArrayList = mainArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mainArrayList.size();
    }

    @Override
    public DashEntityObjects getItem(int position) {
        return mainArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dash_mainlist_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        DashEntityObjects dashEntityObjects = getItem(position);

        mViewHolder.txtCountry.setText(dashEntityObjects.getCountry());
        mViewHolder.txtUniversity.setText(dashEntityObjects.getUniversity());
        mViewHolder.txtCourse.setText(dashEntityObjects.getCourse());

        if (dashEntityObjects.getSemester().equals("0")){
            mViewHolder.txtSemester.setText("Semester  N/A");
        } else {

            mViewHolder.txtSemester.setText("Semester  "+dashEntityObjects.getSemester());
        }

//        Finally initializing our adapter
        ArrayList<DashEntityObjects> REDashList = new ArrayList<>();

        for(int i = 0; i< dashEntityObjects.getSubject_details().size(); i++){
            REDashList.add(dashEntityObjects);
        }

        DashCardAdapter adapter = new DashCardAdapter(context,REDashList);
        mViewHolder.recyclerView.setAdapter(adapter);

        return convertView;
    }

    private class MyViewHolder {
        TextView txtCountry,txtUniversity,txtCourse,txtSemester;
        RecyclerView recyclerView;

        private MyViewHolder(View view) {

            //Initializing Views
            txtCountry = (TextView)view.findViewById(R.id.txtCountry);
            txtUniversity = (TextView)view.findViewById(R.id.txtUniversity);
            txtCourse = (TextView)view.findViewById(R.id.txtCourse);
            txtSemester = (TextView)view.findViewById(R.id.txtSemester);
            recyclerView = (RecyclerView)view.findViewById(R.id.dash_recycler_view);

        }
    }
}
