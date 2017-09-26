package com.learning_app.user.chathamkulam.SearchFilters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.learning_app.user.chathamkulam.Adapters.CustomVolleyImageLoader;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.learning_app.user.chathamkulam.Apis.API_ONLINE_MODULE_DATA;
import static com.learning_app.user.chathamkulam.Apis.API_ONLINE_VIEW;
import static com.learning_app.user.chathamkulam.Model.BackgroundWork.AlarmReceiver.deleteRecursive;

/**
 * Created by User on 5/14/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class StoreCardFilterAdapter extends RecyclerView.Adapter<StoreCardFilterAdapter.MyViewHolder> implements Filterable {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    private Activity mContext;
    private List<StoreEntityObjects> myList;
    private List<StoreEntityObjects> mFilteredList;
    //    To store Sqlite Database
    private String country, university, course, semester, subject, sub_cost, trial, duration, qbankCount, notesCount, videoCount, zipUrl;
    private String subjectId, subjectNumber;
    private CheckingCards checkingCards;

    public StoreCardFilterAdapter(Activity mContext) {
        this.mContext = mContext;
    }

    public StoreCardFilterAdapter(Activity mContext, List<StoreEntityObjects> myList) {

//        Getting all store data
        this.mContext = mContext;
        this.myList = myList;
        mFilteredList = myList;
        notifyDataSetChanged();
    }

    private static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_store_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final StoreEntityObjects storeEntityObjects = mFilteredList.get(position);
        ImageLoader imageLoader = CustomVolleyImageLoader.getInstance(mContext).getImageLoader();

        imageLoader.get(storeEntityObjects.getFile(), ImageLoader.getImageListener(holder.imageLoader,
                R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));

        country = storeEntityObjects.getCountry();
        university = storeEntityObjects.getUniversity();
        course = storeEntityObjects.getCourse();
        semester = "Semester  " + storeEntityObjects.getSem_no();
        subject = storeEntityObjects.getSubject_name();

        holder.txtTrail.setText(storeEntityObjects.getTrial());
        holder.txtPrice.setText(storeEntityObjects.getSub_cost());

        holder.imageLoader.setImageUrl(storeEntityObjects.getFile(), imageLoader);
        String videoSize = storeEntityObjects.getSize();

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

        String video = storeEntityObjects.getVideo_count();
        if (video.equals("0")) {
            holder.IocVideo.setVisibility(View.GONE);
            holder.txtVideo.setVisibility(View.GONE);
        }

        String notes = storeEntityObjects.getFile_count();
        if (notes.equals("0")) {
            holder.IocNotes.setVisibility(View.GONE);
            holder.txtNotes.setVisibility(View.GONE);
        }

        String qBank = storeEntityObjects.getQa_count();
        if (qBank.equals("0")) {
            holder.IocQbank.setVisibility(View.GONE);
            holder.txtQbank.setVisibility(View.GONE);
        }

        checkingCards = CheckingCards.getInstance(mContext);

        StoreEntireDetails databaseHelperStore = StoreEntireDetails.getInstance(mContext);
        Cursor Result = databaseHelperStore.getSubjectFromTable();

        if (Result.getCount() != 0) {

            if (databaseHelperStore.ifExists(subject)) {

//                holder.IocDots.setVisibility(View.VISIBLE);
                Cursor cursor = databaseHelperStore.getSubjectRow(subject);
                if (cursor.getCount() != 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            final String progress = cursor.getString(16);
                            final String status = cursor.getString(17);

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

                                File mydir = mContext.getDir("Chathamkulam", Context.MODE_PRIVATE);
                                final File rootPath = new File(mydir, subject + ".zip"); //Getting a file within the dir.

                                if (rootPath.exists()) {
                                    deleteRecursive(rootPath);
                                    Log.d("cancelReport", "fileSuccessfully Removed");
                                }

                            }

                            holder.storeBarDownloading.setProgress(Integer.parseInt(progress));

                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                }
            }
        }
        Result.close();

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
                                    subject = storeEntityObjects.getSubject_name();
                                    subjectId = storeEntityObjects.getId();
                                    subjectNumber = storeEntityObjects.getSub_no();
                                    semester = storeEntityObjects.getSem_no();

                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("subject_id", subjectId);
                                    params.put("sem_no", semester);
                                    params.put("sub_no", subjectNumber);

                                    OnlineModuleView async = new OnlineModuleView(API_ONLINE_MODULE_DATA, params, mContext, ModuleList.class, subject);
                                    async.execute();

                                    Log.d("putValues", semester + subject + subjectId + subjectNumber);

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
                                    subject = storeEntityObjects.getSubject_name();
                                    subjectId = storeEntityObjects.getId();
                                    subjectNumber = storeEntityObjects.getSub_no();
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
                                    subject = storeEntityObjects.getSubject_name();
                                    subjectId = storeEntityObjects.getId();
                                    subjectNumber = storeEntityObjects.getSub_no();
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
                    if (IsEntry) {

                        Log.d("Check subject", "Successfully Added");
                    } else {
                        Log.d("Check subject", "Added failed");

                    }

                } else {

                    checkingCards.removeUnCheckData(subject);
                }
            }
        });
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

                    ArrayList<StoreEntityObjects> filteredList = new ArrayList<>();

                    for (StoreEntityObjects dashEntityObjects : myList) {

                        if (dashEntityObjects.getCountry().toLowerCase().contains(charString) ||
                                dashEntityObjects.getSem_no().toLowerCase().contains(charString) ||
                                dashEntityObjects.getUniversity().toLowerCase().contains(charString) ||
                                dashEntityObjects.getCourse().toLowerCase().contains(charString) ||
                                dashEntityObjects.getSubject_name().toLowerCase().contains(charString)) {

                            filteredList.add(dashEntityObjects);
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
                mFilteredList = (ArrayList<StoreEntityObjects>) filterResults.values;
                notifyDataSetChanged();
            }
        };
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
