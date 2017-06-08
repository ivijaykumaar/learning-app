package com.learning_app.user.chathamkulam;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.learning_app.user.chathamkulam.Adapters.CustomVolleyImageLoader;
import com.learning_app.user.chathamkulam.Fragments.Drawer;
import com.learning_app.user.chathamkulam.Model.AsyncUrl;
import com.learning_app.user.chathamkulam.Model.StoreModel.StoreFilterItems;
import com.learning_app.user.chathamkulam.PaymentGateway.PaymentGateWay;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.learning_app.user.chathamkulam.AlarmReceiver.deleteRecursive;
import static com.learning_app.user.chathamkulam.Model.Constants.DOWNLOAD_URL;
import static com.learning_app.user.chathamkulam.Model.Constants.GET_URLS;
import static com.learning_app.user.chathamkulam.Model.Constants.PHONENUMBER;
import static com.learning_app.user.chathamkulam.Model.Constants.SUBSCRIPTION;
import static com.learning_app.user.chathamkulam.Registration.Registration.deleteCache;

/**
 * Created by User on 6/7/2017.
 */

public class StoreFilterAdapter extends RecyclerView.Adapter<StoreFilterAdapter.MyViewHolder>  {

    private Context mContext;
    private List<StoreFilterItems> myList;

    //   Download AsyncUrl variables
    private JSONArray UrlResult = null;
    private String URL_JSON_ARRAY = "result";
    private String current_url = "file";
    private String current_name = "name";
    private ArrayList<String> url_arrayList;
    private ArrayList<String> name_arrayList;
    private ProgressDialog progressDialog;

    //    File variables
    private File rootPath;

    //    To store Sqlite Database
    private String country;
    private String university;
    private String course;
    private String semester;
    private String subject;
    private String subjectId;
    private String subjectNumber;

    //    Subscription variables
    private static String file_Url;

    private StoreFilterItems storeFilterItems;

    private int dlcount;

    public StoreFilterAdapter(Context mContext, List<StoreFilterItems> storeFilterItemses) {

//        Getting all store data
        this.mContext = mContext;
        this.myList = storeFilterItemses;
    }

