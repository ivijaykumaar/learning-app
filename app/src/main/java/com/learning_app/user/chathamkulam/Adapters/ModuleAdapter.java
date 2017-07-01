package com.learning_app.user.chathamkulam.Adapters;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.learning_app.user.chathamkulam.Model.ModuleModel.ModuleItems;
import com.learning_app.user.chathamkulam.R;

import java.util.ArrayList;

/**
 * Created by User on 5/25/2017.
 */

public class ModuleAdapter extends BaseAdapter {

    private Activity context;
    private LayoutInflater inflater;
    private ArrayList<ModuleItems> mainArrayList = new ArrayList();

    public ModuleAdapter(Activity context, ArrayList<ModuleItems> mainArrayList) {
        this.context = context;
        this.mainArrayList = mainArrayList;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return mainArrayList.size();
    }

    @Override
    public ModuleItems getItem(int position) {
        return mainArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.module_list_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        ModuleItems moduleItems = getItem(position);

        mViewHolder.txtModuleName.setText(moduleItems.getModule_name());

//        Finally initializing our adapter
        ArrayList<ModuleItems> topicSubList = new ArrayList<>();
        for(int i = 0; i< moduleItems.getTopic_details().size(); i++){
            topicSubList.add(moduleItems);
        }

        ModuleCardAdapter moduleCardAdapter = new ModuleCardAdapter(context,topicSubList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mViewHolder.moduleRecycler.setHasFixedSize(true);
        mViewHolder.moduleRecycler.setLayoutManager(layoutManager);
        mViewHolder.moduleRecycler.setAdapter(moduleCardAdapter);

        return convertView;
    }

    private class MyViewHolder {
        TextView txtModuleName;
        RecyclerView moduleRecycler;

        MyViewHolder(View view) {

            //Initializing Views
            txtModuleName = (TextView)view.findViewById(R.id.txtModuleName);
            moduleRecycler = (RecyclerView) view.findViewById(R.id.module_recycler_view);

        }
    }
}
