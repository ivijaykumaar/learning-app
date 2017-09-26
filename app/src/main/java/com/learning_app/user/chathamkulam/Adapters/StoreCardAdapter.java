package com.learning_app.user.chathamkulam.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.learning_app.user.chathamkulam.Fragments.ModuleList;
import com.learning_app.user.chathamkulam.Model.BackgroundWork.OnlineModuleView;
import com.learning_app.user.chathamkulam.Model.MyBounceInterpolator;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreEntityObjects;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Registration.Registration;
import com.learning_app.user.chathamkulam.Sqlite.CheckingCards;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;
import com.learning_app.user.chathamkulam.Viewer.NSPDFViewer;
import com.learning_app.user.chathamkulam.Viewer.QBPDFViewer;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.learning_app.user.chathamkulam.Apis.API_ONLINE_MODULE_DATA;
import static com.learning_app.user.chathamkulam.Apis.API_ONLINE_VIEW;
import static com.learning_app.user.chathamkulam.Model.BackgroundWork.AlarmReceiver.deleteRecursive;

/**
 * Created by User on 5/14/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class StoreCardAdapter extends RecyclerView.Adapter<StoreCardAdapter.MyViewHolder> {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    private Activity mContext;
    private List<StoreEntityObjects> myList;
    //    To store Sqlite Database
    private String country, university, course, semester, subject, sub_cost, trial, duration, qbankCount, notesCount, videoCount, zipUrl;
    private String subjectId, subjectNumber;
    private StoreEntityObjects storeEntityObjects;
    private CheckingCards checkingCards;

    public StoreCardAdapter(Activity mContext) {
        this.mContext = mContext;
    }

    public StoreCardAdapter(Activity mContext, List<StoreEntityObjects> entityObjectsList) {

//        Getting all store data
        this.mContext = mContext;
        this.myList = entityObjectsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_store_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        storeEntityObjects = myList.get(position);
        ImageLoader imageLoader = CustomVolleyImageLoader.getInstance(mContext).getImageLoader();

        imageLoader.get(storeEntityObjects.getSubject_details().get(position).getFile(), ImageLoader.getImageListener(holder.imageLoader,
                R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));

        country = storeEntityObjects.getCountry();
        university = storeEntityObjects.getUniversity();
        course = storeEntityObjects.getCourse();
        semester = "Semester  " + storeEntityObjects.getSem_no();
        subject = storeEntityObjects.getSubject_details().get(position).getSubject_name();

        holder.txtTrail.setText(storeEntityObjects.getSubject_details().get(position).getTrial());
        holder.txtPrice.setText(storeEntityObjects.getSubject_details().get(position).getSub_cost());

        holder.imageLoader.setImageUrl(storeEntityObjects.getSubject_details().get(position).getFile(), imageLoader);
        String videoSize = storeEntityObjects.getSubject_details().get(position).getSize();

        String[] sizes = videoSize.split(":");
        String hours = sizes[0];
        String minute = sizes[1];
        final String seconds = sizes[2];

        if (hours.equals("00")) {
            if (!minute.equals("00")) {
                holder.txtVideoDuration.setText("(" + minute + ":" + seconds + " Mins" + ")");
            } else {
                holder.txtVideoDuration.setText("(" + minute + ":" + seconds + " Secs" + ")");
            }
        } else {
            holder.txtVideoDuration.setText("(" + videoSize + " Hrs" + ")");
        }

        String video = storeEntityObjects.getSubject_details().get(position).getVideo_count();
        if (video.equals("0")) {
            holder.IocVideo.setVisibility(View.GONE);
            holder.txtVideo.setVisibility(View.GONE);
        }

        String notes = storeEntityObjects.getSubject_details().get(position).getFile_count();
        if (notes.equals("0")) {
            holder.IocNotes.setVisibility(View.GONE);
            holder.txtNotes.setVisibility(View.GONE);
        }

        String qBank = storeEntityObjects.getSubject_details().get(position).getQa_count();
        if (qBank.equals("0")) {
            holder.IocQbank.setVisibility(View.GONE);
            holder.txtQbank.setVisibility(View.GONE);
        }


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                StoreEntireDetails databaseHelperStore = StoreEntireDetails.getInstance(mContext);
                Cursor Result = databaseHelperStore.getSubjectFromTable();

                if (Result.getCount() != 0) {

                    if (databaseHelperStore.ifExists(subject)) {

                        handler.post(new Runnable() {
                            public void run() {
//                                holder.IocDots.setVisibility(View.VISIBLE);
                            }
                        });

                        final Cursor cursor = databaseHelperStore.getSubjectRow(subject);
                        if (cursor.getCount() != 0) {
                            if (cursor.moveToFirst()) {
                                do {
                                    final String progress = cursor.getString(16);
                                    final String status = cursor.getString(17);

                                    handler.post(new Runnable() {
                                        public void run() {

                                            if (status != null) {

                                                if (status.equals("onCompleted") && progress.equals("100")) {
                                                    holder.txtDownloadStatus.setText("Downloaded");
                                                }

                                                if (status.equals("onFailed")) {
                                                    holder.txtDownloadStatus.setText("Failed");
                                                }

                                                if (status.equals("onDownloadPaused")) {
                                                    holder.txtDownloadStatus.setText("Paused");
                                                }

                                                if (status.equals("onDownloadCanceled")) {
                                                    holder.storeBarDownloading.setProgress(0);

                                                    Thread thread1 = new Thread() {
                                                        public void run() {

                                                            File mydir = mContext.getDir("Chathamkulam", Context.MODE_PRIVATE);
                                                            final File rootPath = new File(mydir, subject + ".zip"); //Getting a file within the dir.

                                                            if (rootPath.exists()) {
                                                                deleteRecursive(rootPath);
                                                                Log.d("cancelReport", "fileSuccessfully Removed");
                                                            }
                                                        }
                                                    };
                                                    thread1.start();

                                                }
                                                holder.storeBarDownloading.setProgress(Integer.parseInt(progress));
                                            }
                                        }
                                    });


                                } while (cursor.moveToNext());
                                cursor.close();
                            }
                        }
                    }
                }
                Result.close();
            }
        };
        new Thread(runnable).start();

        holder.IocVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                holder.IocVideo.startAnimation(myAnim);

                RegisterMember registerMember = RegisterMember.getInstance(mContext);
                final Cursor cursorResult = registerMember.getDetails();

                AlertDialog.Builder alertNotes = new AlertDialog.Builder(mContext);

                while (cursorResult.moveToNext()) {
                    String userName = cursorResult.getString(1);

                    alertNotes.setTitle("Dear  " + userName);
                    alertNotes.setMessage(R.string.viewMessage);
                    alertNotes.setIcon(R.drawable.ic_question);
                    alertNotes.setPositiveButton("Continue",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Registration.deleteCache(mContext);
                                    subject = storeEntityObjects.getSubject_details().get(position).getSubject_name();
                                    subjectId = storeEntityObjects.getId();
                                    subjectNumber = storeEntityObjects.getSubject_details().get(position).getSub_no();
                                    semester = storeEntityObjects.getSem_no();

                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("subject_id", subjectId);
                                    params.put("sem_no", semester);
                                    params.put("sub_no", subjectNumber);

                                    OnlineModuleView async = new OnlineModuleView(API_ONLINE_MODULE_DATA, params, mContext, ModuleList.class, subject);
                                    async.execute();

                                }
                            });

                    alertNotes.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();

                                }
                            });
                    alertNotes.show();
                }
            }
        });

        holder.IocNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                holder.IocNotes.startAnimation(myAnim);

                RegisterMember registerMember = RegisterMember.getInstance(mContext);
                final Cursor cursorResult = registerMember.getDetails();

                AlertDialog.Builder alertNotes = new AlertDialog.Builder(mContext);

                while (cursorResult.moveToNext()) {
                    String userName = cursorResult.getString(1);

                    alertNotes.setTitle("Dear  " + userName);
                    alertNotes.setMessage(R.string.viewMessage);
                    alertNotes.setIcon(R.drawable.ic_question);
                    alertNotes.setPositiveButton("Continue",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Registration.deleteCache(mContext);
                                    subject = storeEntityObjects.getSubject_details().get(position).getSubject_name();
                                    subjectId = storeEntityObjects.getId();
                                    subjectNumber = storeEntityObjects.getSubject_details().get(position).getSub_no();
                                    semester = storeEntityObjects.getSem_no();

                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("subject_id", subjectId);
                                    params.put("sem_no", semester);
                                    params.put("sub_no", subjectNumber);
                                    params.put("type", "notes");

                                    OnlineModuleView async = new OnlineModuleView(API_ONLINE_VIEW, params, mContext, NSPDFViewer.class, subject);
                                    async.execute();
                                }
                            });

                    alertNotes.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();

                                }
                            });
                    alertNotes.show();
                }
            }
        });

        holder.IocQbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                holder.IocQbank.startAnimation(myAnim);

                RegisterMember registerMember = RegisterMember.getInstance(mContext);
                final Cursor cursorResult = registerMember.getDetails();

                AlertDialog.Builder alertNotes = new AlertDialog.Builder(mContext);

                while (cursorResult.moveToNext()) {
                    String userName = cursorResult.getString(1);

                    alertNotes.setTitle("Dear  " + userName);
                    alertNotes.setMessage(R.string.viewMessage);
                    alertNotes.setIcon(R.drawable.ic_question);
                    alertNotes.setPositiveButton("Continue",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Registration.deleteCache(mContext);
                                    subject = storeEntityObjects.getSubject_details().get(position).getSubject_name();
                                    subjectId = storeEntityObjects.getId();
                                    subjectNumber = storeEntityObjects.getSubject_details().get(position).getSub_no();
                                    semester = storeEntityObjects.getSem_no();

                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("subject_id", subjectId);
                                    params.put("sem_no", semester);
                                    params.put("sub_no", subjectNumber);
                                    params.put("type", "qa");

                                    OnlineModuleView async = new OnlineModuleView(API_ONLINE_VIEW, params, mContext, QBPDFViewer.class, subject);
                                    async.execute();

                                }
                            });

                    alertNotes.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();

                                }
                            });
                    alertNotes.show();
                }
            }
        });

        checkingCards = CheckingCards.getInstance(mContext);
        holder.checkBoxSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread() {
                    public void run() {

                        semester = storeEntityObjects.getSem_no();
                        subjectId = storeEntityObjects.getId();
                        subjectNumber = storeEntityObjects.getSubject_details().get(position).getSub_no();
                        subject = storeEntityObjects.getSubject_details().get(position).getSubject_name();
                        sub_cost = storeEntityObjects.getSubject_details().get(position).getSub_cost();
                        trial = storeEntityObjects.getSubject_details().get(position).getTrial();
                        duration = storeEntityObjects.getSubject_details().get(position).getSize();
                        notesCount = storeEntityObjects.getSubject_details().get(position).getFile_count();
                        qbankCount = storeEntityObjects.getSubject_details().get(position).getQa_count();
                        videoCount = storeEntityObjects.getSubject_details().get(position).getVideo_count();
                        zipUrl = storeEntityObjects.getSubject_details().get(position).getUrl();

                        if (holder.checkBoxSubject.isChecked()) {


                            boolean IsEntry = checkingCards.addCheckData(String.valueOf(position), country, university, course,
                                    semester, subjectId, subjectNumber, subject, sub_cost, trial, duration, notesCount, qbankCount, videoCount, zipUrl);
                            checkingCards.close();
                            if (IsEntry) {

                                Log.d("Check subject", "Successfully Added");
                            } else {
                                Log.d("Check subject", "Added failed");

                            }

                        } else {

                            checkingCards.removeUnCheckData(subject);
                        }
                    }
                };
                thread.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTrail, txtPrice, txtVideoDuration, txtNotes, txtQbank, txtVideo, txtDownloadStatus;
        ImageView IocVideo, IocNotes, IocQbank;
        NetworkImageView imageLoader;
        LinearLayout linearMainLayout;
        ProgressBar storeBarDownloading;

        CheckBox checkBoxSubject;

        MyViewHolder(final View view) {
            super(view);

            txtTrail = (TextView) view.findViewById(R.id.txtTrail);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            linearMainLayout = (LinearLayout) view.findViewById(R.id.storeMainLayout);
            imageLoader = (NetworkImageView) view.findViewById(R.id.imageLoader);

            txtVideoDuration = (TextView) view.findViewById(R.id.txtVideoDuration);
            IocVideo = (ImageView) view.findViewById(R.id.iocVideo);
            IocNotes = (ImageView) view.findViewById(R.id.iocNotes);
            IocQbank = (ImageView) view.findViewById(R.id.iocQB);
            txtNotes = (TextView) view.findViewById(R.id.txtNotes);
            txtQbank = (TextView) view.findViewById(R.id.txtQbank);
            txtVideo = (TextView) view.findViewById(R.id.txtVideo);

            storeBarDownloading = (ProgressBar) view.findViewById(R.id.storeBarDownloading);
            checkBoxSubject = (CheckBox) view.findViewById(R.id.checkSubjects);
            txtDownloadStatus = (TextView) view.findViewById(R.id.txtDownloadStatus);

        }
    }
}
