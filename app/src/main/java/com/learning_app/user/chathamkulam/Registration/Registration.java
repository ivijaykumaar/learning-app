package com.learning_app.user.chathamkulam.Registration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.learning_app.user.chathamkulam.Model.Validation;
import com.learning_app.user.chathamkulam.R;
import com.hbb20.CountryCodePicker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.learning_app.user.chathamkulam.Model.Constants.DOB;
import static com.learning_app.user.chathamkulam.Model.Constants.EMAIL;
import static com.learning_app.user.chathamkulam.Model.Constants.NAME;
import static com.learning_app.user.chathamkulam.Model.Constants.PHONENUMBER;
import static com.learning_app.user.chathamkulam.Model.Constants.PINCODE;
import static com.learning_app.user.chathamkulam.Model.Constants.REGSITRATOINURL;
import static com.learning_app.user.chathamkulam.Model.DateOfBirth.NewDay;
import static com.learning_app.user.chathamkulam.Model.DateOfBirth.NewMonth;
import static com.learning_app.user.chathamkulam.Model.DateOfBirth.NewYear;


public class Registration extends AppCompatActivity {

    EditText NameEdt,EmailEdt,NumberEdt,PincodeEdt;
    AutoCompleteTextView dateEdt,monthEdt,yearEdt;
    Button submitBtn;
    Validation validation;
    public static String mobile;
    CountryCodePicker ccp;
    String CountryCode;

    //    Background video
    MainActivity mainActivity;
    VideoView bg_video_view;
    String bg_video_path;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_area);

        validation = new Validation();


        NameEdt = (EditText)findViewById(R.id.editText_firstName);
        EmailEdt = (EditText)findViewById(R.id.editText_email);
        NumberEdt = (EditText)findViewById(R.id.editText_PhoneNumber);
        PincodeEdt  = (EditText)findViewById(R.id.editText_Pincode);
        dateEdt = (AutoCompleteTextView) findViewById(R.id.editText_day);
        monthEdt = (AutoCompleteTextView) findViewById(R.id.editText_month);
        yearEdt = (AutoCompleteTextView) findViewById(R.id.editText_year);
        ccp = (CountryCodePicker)findViewById(R.id.ccp);

        mainActivity = new MainActivity();
        bg_video_view = (VideoView) findViewById(R.id.bg_video_registration);
        bg_video_path = "android.resource://"+getPackageName()+"/"+R.raw.bg_video_reg;
        mainActivity.bg_video(bg_video_view,bg_video_path);


        mobile = null;
        deleteCache(getApplicationContext());
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {


                Toast.makeText(Registration.this, "Updated " + ccp.getSelectedCountryCodeWithPlus(), Toast.LENGTH_SHORT).show();
            }
        });

        submitBtn = (Button)findViewById(R.id.Btn_submit);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteCache(getApplicationContext());
                CountryCode = ccp.getSelectedCountryCodeWithPlus();

                String name = NameEdt.getText().toString();
                String EmailId = EmailEdt.getText().toString();
                String number = NumberEdt.getText().toString();

                String date = dateEdt.getText().toString();
                String month = monthEdt.getText().toString();
                String year = yearEdt.getText().toString();

                String pincode = PincodeEdt.getText().toString();

                if (name.length() == 0) {
                    NameEdt.setError("Must put Name");
                }else if (!validation.isValidEmail(EmailId)) {
                    EmailEdt.setError("Invalid Email");
                }else if (number.length() != 10) {
                    NumberEdt.setError("Invalid Number");
                } else if (date.length() == 0) {
                    dateEdt.setError("Invalid Date Format");
                }else if (date != null){

                    int dateParseInt = Integer.parseInt(date);
                    if (32 < dateParseInt){
                        dateEdt.setError("Invalid Date");
                    }else if (month.length() == 0){
                        monthEdt.setError("Invalid Month Format");
                    }else if (month != null){

                        int  monthParseInt = Integer.parseInt(month);
                        if (12 < monthParseInt){
                            monthEdt.setError("Invalid Month");
                        }else if (year.length() == 0){
                            yearEdt.setError("Invalid Year");
                        }else if (year != null){
                            int  yearParseInt = Integer.parseInt(year);
                            if (yearParseInt < 1950 || yearParseInt > 2050 ){
                                yearEdt.setError("Invalid Year");
                            }else if (pincode.length() != 6){
                                PincodeEdt.setError("Invalid Pincode");
                            } else {
                                deleteCache(getApplicationContext());
                                newUserRegistration();
                            }
                        }
                    }
                }
            }
        });

        dateOfBirth();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        mainActivity.bg_video(bg_video_view,bg_video_path);

    }

    public boolean newUserRegistration(){


        String Date = dateEdt.getText().toString().trim();
        String Month = monthEdt.getText().toString().trim();
        String Year = yearEdt.getText().toString().trim();

        final String name = NameEdt.getText().toString().trim();
        final String email = EmailEdt.getText().toString().trim();
        mobile = CountryCode + NumberEdt.getText().toString().trim();
        final String dateOfBirth = Date+"/"+Month+"/"+Year;
        final String pincode = PincodeEdt.getText().toString().trim();


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final ProgressDialog loading = ProgressDialog.show(Registration.this, "Registering!!", "Please wait while we check the entered details!!!", false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGSITRATOINURL, new Response.Listener<String>() {
            @Override

            public void onResponse(String response) {
                String res = "Successfully Registered";

                if (response.equals(res)){

                    loading.dismiss();
                    Intent i = new Intent(Registration.this,OtpConformation.class);
                    i.putExtra("Key_NewNumber",mobile);
                    startActivity(i);
                    Toast.makeText(Registration.this,res, Toast.LENGTH_LONG).show();
                    Toast.makeText(Registration.this,"Your otp will send via sms", Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(Registration.this,response, Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Registration.this,error.toString(),Toast.LENGTH_LONG).show();
                loading.dismiss();

            }
        }) {

            @Override
            protected Map<String,String> getParams(){

                Map<String,String> Hashmap = new HashMap<String, String>();
                Hashmap.put(NAME,name);
                Hashmap.put(EMAIL,email);
                Hashmap.put(PHONENUMBER,mobile);
                Hashmap.put(DOB,dateOfBirth);
                Hashmap.put(PINCODE,pincode);

                return Hashmap;
            }

        };
        requestQueue.add(stringRequest);
        return false;
    }

    public void dateOfBirth() {


        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,NewDay);
        dateEdt.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
        dateEdt.setAdapter(dateAdapter);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,NewMonth);
        monthEdt.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
        monthEdt.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,NewYear);
        yearEdt.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
        yearEdt.setAdapter(yearAdapter);

    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
