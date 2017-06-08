package com.learning_app.user.chathamkulam.Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.learning_app.user.chathamkulam.Fragments.Drawer;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;

import java.util.HashMap;
import java.util.Map;

import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;

import static com.learning_app.user.chathamkulam.Model.Constants.CONFORMATIONURL;
import static com.learning_app.user.chathamkulam.Model.Constants.OTP;
import static com.learning_app.user.chathamkulam.Model.Constants.PHONENUMBER;
import static com.learning_app.user.chathamkulam.Model.Constants.RESENDURL;
import static com.learning_app.user.chathamkulam.Registration.AlreadyRegistered.existingNumber;
import static com.learning_app.user.chathamkulam.Registration.Registration.deleteCache;
import static com.learning_app.user.chathamkulam.Registration.Registration.mobile;


public class OtpConformation extends AppCompatActivity implements OTPListener {

    ProgressDialog loading;
    RequestQueue requestQueue;
    EditText OtpEdt;
    TextView timerText;
    Button ResendOtpBtn,SubmitBtn;
    Registration registration;
    TextView verifyNumberEdt,currentNumberEdt;
    LinearLayout verifyLay,anotherVerifyLay;

    MainActivity mainActivity;
    VideoView bg_video_view;
    String bg_video_path;

    public static String CurrentName;
    public static String CurrentEmail;
    public static String CurrentMobNo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_conformation);
        OtpReader.bind(this,"DM-MKMIND");

        requestQueue = Volley.newRequestQueue(this);
        timerText = (TextView)findViewById(R.id.text_Timmer);
        ResendOtpBtn = (Button)findViewById(R.id.resendOtp_BTn);
        SubmitBtn = (Button)findViewById(R.id.Btn_submit);
        OtpEdt = (EditText)findViewById(R.id.otp_edit);
        verifyNumberEdt = (TextView)findViewById(R.id.verifyNumberEdt);
        currentNumberEdt = (TextView)findViewById(R.id.currentNumberEdt);
        verifyLay = (LinearLayout)findViewById(R.id.verify_layout);
        anotherVerifyLay = (LinearLayout)findViewById(R.id.anotherVerify_lay);

        mainActivity = new MainActivity();
        bg_video_view = (VideoView) findViewById(R.id.bg_video_otp_conform);
        bg_video_path = "android.resource://"+getPackageName()+"/"+R.raw.bg_video_reg;
        mainActivity.bg_video(bg_video_view,bg_video_path);

        registration = new Registration();
        final MyCounter myCounter = new MyCounter(120000,1000);
        myCounter.start();
        ResendOtpBtn.setVisibility(View.GONE);

        String NewNumber = getIntent().getStringExtra("Key_NewNumber");
        String AlreadyNumber = getIntent().getStringExtra("Key_AlreadyNumber");

        if (AlreadyNumber != null){
            currentNumberEdt.setText(AlreadyNumber);
            verifyNumberEdt.setText(AlreadyNumber);
        }else {
            currentNumberEdt.setText(NewNumber);
            verifyNumberEdt.setText(NewNumber);
        }

        deleteCache(getApplicationContext());
        ResendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteCache(getApplicationContext());
                ResendOtp();

            }
        });

        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteCache(getApplicationContext());
                ConformOtp();

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mainActivity.bg_video(bg_video_view,bg_video_path);
    }

    public boolean ConformOtp(){

        loading = ProgressDialog.show(OtpConformation.this, "Authenticating", "Please wait while we check the entered code", false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,CONFORMATIONURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] separated = response.split("-");

                String CurrentResponse = separated[0];
                CurrentName = separated[1];
                CurrentEmail = separated[2];
                CurrentMobNo = separated[3];

                if(CurrentResponse.equalsIgnoreCase("Your OTP is Verified")){

                    loading.dismiss();

                    Toast.makeText(OtpConformation.this,CurrentResponse,Toast.LENGTH_LONG).show();

                    RegisterMember registerMember = RegisterMember.getInstance(getApplicationContext());

                    if (!registerMember.ifExists(CurrentEmail)){

                        boolean IsEntry = registerMember.addMember(CurrentName,CurrentEmail,CurrentMobNo);
                        if (IsEntry) {
                            Toast.makeText(getApplicationContext(), "Now you Are a Member our Organization", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Already you are Member", Toast.LENGTH_LONG).show();
                        }

                    }else{

                        Toast.makeText(getApplicationContext(), "Already you Are a Member our Organization", Toast.LENGTH_LONG).show();

                    }

                    startActivity(new Intent(OtpConformation.this,Drawer.class));
                }else {

                    Toast.makeText(OtpConformation.this,"Wrong Otp Please Try Again",Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(OtpConformation.this,error+"PLease check your And entered code",Toast.LENGTH_SHORT).show();
                loading.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> Hashmap = new HashMap<String, String>();

                final String newOtp = OtpEdt.getText().toString();
                if (newOtp.length() != 0 ) {

                    if (existingNumber != null) {
                        Hashmap.put(OTP, newOtp);
                        Hashmap.put(PHONENUMBER, existingNumber);

                    } else {
                        Hashmap.put(OTP, newOtp);
                        Hashmap.put(PHONENUMBER, mobile);
                    }
                }
                return Hashmap;
            }
        };

        requestQueue.add(stringRequest);
        return false;
    }
    public boolean ResendOtp(){

        final ProgressDialog loading = ProgressDialog.show(OtpConformation.this, "Resending", "Please wait your code will be Resend", false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,RESENDURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loading.dismiss();
                Toast.makeText(OtpConformation.this,response,Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading.dismiss();
                Toast.makeText(OtpConformation.this,error.toString(),Toast.LENGTH_LONG).show();

            }
        }){

            @Override
            protected Map<String,String> getParams(){

                Map<String,String> Hashmap = new HashMap<String, String>();

                if (existingNumber != null){
                    Hashmap.put(PHONENUMBER,existingNumber);
                }else{
                    Hashmap.put(PHONENUMBER,mobile);
                }

                return Hashmap;
            }
        };
        requestQueue.add(stringRequest);
        return false;
    }

    @Override
    public void otpReceived(String messageText) {

        if (messageText != null){
            OtpEdt.setText(messageText);
        }
    }

    public class MyCounter extends CountDownTimer {

        public MyCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            ResendOtpBtn.setVisibility(View.VISIBLE);
            timerText.setVisibility(View.GONE);


        }

        @Override
        public void onTick(long millisUntilFinished) {

            int seconds = (int) (millisUntilFinished / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timerText.setText(String.format("%02d", minutes)
                    + ":" + String.format("%02d", seconds));

        }
    }
}
