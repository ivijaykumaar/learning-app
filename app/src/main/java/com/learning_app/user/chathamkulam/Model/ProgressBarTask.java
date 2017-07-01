package com.learning_app.user.chathamkulam.Model;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by User on 6/16/2017.
 */

public class ProgressBarTask extends ProgressBar {

//    private ProgressBar bar;
//
//    private int progress_status = 0;

    public ProgressBarTask(Context context) {
        super(context);
    }

    public ProgressBarTask(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressBarTask(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ProgressBarTask(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        progress_status = 0 ;
//
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//
//        while (progress_status < 100){
//            progress_status +=2;
//            publishProgress(progress_status);
//            SystemClock.sleep(300);
//        }
//
//        return null;
//    }
//
//    @Override
//    protected void onProgressUpdate(Integer... values) {
//        super.onProgressUpdate(values);
//        if (this.bar != null) {
//            bar.setProgress(values[0]);
//        }
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(aVoid);
//    }
}