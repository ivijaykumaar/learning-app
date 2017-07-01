package com.learning_app.user.chathamkulam.Registration;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.VideoView;

import com.learning_app.user.chathamkulam.Model.InternetDetector;
import com.learning_app.user.chathamkulam.R;

public class MainActivity extends AppCompatActivity {

    CheckBox AgreeChb;
    Button NewUserBtn,ExistingUserBtn;

    VideoView bg_video_view;
    String bg_video_path;

    InternetDetector internetDetector;
    Boolean isConnectionExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        AgreeChb = (CheckBox)findViewById(R.id.AgreecheckBox);
        ExistingUserBtn = (Button)findViewById(R.id.ExistingUserBtn);
        NewUserBtn = (Button)findViewById(R.id.NewUserBtn);

        bg_video_view = (VideoView) findViewById(R.id.bg_video_home);
        bg_video_path = "android.resource://"+getPackageName()+"/"+R.raw.bg_video_home;
        bg_video(bg_video_view,bg_video_path);

        NewUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AgreeChb.isChecked()){

                    startActivity(new Intent(MainActivity.this,Registration.class));
                }else{
                    Toast.makeText(MainActivity.this, "please accept our Condition", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ExistingUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    startActivity(new Intent(MainActivity.this,AlreadyRegistered.class));

            }
        });

        internetDetector = new InternetDetector(getApplicationContext());

        isConnectionExist = internetDetector.checkMobileInternetConn();
        if (isConnectionExist) {

            Log.v("Internet Connection","Yeah ! Internet Found !!");
        } else {

            Log.v( "No Internet Connection", "Your device doesn't have mobile internet");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        bg_video(bg_video_view,bg_video_path);

    }

    public void bg_video(VideoView videoView,String filename){

        Uri uri = Uri.parse(filename);
        videoView.setVideoURI(uri);
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.setLooping(true);

                try {
                    if (mediaPlayer.isPlaying()) {

                        mediaPlayer.stop();
                        mediaPlayer.release();

                    }
                    mediaPlayer.setVolume(0f, 0f);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
