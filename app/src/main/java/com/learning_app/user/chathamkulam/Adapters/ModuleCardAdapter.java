package com.learning_app.user.chathamkulam.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learning_app.user.chathamkulam.Model.ModuleModel.ModuleItems;
import com.learning_app.user.chathamkulam.MyBounceInterpolator;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Viewer.NormalVideoView;

import java.util.List;

import static com.learning_app.user.chathamkulam.Fragments.ModuleList.currentSubjectName;

/**
 * Created by User on 5/14/2017.
 */

public class ModuleCardAdapter extends RecyclerView.Adapter<ModuleCardAdapter.MyViewHolder> {

    private Context mContext;
    private List<ModuleItems> myList;

    public ModuleCardAdapter(Context mContext, List<ModuleItems> myList) {
        super();
        this.mContext = mContext;
        this.myList = myList;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTopic,txtTopicDuration;
        RelativeLayout topicLay;
        ImageView iocVideo;
        ProgressBar videoProgress;

        MyViewHolder(View view) {
            super(view);
            txtTopic = (TextView) view.findViewById(R.id.txtTopic);
            topicLay = (RelativeLayout)view.findViewById(R.id.topicList);
            txtTopicDuration = (TextView) view.findViewById(R.id.txtTopicDuration);
            iocVideo = (ImageView) view.findViewById(R.id.iocVideo);
            videoProgress = (ProgressBar)view.findViewById(R.id.videoProgress);

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
        holder.txtTopicDuration.setText(moduleItems.getTopicItems().get(position).getTopicDuration());

        int totalDuration = Integer.parseInt(moduleItems.getTopicItems().get(position).getTotalDuration());
        int pauseDuration = Integer.parseInt(moduleItems.getTopicItems().get(position).getPauseDuration());

        if (totalDuration != 0 && pauseDuration != 0){

            holder.videoProgress.setProgress(((int) pauseDuration * 100) / totalDuration );
        } else {

            holder.videoProgress.setProgress(0);
        }

        holder.topicLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);

                holder.iocVideo.startAnimation(myAnim);

                String moduleName = moduleItems.getModuleName();
                String topicName = moduleItems.getTopicItems().get(position).getTopicName();
                String topicDuration = moduleItems.getTopicItems().get(position).getTopicDuration();
                String currentTopicName = topicDuration.replaceAll(":","")+"-"+topicName;

                Intent sendValue = new Intent(mContext,NormalVideoView.class);
                sendValue.putExtra("Key_position",position);
                sendValue.putExtra("Key_subName",currentSubjectName);
                sendValue.putExtra("Key_moduleName",moduleName);
                sendValue.putExtra("Key_fileName",currentTopicName);
                sendValue.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(sendValue);

//                VideoHandler videoHandler = VideoHandler.getInstance(mContext);
//                videoHandler.DeleteAll();

                Log.v("sendValue",position +"   "+moduleName+"  "+currentTopicName);

            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }
}
