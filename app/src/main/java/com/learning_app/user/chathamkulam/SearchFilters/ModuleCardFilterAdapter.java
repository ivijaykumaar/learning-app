package com.learning_app.user.chathamkulam.SearchFilters;

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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learning_app.user.chathamkulam.Model.ModuleModel.ModuleItems;
import com.learning_app.user.chathamkulam.Model.MyBounceInterpolator;
import com.learning_app.user.chathamkulam.Model.OnlineModuleView;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Registration.Registration;
import com.learning_app.user.chathamkulam.Viewer.NormalVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.learning_app.user.chathamkulam.Fragments.ModuleList.currentSubjectName;
import static com.learning_app.user.chathamkulam.Model.Constants.ONLINE_VIEW;

/**
 * Created by User on 5/14/2017.
 */

public class ModuleCardFilterAdapter extends RecyclerView.Adapter<ModuleCardFilterAdapter.MyViewHolder> implements Filterable{

    private Activity mContext;
    private List<ModuleItems> myList;
    private List<ModuleItems> mFilteredList;

    ModuleItems moduleItems;

    public ModuleCardFilterAdapter(Activity mContext, List<ModuleItems> myList) {
        super();
        this.mContext = mContext;
        this.myList = myList;
        mFilteredList = myList;
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

        moduleItems = mFilteredList.get(position);

        if (moduleItems.getTotalDuration() != null){

            holder.txtTopic.setText(moduleItems.getTopic_name());
            holder.txtTopicDuration.setText(moduleItems.getTopic_duration());

            int totalDuration = Integer.parseInt(moduleItems.getTotalDuration());
            int pauseDuration = Integer.parseInt(moduleItems.getPauseDuration());

            if (totalDuration != 0 && pauseDuration != 0){
                int percent = ((int) pauseDuration * 100) / totalDuration;

                holder.videoProgress.setProgress(percent);

                if (percent >= 70){

                    holder.videoProgress.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);

                }

                if (percent <= 30){

                    holder.videoProgress.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

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
                    String topicName = moduleItems.getTopic_name();
                    String topicDuration = moduleItems.getTopic_duration();
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

        } else {

            holder.txtTopic.setText(moduleItems.getTopic_name());
            holder.txtTopicDuration.setText(moduleItems.getTopic_duration());
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
                    String module_no = moduleItems.getModule_no();
                    String topic_no = moduleItems.getTopic_no();

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("subject_id",subjectId);
                    params.put("sem_no",semester);
                    params.put("sub_no",subjectNumber);
                    params.put("module_no",module_no);
                    params.put("topic_no",topic_no);
                    params.put("type","video");

                    OnlineModuleView async = new OnlineModuleView
                            (ONLINE_VIEW,params,mContext,NormalVideoView.class,currentSubjectName);
                    async.execute();


//                    Intent sendValue = new Intent(mContext,NormalVideoView.class);
//                    sendValue.putExtra("Key_onlineSubName",currentSubjectName);
//                    sendValue.putExtra("Key_url","url");
//                    sendValue.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(sendValue);

                    Log.v("sendValue",position +"   "+currentSubjectName);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = myList;
                } else {

                    ArrayList<ModuleItems> filteredList = new ArrayList<>();

                    for (ModuleItems moduleItems : myList) {

                        if (moduleItems.getModule_name().toLowerCase().contains(charString) ||
                                moduleItems.getTopic_name().toLowerCase().contains(charString)||
                                moduleItems.getTopic_duration().toLowerCase().contains(charString)) {

                            filteredList.add(moduleItems);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<ModuleItems>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
