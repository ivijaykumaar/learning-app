package com.learning_app.user.chathamkulam.Viewer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.learning_app.user.chathamkulam.Adapters.StoreCardAdapter;
import com.learning_app.user.chathamkulam.Model.EncryptDecrypt.FileCrypto;
import com.learning_app.user.chathamkulam.Fragments.Drawer;
import com.learning_app.user.chathamkulam.Model.BackgroundWork.AsyncUrl;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Registration.Registration;
import com.learning_app.user.chathamkulam.Sqlite.CheckingCards;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.learning_app.user.chathamkulam.Model.Constants.GET_URLS;

/**
 * Created by User on 3/22/2017.
 */

public class QBPDFViewer extends AppCompatActivity {

    String fileName;

    //   Download AsyncUrl variables
    private JSONArray UrlResult = null;
    private String URL_JSON_ARRAY = "result";
    private String current_url = "file";
    private String current_name = "name";
    private ArrayList<String> url_arrayList;
    private ArrayList<String> name_arrayList;
    private ProgressDialog progressDialog;

    PDFView qBankPdfView;
    WebView qBankWebView;
    TextView txtRibbon;

    File mainPath;
    ProgressBar qBankProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_qbank);

        qBankPdfView  = (PDFView)findViewById(R.id.qBankPdfView);
        qBankWebView = (WebView) findViewById(R.id.qBankWebView);
        txtRibbon = (TextView)findViewById(R.id.txtRibbon);
