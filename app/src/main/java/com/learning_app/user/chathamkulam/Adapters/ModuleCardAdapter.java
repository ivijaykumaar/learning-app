package com.learning_app.user.chathamkulam.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learning_app.user.chathamkulam.Model.ModuleModel.ModuleItems;
import com.learning_app.user.chathamkulam.Viewer.NormalVideoView;
import com.learning_app.user.chathamkulam.R;

import java.util.List;

import static com.learning_app.user.chathamkulam.Fragments.ModuleList.currentSubjectName;

/**
 * Created by User on 5/14/2017.
 */

public class ModuleCardAdapter extends RecyclerView.Adapter<ModuleCardAdapter.MyViewHolder> {

    private Context mContext;
    private List<ModuleItems> myList;

    private int topicPosition;

    public ModuleCardAdapter(Context mContext, List<ModuleItems> myList) {
        super();
        this.mContext = mContext;
        this.myList = myList;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTopic,txtTopicDuration;
        RelativeLayout topicLay;

        MyViewHolder(View view) {
            super(view);
            txtTopic = (TextView) view.findViewById(R.id.txtTopic);
            topicLay = (RelativeLayout)view.findViewById(R.id.TopicLay);
//            txtTopicDuration = (TextView) view.findViewById(R.id.txtTopicDuration);

            topicPosition = getAdapterPosition();

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.module_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final ModuleItems moduleItems = myList.get(position);

        holder.txtTopic.setText(moduleItems.getTopicItems().get(position).getTopicName());
//        holder.txtTopicDuration.setText(moduleItems.getTopicItems().get(position).getTopicDuration());

        holder.topicLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String moduleName = moduleItems.getModuleName();
                String topicName = moduleItems.getTopicItems().get(position).getTopicName();

                Intent sendValue = new Intent(mContext,NormalVideoView.class);
                sendValue.putExtra("Key_position",topicPosition);
                sendValue.putExtra("Key_subName",currentSubjectName);
                sendValue.putExtra("Key_moduleName",moduleName);
                sendValue.putExtra("Key_fileName",topicName);
                sendValue.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(sendValue);

                Log.v("sendValue", moduleName+"  "+topicName);

            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }
}
