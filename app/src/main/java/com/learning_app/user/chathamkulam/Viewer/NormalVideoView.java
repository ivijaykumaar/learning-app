package com.learning_app.user.chathamkulam.Viewer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.learning_app.user.chathamkulam.R;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by User on 5/27/2017.
 */

public class NormalVideoView extends Activity {


    VideoView  video_player_view;
    DisplayMetrics dm;
    MediaController media_Controller;

    LinkedList<String> linkedList;

    String currentSubject;
    String currentModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);

        video_player_view = (VideoView)findViewById(R.id.video_player_view);
        media_Controller = new MediaController(this);

        dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        video_player_view.setMinimumWidth(width);
        video_player_view.setMinimumHeight(height);

        if (media_Controller == null) {
            media_Controller = new MediaController(getApplicationContext());
            media_Controller.setAnchorView(video_player_view);
        }

        video_player_view.setMediaController(media_Controller);

        currentSubject = getIntent().getStringExtra("Key_subName");
        currentModule = getIntent().getStringExtra("Key_moduleName");
        final String currentFile = getIntent().getStringExtra("Key_fileName");

        String video = "Chathamkulam" + "/" + currentSubject + "/" + currentModule;
        File list = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), video);

        linkedList = new LinkedList<String>();
        File listFile[] = list.listFiles();

        if (listFile != null && listFile.length > 0) {

            for (File aListFile : listFile) {
                if (aListFile.isFile()) ;
                {
                    if (aListFile.getName().endsWith(".mp4")){
                        String fileName = aListFile.getName();
                        linkedList.add(fileName);
                    }else {
                        Log.v("File Exception","There is no video file");
                    }
                }
            }
        }

        String videoPath = "Chathamkulam" + "/" + currentSubject + "/" + currentModule + "/" + currentFile+".mp4";
        final File videoFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), videoPath);

//        final ProgressDialog progressDialog = ProgressDialog.show(this, "Please wait...", "While checking......", true);
//        progressDialog.show();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Do something after 100ms
//
//                if (progressDialog.isShowing()){
//                    progressDialog.dismiss();
//
//                    try {
//                    FileCrypto.encrypt(encryptedFile, encryptedFile);
//                        FileCrypto.decrypt(videoFile, videoFile);

                        video_player_view.setVideoURI(Uri.fromFile(videoFile));
                        video_player_view.start();

//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.d("CryptoException",e.getMessage());
//                    }
//                }
//            }
//        }, 5000);


        video_player_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video_player_view.stopPlayback();
                startVideo();
            }
        });
        video_player_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show();

                return false;
            }
        });
    }

    public void startVideo(){

        int position = getIntent().getIntExtra("Key_position",0);


        for (int i = position + 1; i<linkedList.size(); i++){

            Log.v("position1", String.valueOf(i));
            Log.v("Get FileName", String.valueOf(Uri.parse(linkedList.get(i))));

            String next_videoPath = "Chathamkulam"+"/"+ currentSubject +"/"+ currentModule+"/"+String.valueOf(Uri.parse(linkedList.get(i)));
            File next_videoFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),next_videoPath);
            video_player_view.setVideoURI(Uri.fromFile(next_videoFile));
            video_player_view.start();
            linkedList.remove(i);
        }
    }
}
