package com.learning_app.user.chathamkulam.Viewer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.learning_app.user.chathamkulam.Model.EncryptDecrypt.FileCrypto;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Registration.Registration;
import com.learning_app.user.chathamkulam.Sqlite.VideoHandler;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by User on 5/27/2017.
 */

public class NormalVideoView extends AppCompatActivity {

    TextView txtRibbon;
    VideoView  video_player_view;
    AVLoadingIndicatorView loadingIndicatorView;
    DisplayMetrics dm;
    MediaController media_Controller;

    ArrayList<String> videoList;
    RelativeLayout video_viewer_lay;

    String currentSubject;
    String currentModule;

    File outputDir;
    File outputFile;
    String currentFile;

    String onlineSubject;
    String onlineUrl;
    ProgressDialog loading;

    String topicName;

    VideoHandler videoHandler;
    int stopPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_video);

        video_player_view = (VideoView)findViewById(R.id.video_player_view);
        loadingIndicatorView = (AVLoadingIndicatorView)findViewById(R.id.aviLoading);
        video_viewer_lay = (RelativeLayout) findViewById(R.id.video_viewer_lay);
        txtRibbon = (TextView)findViewById(R.id.txtRibbon);

        videoHandler = VideoHandler.getInstance(getApplicationContext());
//        videoHandler.DeleteAll();

        getSupportActionBar().hide();

        loadingIndicatorView.smoothToShow();

        media_Controller = new MediaController(this);

        dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        video_player_view.setMinimumWidth(width);
        video_player_view.setMinimumHeight(height);
        video_player_view.setZOrderOnTop(true);

        if (media_Controller == null) {
            media_Controller = new MediaController(getApplicationContext());
            media_Controller.setAnchorView(video_player_view);
        }

        video_player_view.setMediaController(media_Controller);

        onlineSubject = getIntent().getStringExtra("Key_subjectName");
        onlineUrl = getIntent().getStringExtra("Key_OnlineJsonVideo");

        currentSubject = getIntent().getStringExtra("Key_subName");
        currentModule = getIntent().getStringExtra("Key_moduleName");
        currentFile = getIntent().getStringExtra("Key_fileName");

        Log.d("@@values ",currentSubject+"  "+currentModule+"  "+currentFile);

        if (onlineSubject == null){

            txtRibbon.setVisibility(View.GONE);
            getSupportActionBar().setTitle(currentSubject);

            String video = "Chathamkulam" + "/" + currentSubject + "/" + currentModule;
            File list = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), video);

            videoList = new ArrayList<>();
            File listFile[] = list.listFiles();

            if (listFile != null && listFile.length > 0) {

                for (File aListFile : listFile) {
                    if (aListFile.isFile()) {
                        if (aListFile.getName().endsWith(".mp4")){
                            String fileName = aListFile.getName();
                            videoList.add(fileName);
                        }else {
                            Log.v("File Exception","There is no video file");
                        }
                    }
                }
            } else {

                Toast.makeText(this,"There is no video file", Toast.LENGTH_SHORT).show();
            }

            String videoPath = "Chathamkulam" + "/" + currentSubject + "/" + currentModule + "/" + currentFile+".mp4";
            final File videoFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), videoPath);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    try {

                        outputDir = getApplicationContext().getCacheDir();
                        outputFile = File.createTempFile("Temp",".mp4", outputDir);
                        Log.v("Temp Created", videoFile.getName()+"   "+String.valueOf(outputFile));

                        FileCrypto.decrypt(videoFile,outputFile);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                loadingIndicatorView.smoothToHide();
                                Log.v("Temp view", String.valueOf(outputFile));
                                video_player_view.setVideoURI(Uri.fromFile(outputFile));
                                video_player_view.start();

                                Log.v("File","Decrypted Success");
                            }
                        });

                    } catch (final Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Decryption Error",e.toString());
                                loadingIndicatorView.smoothToHide();
                                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });

            video_player_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    loadingIndicatorView.smoothToHide();
                    topicName = videoFile.toString();
                }
            });

            video_player_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    if (onlineSubject == null) {

                        int totalDuration = video_player_view.getDuration();
                        Log.d("onResult", totalDuration +"   " + topicName);

                        if (totalDuration != 0 && !topicName.equals(null)) {

                            Cursor mainCursor = videoHandler.getAllData();
                            if (mainCursor.getCount() == 0) {

                                boolean IsEntry = videoHandler.AddTopicDetails(topicName, totalDuration, 1);
                                if (IsEntry) {
                                    Log.d("entryStatus", "Successfully Added");

                                } else {
                                    Log.d("entryStatus", "Added failed");
                                }

                            } else {

                                if (videoHandler.ifExists(topicName)) {

                                    Cursor cursor = videoHandler.getRow(topicName);
                                    while (cursor.moveToNext()) {

                                        String topicName = cursor.getString(1);
                                        String totalTime = cursor.getString(2);
                                        int count = cursor.getInt(3);

                                        boolean IsEntry = videoHandler.UpdateData(topicName, totalDuration,count+1);
                                        if (IsEntry) {
                                            Log.d("updateStatus", "Successfully Updated");

                                        } else {
                                            Log.d("updateStatus", "Added Updated");
                                        }
                                    }
                                    cursor.close();

                                } else {

                                    boolean IsEntry = videoHandler.AddTopicDetails(topicName, totalDuration, 1);
                                    if (IsEntry) {
                                        Log.d("updateStatus", "Successfully Added");

                                    } else {
                                        Log.d("updateStatus", "Added failed");
                                    }
                                }
                            }
                            mainCursor.close();
                        }

                        Cursor cursor1 = videoHandler.getAllData();
                        while (cursor1.moveToNext()) {

                            String topicName = cursor1.getString(1);
                            String totalTime = cursor1.getString(2);
                            String count = cursor1.getString(3);

                            Log.d("finalData", topicName + "  " + totalTime + "   " + count);
                        }
                        cursor1.close();

                        Log.d("cursorCount", String.valueOf(cursor1.getCount()));
                    }

                    video_player_view.stopPlayback();
                    startVideo();