    @Override
    public StoreFilterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_store_card, parent, false);
        return new StoreFilterAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StoreFilterAdapter.MyViewHolder holder, final int position) {

        storeFilterItems = myList.get(position);
        ImageLoader imageLoader = CustomVolleyImageLoader.getInstance(mContext).getImageLoader();

        imageLoader.get(storeFilterItems.getFile(),ImageLoader.getImageListener(holder.imageLoader,
                R.mipmap.ic_launcher,android.R.drawable.ic_dialog_alert));

        semester = "Semester  "+ storeFilterItems.getSemester();
        subject = storeFilterItems.getSubject_name();

        holder.imageLoader.setImageUrl(storeFilterItems.getFile(), imageLoader);

        holder.txtDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                subject = storeFilterItems.getSubject_name();
                subjectId = storeFilterItems.getSubject_id();
                subjectNumber = storeFilterItems.getSubject_number();
                semester = storeFilterItems.getSemester();

//                put values for download
                HashMap<String,String> params = new HashMap<String, String>();
                params.put("sem_no",semester);
                params.put("id",subjectId);
                params.put("sub_no",subjectNumber);
                params.put("type","url");

                Log.v("Subject Values",subject+subjectId+subjectNumber+semester);
                Log.v("Hash Values",params.toString());
                url_arrayList = new ArrayList<String>();
                name_arrayList = new ArrayList<String>();

                AsyncUrl asyncUrl = new AsyncUrl(mContext,params,UrlResult,URL_JSON_ARRAY,
                        current_url,current_name,url_arrayList,name_arrayList,progressDialog);
                asyncUrl.execute(GET_URLS);

                progressDialog = ProgressDialog.show(mContext, "Please wait...", "While checking......", true);
                progressDialog.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        Log.v("Check UrlArraylist", String.valueOf(AsyncUrl.url_arrayList.size()));
                        Log.v("Check NameArraylist", String.valueOf(AsyncUrl.name_arrayList.size()));

                        String DNAME = "Chathamkulam"+"/"+subject;
                        final File rootPath = new File(Environment.getExternalStorageDirectory().toString(), DNAME);

                        if (!rootPath.exists()){

                            DownloadFile();

                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("This file already downloaded!!!");
                            builder.setMessage("If you want overwrite this file?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            deleteRecursive(rootPath);

                                            DownloadFile();

                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                    }
                }, 5000);

            }
        });

        holder.txtPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mContext.startActivity(new Intent(mContext, PaymentGateWay.class));
                Toast.makeText(mContext," Please buy our subjects !! ",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtDuration,txtPrice;
        NetworkImageView imageLoader;

        MyViewHolder(View view) {
            super(view);
            txtDuration = (TextView) view.findViewById(R.id.txtDuration);
            txtPrice = (TextView)view.findViewById(R.id.txtPrice);

            imageLoader = (NetworkImageView)view.findViewById(R.id.imageLoader);

        }
    }


    public void DownloadFile(){

        for (int i = 0; i < AsyncUrl.url_arrayList.size(); i++) {

            file_Url = String.valueOf(AsyncUrl.url_arrayList.get(i));
            final String fileType = file_Url.substring(file_Url.lastIndexOf(".") + 1);

            String file_Name = String.valueOf(AsyncUrl.name_arrayList.get(i));

            if (fileType.equals("mp4")) {

                String[] separated = file_Name.split("_");
                String fileName = separated[0];
                String ModuleName = separated[1];
                String TopicName = separated[2];

                String DNAME = "Chathamkulam"+"/"+subject+"/"+ModuleName;
                File rootPath = new File(Environment.getExternalStorageDirectory().toString(), DNAME);
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }

                if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Log.v("Cannot use storage","Cannot use storage");
                }

                final File myFinalDir = new File(rootPath,TopicName);

                try {

                    DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(file_Url);
                    Uri destination = Uri.fromFile(myFinalDir);

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setDestinationUri(destination);
                    request.setAllowedOverRoaming(false);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    final Long downloadId = downloadManager.enqueue(request);

                    IntentFilter filter = new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE);

                    final int finalI = i;
                    BroadcastReceiver receiver = new BroadcastReceiver() {
                        public void onReceive(Context ctxt, Intent intent) {

//                            try {
//                                FileCrypto.encrypt(myFinalDir, myFinalDir);
//                                Log.d("fileCrypto","Encrypted");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                Log.d("fileCrypto Exception",e.getMessage());
//                            }

                            dlcount++;

                            if(dlcount == AsyncUrl.url_arrayList.size()) {

//                                Toast.makeText(mContext,"Completed",Toast.LENGTH_LONG).show();
                                AddDetailsToSqlLite(mContext);
                                subscription(mContext);
//                                AskQuestion(mContext);

                            }
                        }
                    };
                    mContext.registerReceiver( receiver, filter);

                }catch (Exception e){
                    Log.d("DManagerException",e.getMessage());
                }
            } else {
                Log.v("Video result", "There is no video file");
            }

            if (!fileType.equals("mp4")) {

                String DNAME = "Chathamkulam"+"/"+subject;
                rootPath = new File(Environment.getExternalStorageDirectory().toString(), DNAME);
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Log.v("Cannot use storage","Cannot use storage");
                }

                final File myFinalDir = new File(rootPath,file_Name);

                try {
                    DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(file_Url);
                    Uri destination = Uri.fromFile(myFinalDir);

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setDestinationUri(destination);
                    request.setAllowedOverRoaming(false);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    final Long downloadId = downloadManager.enqueue(request);

                    IntentFilter filter = new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE);

                    final int finalI1 = i;
                    BroadcastReceiver receiver =new BroadcastReceiver() {
                        public void onReceive(Context ctxt, Intent intent) {

//                            if (!fileType.equals("jpg")){
//                                try {
//                                    FileCrypto.encrypt(myFinalDir, myFinalDir);
//                                    Log.d("fileCrypto","Encrypted");
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    Log.d("fileCrypto Exception",e.getMessage());
//                                }
//                            }

                            dlcount++;
                            if(dlcount == AsyncUrl.url_arrayList.size()) {

//                                Toast.makeText(mContext,"Completed",Toast.LENGTH_LONG).show();


                                AddDetailsToSqlLite(mContext);
                                subscription(mContext);
//                                AskQuestion(mContext);

                            }
                        }
                    };
                    mContext.registerReceiver( receiver, filter);

                }catch (Exception e){
                    Log.d("DownloadManager",e.getMessage());
                }
                Log.d("File path", myFinalDir.toString());
            }
        }
    }

    public void AddDetailsToSqlLite(Context context){

        StoreEntireDetails storeEntireDetails = new StoreEntireDetails(context);
        boolean IsEntry = storeEntireDetails.addData(country,university,course,semester,subject,subjectId,subjectNumber);
        if (IsEntry) {
            Toast.makeText(context, "Successfully Added", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(context, "Added failed", Toast.LENGTH_LONG).show();
        }
        Log.d("databaseValue",country+university+course+semester+subject+subjectId+subjectNumber);
    }

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

                Log.v("Volley Error",error.getMessage());
                loading.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> Hashmap = new HashMap<String, String>();

                RegisterMember registerMember = RegisterMember.getInstance(myContext);;
                Cursor cursorResult = registerMember.getDetails();

                while (cursorResult.moveToNext()) {
                    String MobileNumber= cursorResult.getString(2);

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

    private void AskQuestion(final Context context) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        RegisterMember registerMember = RegisterMember.getInstance(context);;
        Cursor cursorResult = registerMember.getDetails();

        while (cursorResult.moveToNext()) {
            String userName = cursorResult.getString(0);

            alertDialog.setTitle("Hi  "+userName);
            alertDialog.setMessage("Do you want to Continue Downloading process ?");
            alertDialog.setIcon(R.drawable.ic_question);
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();

                        }
                    });

            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(context, Drawer.class);
                            context.startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }

}
