package com.learning_app.user.chathamkulam.SearchFilters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.learning_app.user.chathamkulam.Adapters.CustomVolleyImageLoader;
import com.learning_app.user.chathamkulam.Fragments.ModuleList;
import com.learning_app.user.chathamkulam.Model.MyBounceInterpolator;
import com.learning_app.user.chathamkulam.Model.BackgroundWork.OnlineModuleView;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreEntityObjects;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Registration.Registration;
import com.learning_app.user.chathamkulam.Sqlite.CheckingCards;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;
import com.learning_app.user.chathamkulam.Viewer.NSPDFViewer;
import com.learning_app.user.chathamkulam.Viewer.QBPDFViewer;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.learning_app.user.chathamkulam.Model.Constants.DOWNLOAD_URL;
import static com.learning_app.user.chathamkulam.Model.Constants.ONLINE_MODULE_DATA;
import static com.learning_app.user.chathamkulam.Model.Constants.ONLINE_VIEW;
import static com.learning_app.user.chathamkulam.Model.Constants.PHONENUMBER;
import static com.learning_app.user.chathamkulam.Model.Constants.SUBSCRIPTION;
import static com.learning_app.user.chathamkulam.Registration.Registration.deleteCache;

/**
 * Created by User on 5/14/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class StoreCardFilterAdapter extends RecyclerView.Adapter<StoreCardFilterAdapter.MyViewHolder> implements Filterable{

    private Activity mContext;
    private List<StoreEntityObjects> myList;

    private List<StoreEntityObjects> mFilteredList;

    //   Download AsyncUrl variables
    private JSONArray UrlResult = null;
    private String URL_JSON_ARRAY = "result";
    private String current_url = "file";
    private String current_name = "name";
    private ArrayList<String> url_arrayList;
    private ArrayList<String> name_arrayList;
    private ProgressDialog progressDialog;

    //    To store Sqlite Database
    private String country;
    private String university;
    private String course;
    private String semester;
    private String subject;
    private String subjectId;
    private String subjectNumber;
    private String freeValidity;
    private String paidValidity;
    private String duration;
    private String videoCount;
    private String notesCount;
    private String qbankCount;
    private String amount;

    //    Subscription variables
    private static String file_Url;

    private int dlcount;

    public int dl_progress;

    private CheckingCards checkingCards;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

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

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtDuration,txtPrice,txtVideoDuration,txtNotes,txtQbank,txtVideo;
        ImageView IocVideo,IocNotes,IocQbank;
        NetworkImageView imageLoader;
        LinearLayout linearMainLayout;
        ProgressBar storeBarDownloading;

        CheckBox checkBoxSubject;

        MyViewHolder(final View view) {
            super(view);

            txtDuration = (TextView) view.findViewById(R.id.txtDuration);
            txtPrice = (TextView)view.findViewById(R.id.txtPrice);
            linearMainLayout = (LinearLayout)view.findViewById(R.id.storeMainLayout);
            imageLoader = (NetworkImageView)view.findViewById(R.id.imageLoader);
            txtVideoDuration = (TextView)view.findViewById(R.id.txtVideoDuration);
            IocVideo = (ImageView)view.findViewById(R.id.iocVideo);
            IocNotes = (ImageView)view.findViewById(R.id.iocNotes);
            IocQbank = (ImageView)view.findViewById(R.id.iocQB);
            txtNotes = (TextView)view.findViewById(R.id.txtNotes);
            txtQbank = (TextView)view.findViewById(R.id.txtQbank);
            txtVideo = (TextView)view.findViewById(R.id.txtVideo);

            storeBarDownloading = (ProgressBar)view.findViewById(R.id.storeBarDownloading);
            checkBoxSubject = (CheckBox)view.findViewById(R.id.checkSubjects);

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

        imageLoader.get(storeEntityObjects.getFile(),ImageLoader.getImageListener(holder.imageLoader,
                R.mipmap.ic_launcher,android.R.drawable.ic_dialog_alert));

        country = storeEntityObjects.getCountry();
        university = storeEntityObjects.getUniversity();
        course = storeEntityObjects.getCourse();
        semester = "Semester  "+ storeEntityObjects.getSem_no();
        subject = storeEntityObjects.getSubject_name();

        holder.imageLoader.setImageUrl(storeEntityObjects.getFile(), imageLoader);
        String videoSize = storeEntityObjects.getSize();

        String[] sizes = videoSize.split(":");
        String hours = sizes[0];
        String minute = sizes[1];
        final String seconds = sizes[2];

        if (hours.equals("00")){
            if (!minute.equals("00")){
                holder.txtVideoDuration.setText("("+minute+":"+seconds+" Mins"+")");
            } else{
                holder.txtVideoDuration.setText("("+minute+":"+seconds+" Secs"+")");
            }
        }else{
            holder.txtVideoDuration.setText("("+videoSize+" Hrs"+")");
        }

        String video = storeEntityObjects.getVideo_count();
        if (video.equals("0")){
            holder.IocVideo.setVisibility(View.GONE);
            holder.txtVideo.setVisibility(View.GONE);
        }

        String notes = storeEntityObjects.getFile_count();
        if (notes.equals("0")){
            holder.IocNotes.setVisibility(View.GONE);
            holder.txtNotes.setVisibility(View.GONE);
        }

        String qBank = storeEntityObjects.getQa_count();
        if (qBank.equals("0")){
            holder.IocQbank.setVisibility(View.GONE);
            holder.txtQbank.setVisibility(View.GONE);
        }

        checkingCards = new CheckingCards(mContext);

        holder.IocVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Use bounce interpolator with amplitude 0.2 and frequency 20
                Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                holder.IocVideo.startAnimation(myAnim);

                Registration.deleteCache(mContext);
                subject = storeEntityObjects.getSubject_name();
                subjectId = storeEntityObjects.getId();
                subjectNumber = storeEntityObjects.getSub_no();
                semester = storeEntityObjects.getSem_no();

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("subject_id",subjectId);
                params.put("sem_no",semester);
                params.put("sub_no",subjectNumber);

                OnlineModuleView async = new OnlineModuleView(ONLINE_MODULE_DATA,params,mContext,ModuleList.class,subject);
                async.execute();

                Log.d("putValues",semester+subject+subjectId+subjectNumber);

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

                Registration.deleteCache(mContext);
                subject = storeEntityObjects.getSubject_name();
                subjectId = storeEntityObjects.getId();
                subjectNumber = storeEntityObjects.getSub_no();
                semester = storeEntityObjects.getSem_no();

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("subject_id",subjectId);
                params.put("sem_no",semester);
                params.put("sub_no",subjectNumber);
                params.put("type","notes");

                OnlineModuleView async = new OnlineModuleView(ONLINE_VIEW,params,mContext,NSPDFViewer.class,subject);
                async.execute();


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

                Registration.deleteCache(mContext);
                subject = storeEntityObjects.getSubject_name();
                subjectId = storeEntityObjects.getId();
                subjectNumber = storeEntityObjects.getSub_no();
                semester = storeEntityObjects.getSem_no();

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("subject_id",subjectId);
                params.put("sem_no",semester);
                params.put("sub_no",subjectNumber);
                params.put("type","qa");

                OnlineModuleView async = new OnlineModuleView(ONLINE_VIEW,params,mContext,QBPDFViewer.class,subject);
                async.execute();

            }
        });

        checkingCards = new CheckingCards(mContext);
        holder.checkBoxSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                subject = storeEntityObjects.getSubject_name();
                subjectId = storeEntityObjects.getId();
                subjectNumber = storeEntityObjects.getSub_no();
                semester = storeEntityObjects.getSem_no();
                freeValidity = storeEntityObjects.getFree_validity_date();
                paidValidity = storeEntityObjects.getPaid_validity_date();
                duration = storeEntityObjects.getSize();
                videoCount = storeEntityObjects.getVideo_count();
                notesCount = storeEntityObjects.getFile_count();
                qbankCount = storeEntityObjects.getQa_count();
                amount = storeEntityObjects.getAmount();

                if (holder.checkBoxSubject.isChecked()){

                    boolean IsEntry = checkingCards.addCheckData(String.valueOf(position),country,university,course,
                            semester,subject,subjectId,subjectNumber,amount,freeValidity,paidValidity,duration,videoCount,notesCount,qbankCount);
                    if (IsEntry) {

                        Log.d("Check subject","Successfully Added");
                    } else {
                        Log.d("Check subject","Added failed");

                    }

                    Log.d("databaseValue",position+semester+subject+subjectId+subjectNumber+"  "+duration);

                    Cursor cursor = checkingCards.getCheckData();

                    while (cursor.moveToNext()) {

                        String position = cursor.getString(1);
                        String semester = cursor.getString(5);
                        String subject = cursor.getString(6);
                        String subjectId = cursor.getString(7);
                        String subjectNumber = cursor.getString(8);
                        String duration = cursor.getString(9);
                        String videoCount = cursor.getString(10);
                        String notesCount = cursor.getString(11);
                        String qbankCount = cursor.getString(12);

                        Log.d("check data",position+semester+subject+subjectId+subjectNumber+duration
                                +"  "+videoCount+notesCount+qbankCount);
                    }

                } else {

                    checkingCards.removeUnCheckData(subject);

                    Cursor cursor = checkingCards.getCheckData();

                    if (cursor.getCount() != 0){

                        while (cursor.moveToNext()) {

                            String position = cursor.getString(1);
                            String semester = cursor.getString(5);
                            String subject = cursor.getString(6);
                            String subjectId = cursor.getString(7);
                            String subjectNumber = cursor.getString(8);
                            String duration = cursor.getString(9);
                            String videoCount = cursor.getString(10);
                            String notesCount = cursor.getString(11);
                            String qbankCount = cursor.getString(12);

                            Log.d("final data",position+semester+subject+subjectId+subjectNumber+duration+videoCount+notesCount+qbankCount);
                        }

                    } else {
                        Log.d("Cursor result", String.valueOf(cursor.getCount()));
                    }
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
                                dashEntityObjects.getSem_no().toLowerCase().contains(charString)||
                                dashEntityObjects.getUniversity().toLowerCase().contains(charString)||
                                dashEntityObjects.getCourse().toLowerCase().contains(charString)||
                                dashEntityObjects.getSubject_name().toLowerCase().contains(charString)){

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

//    public void DownloadFile(final Activity activity, final String country, final String university, final String course,
//                             final String semester, final String subject, final String subjectId,
//                             final String subjectNumber, final String free_validity, final String paid_validity, final String duration, final String videoCount,
//                             final String notesCount, final String qbankCount){
//
////        Check Android Versions
//        int currentapiVersion = Build.VERSION.SDK_INT;
//        if (currentapiVersion >= 23) {
//
////             Do something for 23 and above versions
//            verifyStoragePermissions(activity);
//        }
//
//        for (int i = 0; i < AsyncUrl.url_arrayList.size(); i++) {
//
//            file_Url = String.valueOf(AsyncUrl.url_arrayList.get(i));
//            final String fileType = file_Url.substring(file_Url.lastIndexOf(".") + 1);
//
//            String file_Name = String.valueOf(AsyncUrl.name_arrayList.get(i));
//
//            if (fileType.equals("mp4")) {
//
//                String[] separated = file_Name.split("_");
//                String fileName = separated[0].trim();
//                String ModuleName = separated[1].trim();
//                String TopicName = separated[2].trim();
//
//                String[] sepTopic = TopicName.split("-");
//                String finalTopicName = sepTopic[0].trim();
//                String finalTopicLength = sepTopic[1].trim();
//
//                String[] sepLength = finalTopicLength.split(":");
//                String hours = sepLength[0].trim();
//                String minutes = sepLength[1].trim();
//                String seconds = sepLength[2].trim();
//
//                String DNAME = "Chathamkulam"+"/"+subject+"/"+ModuleName;
//                File rootPath = new File(Environment.getExternalStorageDirectory().toString(), DNAME.trim());
//                if(!rootPath.exists()) {
//                    rootPath.mkdirs();
//                }
//
//                if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    Log.v("Cannot use storage","Cannot use storage");
//                }
//
//                final File myFinalDir = new File(rootPath,hours+minutes+seconds+"-"+finalTopicName);
//
//                try {
//
//                    final DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//                    Uri uri = Uri.parse(file_Url);
//                    Uri destination = Uri.fromFile(myFinalDir);
//
//                    DownloadManager.Request request = new DownloadManager.Request(uri);
//                    request.setDestinationUri(destination);
//                    request.setAllowedOverRoaming(false);
//                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                    final Long downloadId = downloadManager.enqueue(request);
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            boolean downloading = true;
//
//                            while(downloading){
//
//                                DownloadManager.Query q = new DownloadManager.Query();
//                                q.setFilterById(downloadId);
//
//                                Cursor cursor = downloadManager.query(q);
//                                cursor.moveToFirst();
//
//                                int bytes_downloaded = cursor.getInt(cursor
//                                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//
//                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
//                                        == DownloadManager.STATUS_SUCCESSFUL) {
//                                    downloading = false;
//                                }
//
//                                int status =cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
//
//                                if (status==DownloadManager.STATUS_SUCCESSFUL) {
//                                    Log.d("Download status","done");
//
//                                    AsyncTask.execute(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            //TODO your background code
//
//                                            try {
//                                                FileCrypto.encrypt(myFinalDir, myFinalDir);
//                                                Log.d("fileCrypto","Encrypted");
//                                            }catch (Exception e) {
//                                                e.printStackTrace();
//                                                Log.d("fileCrypto Exception",e.getMessage());
//                                            }
//
//                                        }
//                                    });
//
//                                    break;
//                                }
//
//                                if (status == DownloadManager.STATUS_FAILED || status == DownloadManager.ERROR_UNKNOWN ) {
//                                    Log.d("Download status","failed");
//
//                                    downloadManager.remove(downloadId);
//                                    break;
//                                }
//
//                                dl_progress = (int) ((bytes_downloaded * 100L) / bytes_total);
////                                ProgressBarTask task = new ProgressBarTask(mContext);
////                                task.setProgress(dl_progress);
//
//                                cursor.close();
//                            }
//                        }
//                    }).start();
//
//                    IntentFilter filter = new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//
//                    final int finalI = i;
//                    BroadcastReceiver receiver = new BroadcastReceiver() {
//                        public void onReceive(Context ctxt, Intent intent) {
//                            dlcount++;
//
//                            if(dlcount == AsyncUrl.url_arrayList.size()) {
//
////                                Toast.makeText(mContext,"Completed",Toast.LENGTH_LONG).show();
//                                StoreEntireDetails storeEntireDetails = new StoreEntireDetails(activity);
//
//                                if (!storeEntireDetails.ifExists(subject)){
//
//                                    boolean IsEntry = storeEntireDetails.addData(country,university,course,semester,subject,subjectId,
//                                            subjectNumber,free_validity,paid_validity,duration,videoCount,notesCount,qbankCount);
//                                    if (IsEntry) {
//                                        Toast.makeText(activity, "Successfully Added", Toast.LENGTH_LONG).show();
//
//                                    } else {
//                                        Toast.makeText(activity, "Added failed", Toast.LENGTH_LONG).show();
//                                    }
//                                    Log.d("databaseValue",country+university+course+semester+subject+subjectId+subjectNumber+duration);
//                                    subscription(mContext);
////                                AskQuestion(mContext);
//                                }
//                            }
//                        }
//                    };
//                    mContext.registerReceiver( receiver, filter);
//
//                }catch (Exception e){
//                    Log.d("DManagerException",e.getMessage());
//                }
//            } else {
//                Log.v("Video result", "There is no video file");
//            }
//
//            if (!fileType.equals("mp4")) {
//
//                String DNAME = "Chathamkulam"+"/"+subject;
//                File rootPath = new File(Environment.getExternalStorageDirectory().toString(), DNAME.trim());
//                if(!rootPath.exists()) {
//                    rootPath.mkdirs();
//                }
//                if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    Log.v("Cannot use storage","Cannot use storage");
//                }
//
//                final File myFinalDir = new File(rootPath,file_Name);
//
//                try {
//                    final DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//                    Uri uri = Uri.parse(file_Url);
//                    Uri destination = Uri.fromFile(myFinalDir);
//
//                    DownloadManager.Request request = new DownloadManager.Request(uri);
//                    request.setDestinationUri(destination);
//                    request.setAllowedOverRoaming(false);
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                    final Long downloadId = downloadManager.enqueue(request);
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            boolean downloading = true;
//
//                            while(downloading){
//
//                                DownloadManager.Query q = new DownloadManager.Query();
//                                q.setFilterById(downloadId);
//
//                                Cursor cursor = downloadManager.query(q);
//                                cursor.moveToFirst();
//
//                                int bytes_downloaded = cursor.getInt(cursor
//                                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//
//                                int status =cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
//
//                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
//                                        == DownloadManager.STATUS_SUCCESSFUL) {
//                                    downloading = false;
//                                }
//
//                                if (status==DownloadManager.STATUS_SUCCESSFUL) {
//                                    Log.d("Download status","done");
//
//                                    if (!fileType.equals("jpg")){
//
//                                        AsyncTask.execute(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                //TODO your background code
//
//                                                try {
//                                                    FileCrypto.encrypt(myFinalDir, myFinalDir);
//                                                    Log.d("fileCrypto","Encrypted");
//                                                } catch (Exception e) {
//                                                    e.printStackTrace();
//                                                    Log.d("fileCrypto Exception",e.getMessage());
//                                                }
//                                            }
//                                        });
//                                    }
//                                    break;
//                                }
//                                if ( status == DownloadManager.STATUS_FAILED || status == DownloadManager.ERROR_UNKNOWN) {
//                                    Log.d("Download status","failed");
//                                    downloadManager.remove(downloadId);
//                                    break;
//                                }
//
//                                dl_progress = (int) ((bytes_downloaded * 100L) / bytes_total);
////                                ProgressBarTask task = new ProgressBarTask(mContext);
////                                task.setProgress(dl_progress);
//
//                                cursor.close();
//                            }
//                        }
//                    }).start();
//
//
//                    IntentFilter filter = new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//
//                    final int finalI1 = i;
//                    BroadcastReceiver receiver =new BroadcastReceiver() {
//                        public void onReceive(Context ctxt, Intent intent) {
//
//                            Log.v("Download Others","Download Complete");
//
//                            dlcount++;
//                            if(dlcount == AsyncUrl.url_arrayList.size()) {
//
////                                Toast.makeText(mContext,"Completed",Toast.LENGTH_LONG).show();
//                                StoreEntireDetails storeEntireDetails = new StoreEntireDetails(activity);
//
//                                if (!storeEntireDetails.ifExists(subject)){
//
//                                    boolean IsEntry = storeEntireDetails.addData(country,university,course,semester,subject,subjectId,
//                                            subjectNumber,free_validity,paid_validity,duration,videoCount,notesCount,qbankCount);
//                                    if (IsEntry) {
//                                        Toast.makeText(activity, "Successfully Added", Toast.LENGTH_LONG).show();
//
//                                    } else {
//                                        Toast.makeText(activity, "Added failed", Toast.LENGTH_LONG).show();
//                                    }
//                                    Log.d("databaseValue",country+university+course+semester+subject+subjectId+subjectNumber+duration);
//                                    subscription(mContext);
////                                AskQuestion(mContext);
//                                }
//                            }
//                        }
//                    };
//                    mContext.registerReceiver( receiver, filter);
//
//                }catch (Exception e){
//                    Log.d("DownloadManager",e.getMessage());
//                }
//                Log.d("File path", myFinalDir.toString());
//            }
//        }
//    }

    public static void subscription(final Context myContext){

        deleteCache(myContext);
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);

        final ProgressDialog loading = ProgressDialog.show(myContext, "Authenticating","Please wait while we check subscription details", false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,SUBSCRIPTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.v("Subscription Response",response);
                loading.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.v("Volley Error",error.getMessage()+" ");
                loading.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> Hashmap = new HashMap<String, String>();

                RegisterMember registerMember = RegisterMember.getInstance(myContext);;
                Cursor cursorResult = registerMember.getDetails();

                while (cursorResult.moveToNext()) {
                    String MobileNumber= cursorResult.getString(3);

                    if (MobileNumber != null && file_Url != null) {
                        Hashmap.put(PHONENUMBER, MobileNumber);
                        Hashmap.put(DOWNLOAD_URL, file_Url);

                        Log.v("Number Filed",MobileNumber);
                        Log.v("Url Filed",file_Url);

                    }else {

                        Log.v("Number Filed",null);
                    }
                }
                return Hashmap;
            }
        };
        requestQueue.add(stringRequest);
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
}