//                loadingIndicatorView.smoothToHide();
//                startActivity(new Intent(getApplicationContext(),ModuleList.class));

                }
            });

            video_player_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(getApplicationContext(),"Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show();

                    return false;
                }
            });

        } else {

            txtRibbon.setVisibility(View.VISIBLE);
            getSupportActionBar().hide();
            getSupportActionBar().setTitle(onlineSubject);

            loading = ProgressDialog
                    .show(this,"Buffering.....","Please wait preparing your video!!", false, false);

            try{

                JSONArray jArray = null;
                jArray = new JSONArray(onlineUrl);

                String url = "";
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_obj = jArray.getJSONObject(i);
                    url = json_obj.getString("url");
                }

                Uri video = Uri.parse(url);
                Log.v("OnlineUrl", onlineUrl+"   "+ url);

                video_player_view.setVideoURI(video);

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(),"Oops An Error Occur While Playing Video Online...!!!", Toast.LENGTH_LONG).show();
                loading.dismiss();
                loadingIndicatorView.smoothToHide();
                Log.v("Exception video player", String.valueOf(e));
            }

            video_player_view.requestFocus();
            video_player_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {

                    loading.dismiss();
                    loadingIndicatorView.smoothToHide();
                    video_player_view.start();
                }
            });

            video_player_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

                    Toast.makeText(getApplicationContext(),"Oops An Error Occur While Playing Video Online...!!!", Toast.LENGTH_LONG).show();
                    loadingIndicatorView.smoothToHide();
                    loading.dismiss();

                    return true;
                }
            });
        }
    }

    public void startVideo(){

        loadingIndicatorView.smoothToShow();
        video_player_view.setVisibility(View.GONE);

        int position = getIntent().getIntExtra("Key_position",0);

        Log.v("Original position", String.valueOf(position));

        for (int i = position+1; i<videoList.size(); i++){

            Log.v("position1", String.valueOf(i));
            Log.v("Get FileName", String.valueOf(Uri.parse(videoList.get(i))));

            final String next_videoPath = "Chathamkulam"+"/"+ currentSubject +"/"+ currentModule+"/"+String.valueOf(Uri.parse(videoList.get(i)));
            final File next_videoFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),next_videoPath);

            final int finalI = i;

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    try {

                        File outputDir = getApplicationContext().getCacheDir();
                        final File outputFile = File.createTempFile("Temp",".mp4", outputDir);
                        Log.v("Temp Created", String.valueOf(outputFile));

                        FileCrypto.decrypt(next_videoFile,outputFile);
                        Log.v("File","Decrypted Success");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                loadingIndicatorView.smoothToHide();
                                video_player_view.setVisibility(View.VISIBLE);
                                video_player_view.setVideoURI(Uri.fromFile(outputFile));

                                video_player_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {

                                        topicName = next_videoFile.toString();
                                        int totalDuration = video_player_view.getDuration();
                                        Log.d("OnPrepared 2", topicName+"  "+String.valueOf(totalDuration));
                                    }
                                });

                                video_player_view.start();
                            }
                        });

                    } catch (final Exception e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Decryption Error",e.toString());
                                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!!", Toast.LENGTH_LONG).show();
                                loadingIndicatorView.smoothToHide();
                            }
                        });
                    }
                }
            });
            videoList.remove(finalI);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Registration.deleteCache(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_share).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
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
    public void onPause() {
        super.onPause();
        //stopPosition is an int
    }
}
