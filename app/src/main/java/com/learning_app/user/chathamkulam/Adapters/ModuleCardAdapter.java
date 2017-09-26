package com.learning_app.user.chathamkulam.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

import com.learning_app.user.chathamkulam.Model.BackgroundWork.OnlineModuleView;
import com.learning_app.user.chathamkulam.Model.ModuleModel.ModuleItems;
import com.learning_app.user.chathamkulam.Model.MyBounceInterpolator;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Registration.Registration;
import com.learning_app.user.chathamkulam.Viewer.NormalVideoView;

import java.util.HashMap;
import java.util.List;

import static com.learning_app.user.chathamkulam.Apis.API_ONLINE_VIEW;
import static com.learning_app.user.chathamkulam.Fragments.ModuleList.currentSubjectName;

/**
 * Created by User on 5/14/2017.
 */

public class ModuleCardAdapter extends RecyclerView.Adapter<ModuleCardAdapter.MyViewHolder> {

    private Activity mContext;
    private List<ModuleItems> myList;

    public ModuleCardAdapter(Activity mContext, List<ModuleItems> myList) {
        super();
        this.mContext = mContext;
        this.myList = myList;
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

        if (moduleItems.getTopic_details().get(position).getTotalDuration() != null) {

            holder.txtTopic.setText(moduleItems.getTopic_details().get(position).getTopic_name());
            holder.txtTopicDuration.setText(moduleItems.getTopic_details().get(position).getTopic_duration());

            int totalDuration = Integer.parseInt(moduleItems.getTopic_details().get(position).getTotalDuration());
            int count = Integer.parseInt(moduleItems.getTopic_details().get(position).getCount());

            Log.d("values", totalDuration +" "+ count);

            if (totalDuration != 0) {

                if (count == 1) {

                    holder.videoProgress.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    holder.videoProgress.setProgress(100);

                }

                if (count == 2) {

                    holder.videoProgress.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
                    holder.videoProgress.setProgress(100);

                }

                if (count >= 3) {

                    holder.videoProgress.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                    holder.videoProgress.setProgress(100);

                }

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

                    String moduleName = moduleItems.getModule_name();
                    String topicName = moduleItems.getTopic_details().get(position).getTopic_name();
                    String topicDuration = moduleItems.getTopic_details().get(position).getTopic_duration();
//                    String currentTopicName = topicDuration+"."+topicName+".mp4";

                    String currentTopicName = topicDuration.replaceAll(":", "-") + "-" + topicName + ".mp4";

                    Intent sendValue = new Intent(mContext, NormalVideoView.class);
                    sendValue.putExtra("Key_position", position);
                    sendValue.putExtra("Key_subName", currentSubjectName);
                    sendValue.putExtra("Key_moduleName", moduleName);
                    sendValue.putExtra("Key_fileName", currentTopicName);
                    sendValue.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(sendValue);

                    Log.v("sendValue", topicDuration + "  " + topicName + "   " + position + "   " + moduleName + "  " + currentTopicName);

                }
            });

        } else {

            holder.txtTopic.setText(moduleItems.getTopic_details().get(position).getTopic_name());
            holder.txtTopicDuration.setText(moduleItems.getTopic_details().get(position).getTopic_duration());
            holder.videoProgress.setVisibility(View.GONE);

            holder.topicLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                    Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                    myAnim.setInterpolator(interpolator);

                    holder.iocVideo.startAnimation(myAnim);

                    Registration.deleteCache(mContext);

                    String subjectId = moduleItems.getSubject_id();
                    String semester = moduleItems.getSem_no();
                    String subjectNumber = moduleItems.getSub_no();
                    String module_no = moduleItems.getTopic_details().get(position).getModule_no();
                    String topic_no = moduleItems.getTopic_details().get(position).getTopic_no();

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("subject_id", subjectId);
                    params.put("sem_no", semester);
                    params.put("sub_no", subjectNumber);
                    params.put("module_no", module_no);
                    params.put("topic_no", topic_no);
                    params.put("type", "video");

                    OnlineModuleView async = new OnlineModuleView
                            (API_ONLINE_VIEW, params, mContext, NormalVideoView.class, currentSubjectName);
                    async.execute();

                    Log.v("sendValue", params + "   " + currentSubjectName);

                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return null != myList ? myList.size() : 0;
//        return myList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTopic, txtTopicDuration;
        RelativeLayout topicLay;
        ImageView iocVideo;
        ProgressBar videoProgress;

        MyViewHolder(View view) {
            super(view);
            txtTopic = (TextView) view.findViewById(R.id.txtTopic);
            topicLay = (RelativeLayout) view.findViewById(R.id.topicList);
            txtTopicDuration = (TextView) view.findViewById(R.id.txtTopicDuration);
            iocVideo = (ImageView) view.findViewById(R.id.iocVideo);
            videoProgress = (ProgressBar) view.findViewById(R.id.videoProgress);

        }
    }
}
