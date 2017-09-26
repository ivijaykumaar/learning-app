package com.learning_app.user.chathamkulam.Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
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
import com.hbb20.CountryCodePicker;
import com.learning_app.user.chathamkulam.Model.Validation;
import com.learning_app.user.chathamkulam.R;

import java.util.HashMap;
import java.util.Map;

import static com.learning_app.user.chathamkulam.Apis.API_ALREADY_USER;
import static com.learning_app.user.chathamkulam.Model.Constants.PHONENUMBER;
import static com.learning_app.user.chathamkulam.Registration.Registration.deleteCache;


public class AlreadyRegistered extends AppCompatActivity {


    public static String existingNumber;
    EditText MobileEdt, EmailEdt;
    LinearLayout numberLay;
    TextView txtNotify;
    CountryCodePicker countryCodePicker;
    String CountryCode;
    MainActivity mainActivity;
    VideoView bg_video_view;
    String bg_video_path;

    Button btnMoreOptions;
    PopupMenu popupMenu;
    boolean click = true;
    Validation validation;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.already_registered);

        Button proceedBtn = (Button) findViewById(R.id.proceed_Btn);
        MobileEdt = (EditText) findViewById(R.id.NumberEdt);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.already_ccp);
        EmailEdt = (EditText) findViewById(R.id.edtEmail);
        EmailEdt.setVisibility(View.GONE);
        btnMoreOptions = (Button) findViewById(R.id.btnMoreOptions);
        numberLay = (LinearLayout) findViewById(R.id.numberLay);
        txtNotify = (TextView) findViewById(R.id.txtNotify);

        mainActivity = new MainActivity();
        bg_video_view = (VideoView) findViewById(R.id.bg_video_already_reg);
        bg_video_path = "android.resource://" + getPackageName() + "/" + R.raw.bg_video_reg;
        mainActivity.bg_video(bg_video_view, bg_video_path);

        deleteCache(getApplicationContext());
        existingNumber = null;

        validation = new Validation();

        String alreadyRegisterNum = getIntent().getStringExtra("Key_NewNumber");

        if (alreadyRegisterNum != null) {
            MobileEdt.setText(alreadyRegisterNum.substring(3));
        }

        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                Toast.makeText(AlreadyRegistered.this, "Updated " + countryCodePicker.getSelectedCountryCodeWithPlus(), Toast.LENGTH_SHORT).show();
            }
        });

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteCache(getApplicationContext());
                CountryCode = countryCodePicker.getSelectedCountryCodeWithPlus();

                String mobileNum = MobileEdt.getText().toString();
                if (mobileNum.length() != 10) {
                    MobileEdt.setError("Invalid Number");
                } else if (AlreadyRegister()) {
                    Toast.makeText(getApplicationContext(), "Checking", Toast.LENGTH_SHORT).show();
                }

                String emailId = EmailEdt.getText().toString();
                if (!validation.isValidEmail(emailId)) {
                    EmailEdt.setError("Invalid Email");
                } else if (AlreadyRegister()) {
                    Toast.makeText(getApplicationContext(), "Checking", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu = new PopupMenu(AlreadyRegistered.this, v);
                popupMenu.inflate(R.menu.more_options);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {

                            case R.id.createAcc:

                                startActivity(new Intent(getApplicationContext(), Registration.class));

                                break;

                            case R.id.lossPhone:

                                if (click) {

                                    numberLay.setVisibility(View.GONE);
                                    EmailEdt.setVisibility(View.VISIBLE);
                                    txtNotify.setText("Verify your email");
                                    click = false;

                                } else {

                                    numberLay.setVisibility(View.VISIBLE);
                                    EmailEdt.setVisibility(View.GONE);
                                    txtNotify.setText("Verify your mobile number");
                                    click = true;
                                }

                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mainActivity.bg_video(bg_video_view, bg_video_path);
    }

    public boolean AlreadyRegister() {

        existingNumber = CountryCode + MobileEdt.getText().toString();


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final ProgressDialog loading = ProgressDialog.show(AlreadyRegistered.this, "Checking", "Please wait your detail will be check", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ALREADY_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("registerResponse", response);

                if (response.equalsIgnoreCase("1")) {
                    loading.dismiss();

                    Intent i = new Intent(AlreadyRegistered.this, OtpConformation.class);
                    i.putExtra("Key_AlreadyNumber", existingNumber);
                    startActivity(i);

                    Toast.makeText(AlreadyRegistered.this, "Your otp will be send", Toast.LENGTH_SHORT).show();
                } else {

                    loading.dismiss();
                    Toast.makeText(AlreadyRegistered.this, "You are a new user please registered us", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AlreadyRegistered.this, Registration.class);
                    startActivity(i);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading.dismiss();
                Toast.makeText(AlreadyRegistered.this, "Bad Network Connection", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> Hashmap = new HashMap<String, String>();
                Hashmap.put(PHONENUMBER, existingNumber);

                return Hashmap;
            }
        };
        requestQueue.add(stringRequest);
        return false;
    }
}