//        qBankProgressBar = (ProgressBar)findViewById(R.id.qBankProgressBar);
        qBankWebView.setBackgroundColor(0x00000000);

        txtRibbon.setVisibility(View.GONE);
        qBankPdfView.setVisibility(View.GONE);
        qBankWebView.setVisibility(View.GONE);

        final String currentSubjectName = getIntent().getStringExtra("Key_pdf");

        final CheckingCards checkingCards = new CheckingCards(this);
        Cursor cursor = checkingCards.getCheckData();

        if (currentSubjectName != null){

            qBankPdfView.setVisibility(View.VISIBLE);
            try {
                getSupportActionBar().setTitle(currentSubjectName);

                String DNAME = "Chathamkulam"+"/"+currentSubjectName;
                File rootPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), DNAME);

                if (rootPath.exists()){

                    File listFile[] = rootPath.listFiles();
                    if (listFile != null && listFile.length > 0) {

                        for (File aListFile : listFile) {

                            if (aListFile.isFile()) {

                                if (aListFile.getName().endsWith(".pdf")) {

                                    fileName = aListFile.getName();
                                    String firstWord = fileName.substring(0,1);

                                    Log.d("Vijay ",firstWord);

                                    if (firstWord.equals("#")){

                                        String mainRoot = "Chathamkulam" + "/" + currentSubjectName + "/" + fileName;
                                        mainPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), mainRoot);

                                        final ProgressDialog loading = ProgressDialog.show(QBPDFViewer.this, "Processing",
                                                "Please wait process your file", false,false);

                                        try {

                                            File outputDir = getApplicationContext().getCacheDir();
                                            final File outputFile = File.createTempFile("Temp",".pdf", outputDir);

                                            FileCrypto.decrypt(mainPath,outputFile);
                                            Log.v("File","Decrypted Success");
                                            loading.dismiss();
                                            qBankPdfView.fromFile(outputFile).scrollHandle(new DefaultScrollHandle(this)).onRender(new OnRenderListener() {
                                                @Override
                                                public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                                                    qBankPdfView.fitToWidth();
                                                }
                                            }).load();

                                        } catch (Exception e) {
                                            loading.dismiss();
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {

                    Log.d("FolderInfo","Not found");

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Restoration Process");
                    builder.setMessage("Your file contents are missing do you want restore it, By free downloading?")
                            .setCancelable(false)
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    StoreEntireDetails storeEntireDetails = new StoreEntireDetails(getApplicationContext());
                                    Cursor cursor = storeEntireDetails.getSubjectRow(currentSubjectName);
                                    Log.d("cursorInfo", String.valueOf(cursor.getCount()));

                                    if (cursor.getCount() != 0){

                                        if (cursor.moveToFirst()){

                                            do {

                                                Log.d("#country",cursor.getString(1)+"  ");
                                                Log.d("#university", cursor.getString(2)+"  ");
                                                Log.d("#course", cursor.getString(3)+"  ");
                                                Log.d("#sem", cursor.getString(4)+"  ");
                                                Log.d("#subject", cursor.getString(5)+"  ");
//                            subject = cursor.getString(5);
                                                Log.d("#sub_no", cursor.getString(6)+"  ");
                                                Log.d("#subject_id", cursor.getString(7)+"  ");
                                                Log.d("#free_validity", cursor.getString(8)+"  ");
//                            freeValidity = cursor.getString(8);
                                                Log.d("#paid_validity", cursor.getString(9)+"  ");
                                                Log.d("#duration", cursor.getString(10)+"  ");

                                                final String country = cursor.getString(1);
                                                final String university = cursor.getString(2);
                                                final String course = cursor.getString(3);
                                                final String semester = cursor.getString(4);
                                                final String subject = cursor.getString(5);
                                                final String subjectId = cursor.getString(6);
                                                final String subjectNumber = cursor.getString(7);
                                                final String freeValidity = cursor.getString(8);
                                                final String paidValidity = cursor.getString(9);
                                                final String duration = cursor.getString(10);
                                                final String videoCount = cursor.getString(11);
                                                final String notesCount = cursor.getString(12);
                                                final String qbankCount = cursor.getString(13);

//                            put values for download
                                                HashMap<String,String> params = new HashMap<String, String>();
                                                params.put("sem_no",semester);
                                                params.put("id",subjectId);
                                                params.put("sub_no",subjectNumber);
                                                params.put("type","url");

                                                Log.v("Hash Values",params.toString());

                                                url_arrayList = new ArrayList<String>();
                                                name_arrayList = new ArrayList<String>();

                                                AsyncUrl asyncUrl = new AsyncUrl(QBPDFViewer.this,params,UrlResult,URL_JSON_ARRAY,
                                                        current_url,current_name,url_arrayList,name_arrayList,progressDialog);
                                                asyncUrl.execute(GET_URLS);

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                                    @Override
                                                    public void run() {
//                                    Do something after 100ms
                                                        Log.v("Check UrlArraylist", String.valueOf(AsyncUrl.url_arrayList.size()));
                                                        Log.v("Check NameArraylist", String.valueOf(AsyncUrl.name_arrayList.size()));

                                                        if (AsyncUrl.url_arrayList != null){

                                                            Log.v("Check Entry", String.valueOf(url_arrayList.size()));
                                                            final StoreCardAdapter adapter = new StoreCardAdapter(QBPDFViewer.this);
                                                            adapter.DownloadFile(QBPDFViewer.this,country,university,course,semester,subject,subjectId,
                                                                    subjectNumber,freeValidity,paidValidity,duration,videoCount,notesCount,qbankCount);

                                                        }
                                                    }
                                                }, 10000);

                                            }while (cursor.moveToNext());

                                        } cursor.close();
                                    }

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                    Intent intent = new Intent(QBPDFViewer.this,Drawer.class);
                                    startActivity(intent);
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {

            if (cursor != null && cursor.getCount() == 1){

                qBankWebView.setVisibility(View.VISIBLE);
                txtRibbon.setVisibility(View.VISIBLE);

                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {

                        String position = cursor.getString(1);
                        final String country = cursor.getString(2);
                        final String university = cursor.getString(3);
                        final String course = cursor.getString(4);
                        final String semester = cursor.getString(5);
                        final String subject = cursor.getString(6);
                        final String subjectId = cursor.getString(7);
                        final String subjectNumber = cursor.getString(8);

                        getSupportActionBar().setTitle(subject);
                        Log.d("final data",position+semester+subject+subjectId+subjectNumber);

//                            put values for download
                        HashMap<String,String> params = new HashMap<String, String>();
                        params.put("sem_no",semester);
                        params.put("id",subjectId);
                        params.put("sub_no",subjectNumber);
                        params.put("type","url");

                        Log.v("Subject Values",subject+subjectId+subjectNumber+semester);
                        Log.v("Hash Values",params.toString());
                        url_arrayList = new ArrayList<String>();
                        name_arrayList = new ArrayList<String>();

                        AsyncUrl asyncUrl = new AsyncUrl(this,params,UrlResult,URL_JSON_ARRAY,
                                current_url,current_name,url_arrayList,name_arrayList,progressDialog);
                        asyncUrl.execute(GET_URLS);

                        progressDialog = ProgressDialog.show(this, "Please wait...", "While checking......", true);
                        progressDialog.show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @SuppressLint("SetJavaScriptEnabled")
                            @Override
                            public void run() {
                                //Do something after 100ms

                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();

                                    Log.v("Check UrlArraylist", String.valueOf(AsyncUrl.url_arrayList.size()));
                                    Log.v("Check NameArraylist", String.valueOf(AsyncUrl.name_arrayList.size()));

                                    for (int i = 0;i<AsyncUrl.url_arrayList.size();i++){

                                        String file_Url = String.valueOf(AsyncUrl.url_arrayList.get(i));
                                        final String fileType = file_Url.substring(file_Url.lastIndexOf(".") + 1);
                                        String file_Name = String.valueOf(AsyncUrl.name_arrayList.get(i));

                                        if (fileType.equals("pdf")){

                                            String firstWord = file_Name.substring(0,1);
                                            Log.d("QBank ",firstWord);

                                            if (firstWord.equals("#")){

                                                qBankWebView.setWebViewClient(new MyWebViewClient());
                                                qBankWebView.getSettings().setJavaScriptEnabled(true);
                                                qBankWebView.loadUrl("http://docs.google.com/gview?embedded=true&url="+file_Url);
                                                qBankWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

                                                Log.v("Check NameArraylist",file_Url);

                                            }
                                        }
                                    }
                                }

                            }
                        }, 7000);

                        cursor.moveToNext();
                    }
                }
            }
        }

        String subjectName = getIntent().getStringExtra("Key_subjectName");
        String resultUrl = getIntent().getStringExtra("Key_OnlineJsonVideo");

        Log.v("Online Values",subjectName + resultUrl);

        if (subjectName!=null){

            try {
                qBankWebView.setVisibility(View.VISIBLE);
                txtRibbon.setVisibility(View.VISIBLE);

                JSONArray jArray = null;
                jArray = new JSONArray(resultUrl);

                String url = "";
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_obj = jArray.getJSONObject(i);
                    url = json_obj.getString("url");
                }

                qBankWebView.setWebViewClient(new MyWebViewClient());
                qBankWebView.getSettings().setJavaScriptEnabled(true);
                qBankWebView.loadUrl("http://docs.google.com/gview?embedded=true&url="+url);
                qBankWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        if (mainPath.exists()){
//
//            try {
//                FileCrypto.encrypt(mainPath,mainPath);
//                Log.v("File","Encrypted Success");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

//            qBankProgressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {

        }

        @Override
        public void onPageFinished(WebView view, String url) {

            // TODO Auto-generated method stub
//            qBankProgressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
//            qBankProgressBar.setVisibility(View.GONE);
            super.onPageStarted(view, url, favicon);

        }
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
            //Your code to do
//            qBankProgressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Your Internet Connection May not be active Or " + error , Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_submit).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_share:

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Select your option";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Registration.deleteCache(getApplicationContext());
    }
}
