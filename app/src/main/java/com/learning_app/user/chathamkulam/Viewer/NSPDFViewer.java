package com.learning_app.user.chathamkulam.Viewer;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.learning_app.user.chathamkulam.R;

import java.io.File;

/**
 * Created by User on 3/22/2017.
 */

public class NSPDFViewer extends AppCompatActivity {

    String fileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf);

        try {

            PDFView pdfView = (PDFView)findViewById(R.id.pdfView);
            final String currentSubjectName = getIntent().getStringExtra("Key_pdf");
            getSupportActionBar().setTitle(currentSubjectName);

            String DNAME = "Chathamkulam"+"/"+currentSubjectName;
            File rootPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), DNAME);

            File listFile[] = rootPath.listFiles();
            if (listFile != null && listFile.length > 0) {

                for (File aListFile : listFile) {

                    if (aListFile.isFile()) {

                        if (aListFile.getName().endsWith(".pdf")) {

                            fileName = aListFile.getName();
                            String firstWord = fileName.substring(0,1);
                            Log.d("Vijay ",firstWord);

                            if (!firstWord.equals("#")){

                                String mainRoot = "Chathamkulam" + "/" + currentSubjectName + "/" + fileName;
                                File mainPath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), mainRoot);
                                pdfView.fromFile(mainPath).scrollHandle(new DefaultScrollHandle(this)).load();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
