package com.learning_app.user.chathamkulam.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning_app.user.chathamkulam.Viewer.NSPDFViewer;
import com.learning_app.user.chathamkulam.Viewer.QBPDFViewer;
import com.learning_app.user.chathamkulam.Model.DashboardModel.DashEntityObjects;
import com.learning_app.user.chathamkulam.Fragments.ModuleList;
import com.learning_app.user.chathamkulam.R;

import java.io.File;
import java.util.List;

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
        TextView txtDuration,txtPrice,txtValid;
        ImageView subject_image,video,pdf,questionBank;

        MyViewHolder(View view) {
            super(view);
            txtDuration = (TextView) view.findViewById(R.id.txtDuration);
            txtPrice = (TextView)view.findViewById(R.id.txtPrice);
            txtValid = (TextView)view.findViewById(R.id.txtValid);

            subject_image = (ImageView)view.findViewById(R.id.ime_description);
            video = (ImageView)view.findViewById(R.id.imeVideo);
            pdf = (ImageView)view.findViewById(R.id.imePdf);
            questionBank = (ImageView)view.findViewById(R.id.imeQB);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_dash_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DashEntityObjects dashEntityObjects = myList.get(position);

        String course = dashEntityObjects.getCourse();
        String semester = dashEntityObjects.getSemester();
        String subject = dashEntityObjects.getSubject_details().get(position).getSubject_name();

//        Add Image for card
        try {

            String folderName = dashEntityObjects.getSubject_details().get(position).getSubject_name();
            String fileName = dashEntityObjects.getSubject_details().get(position).getSubject_name()+".jpg";
            String DNAME = "Chathamkulam"+"/"+folderName+"/"+fileName;

            File rootPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), DNAME);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            Bitmap mBitmap = BitmapFactory.decodeFile(rootPath.getAbsolutePath(),options);
            holder.subject_image.setImageBitmap(mBitmap);

        }catch (Exception e){
            e.printStackTrace();
            Log.d("Exception on Bitmap",e.getMessage());
        }

        holder.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentSubName = dashEntityObjects.getSubject_details().get(position).getSubject_name();
//                Toast.makeText(mContext,dashData.getSubject(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext,ModuleList.class);
                intent.putExtra("Key_video",currentSubName);
                mContext.startActivity(intent);

            }
        });

        holder.pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentSubName = dashEntityObjects.getSubject_details().get(position).getSubject_name();

//                Toast.makeText(mContext,"This is pdf",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext,NSPDFViewer.class);
                intent.putExtra("Key_pdf",currentSubName);
                mContext.startActivity(intent);

            }
        });

        holder.questionBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
