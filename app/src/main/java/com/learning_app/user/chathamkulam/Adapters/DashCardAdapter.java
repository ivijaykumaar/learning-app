package com.learning_app.user.chathamkulam.Adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning_app.user.chathamkulam.Model.AlarmReceiver;
import com.learning_app.user.chathamkulam.Fragments.ModuleList;
import com.learning_app.user.chathamkulam.Model.DashboardModel.DashEntityObjects;
import com.learning_app.user.chathamkulam.Model.MyBounceInterpolator;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Viewer.NSPDFViewer;
import com.learning_app.user.chathamkulam.Viewer.QBPDFViewer;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by User on 5/14/2017.
 */

public class DashCardAdapter extends RecyclerView.Adapter<DashCardAdapter.MyViewHolder> {

    private Context mContext;
    private List<DashEntityObjects> myList;

    private String currentSubName;

    public DashCardAdapter(Context mContext, List<DashEntityObjects> myList) {
        super();

//        Getting all store data
        this.mContext = mContext;
        this.myList = myList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtDuration,txtPrice,txtValid,txtVideoDuration,txtVideo,txtNotes,txtQbank,txtSubjectName;
        ImageView subject_image, iocVideo,iocNotes,iocQuestionBank;

        MyViewHolder(View view) {
            super(view);
            txtDuration = (TextView) view.findViewById(R.id.txtDuration);
            txtPrice = (TextView)view.findViewById(R.id.txtPrice);
            txtValid = (TextView)view.findViewById(R.id.txtValid);
            txtVideoDuration = (TextView)view.findViewById(R.id.txtVideoDuration);
            txtVideo = (TextView)view.findViewById(R.id.txtVideo);
            txtNotes = (TextView)view.findViewById(R.id.txtNotes);
            txtQbank = (TextView)view.findViewById(R.id.txtQbank);
            txtSubjectName = (TextView)view.findViewById(R.id.txtSubjectName);
            txtSubjectName.setVisibility(View.GONE);

            subject_image = (ImageView)view.findViewById(R.id.ime_description);
            iocVideo = (ImageView)view.findViewById(R.id.iocVideo);
            iocNotes = (ImageView)view.findViewById(R.id.iocNotes);
            iocQuestionBank = (ImageView)view.findViewById(R.id.iocQB);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_dash_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DashEntityObjects dashEntityObjects = myList.get(position);

        String course = dashEntityObjects.getCourse();
        String semester = dashEntityObjects.getSemester();
        String subject = dashEntityObjects.getSubject_details().get(position).getSubject_name();
        String freeValidity = dashEntityObjects.getSubject_details().get(position).getFree_validity();
        String paidValidity = dashEntityObjects.getSubject_details().get(position).getPaid_validity();

//        Add Image for card
        try {

            String folderName = dashEntityObjects.getSubject_details().get(position).getSubject_name();
            String fileName = dashEntityObjects.getSubject_details().get(position).getSubject_name()+".jpg";
            String DNAME = "Chathamkulam"+"/"+folderName+"/"+fileName;
            File rootPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), DNAME);

            if (!rootPath.exists()){

                holder.subject_image.setBackgroundResource(android.R.drawable.ic_dialog_alert);
                holder.txtSubjectName.setVisibility(View.VISIBLE);
                holder.txtSubjectName.setText(subject);

            } else {

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                Bitmap mBitmap = BitmapFactory.decodeFile(rootPath.getAbsolutePath(),options);
                holder.subject_image.setImageBitmap(mBitmap);

            }

        }catch (Exception e){
            e.printStackTrace();
            Log.d("Exception on Bitmap",e.getMessage());
        }


        String video = dashEntityObjects.getSubject_details().get(position).getVideo_count();
        if (video.equals("0")){
            holder.iocVideo.setVisibility(View.GONE);
            holder.txtVideo.setVisibility(View.GONE);
        }

        final String notes = dashEntityObjects.getSubject_details().get(position).getNotes_count();
        if (notes.equals("0")){
            holder.iocNotes.setVisibility(View.GONE);
            holder.txtNotes.setVisibility(View.GONE);
        }

        String qBank = dashEntityObjects.getSubject_details().get(position).getQbank_count();
        if (qBank.equals("0")){
            holder.iocQuestionBank.setVisibility(View.GONE);
            holder.txtQbank.setVisibility(View.GONE);
        }

        String videoSize = dashEntityObjects.getSubject_details().get(position).getDuration();

        String[] sizes = videoSize.split(":");
        String hours = sizes[0];
        String minute = sizes[1];
        String seconds = sizes[2];

        if (hours.equals("00")){
            if (!minute.equals("00")){
                holder.txtVideoDuration.setText("("+minute+":"+seconds+" Mins"+")");
            }else{
                holder.txtVideoDuration.setText("("+minute+":"+seconds+" Secs"+")");
            }
        }else{
            holder.txtVideoDuration.setText("("+videoSize+" Hrs"+")");
        }


        if (!freeValidity.equals(null)){

            Intent intent = new Intent(mContext,AlarmReceiver.class);
            intent.putExtra("Key_currentSub",subject);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,1,intent,0);

            Calendar calendar = Calendar.getInstance();

            String[] sep = freeValidity.split("-");
            int year = Integer.parseInt(sep[0]);
            int month = Integer.parseInt(sep[1]);
            int day = Integer.parseInt(sep[2]);

            calendar.set(year,month,day);

            AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

            Log.v("Alarm Result","Alarm set with" +"   "+ calendar.getTimeInMillis());

        } else {

            Log.v("Alarm Result",freeValidity);
        }

        holder.iocVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                holder.iocVideo.startAnimation(myAnim);

                currentSubName = dashEntityObjects.getSubject_details().get(position).getSubject_name();
//                Toast.makeText(mContext,dashData.getSubject(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext,ModuleList.class);
                intent.putExtra("Key_video",currentSubName);
                mContext.startActivity(intent);

            }
        });

        holder.iocNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                holder.iocNotes.startAnimation(myAnim);

                currentSubName = dashEntityObjects.getSubject_details().get(position).getSubject_name();

//                Toast.makeText(mContext,"This is notesViewer",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext,NSPDFViewer.class);
                intent.putExtra("Key_pdf",currentSubName);
                mContext.startActivity(intent);

            }
        });

        holder.iocQuestionBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                holder.iocQuestionBank.startAnimation(myAnim);

                currentSubName = dashEntityObjects.getSubject_details().get(position).getSubject_name();
//                Toast.makeText(mContext,"This is QuestionBank"+"   "+currentSubName,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext,QBPDFViewer.class);
                intent.putExtra("Key_pdf",currentSubName);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }
}
