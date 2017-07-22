package com.learning_app.user.chathamkulam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hbb20.CountryCodePicker;
import com.learning_app.user.chathamkulam.Fragments.Drawer;
import com.learning_app.user.chathamkulam.Model.DbBitmapUtility;
import com.learning_app.user.chathamkulam.Model.Validation;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.learning_app.user.chathamkulam.Model.Constants.EMAIL;
import static com.learning_app.user.chathamkulam.Model.Constants.NAME;
import static com.learning_app.user.chathamkulam.Model.Constants.PHONENUMBER;
import static com.learning_app.user.chathamkulam.Model.Constants.PROFILE_UPDATE_URL;

/**
 * Created by User on 7/15/2017.
 */

public class EditProfileStatus extends AppCompatActivity {

    EditText edtUserName,edtEmail,edtNumber;
    Button btnUpdate;
    Validation validation;
    ImageView ic_edtPic;
    CountryCodePicker ccp;

    private static final int SELECT_PHOTO = 100;

    CircleImageView circleImageView;
    DbBitmapUtility dbBitmapUtility;
    RegisterMember registerMember;
    Cursor cursorResult;

    Bitmap yourSelectedImage;

    String currentNumber;
    String CountryCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        dbBitmapUtility = new DbBitmapUtility();
        registerMember = RegisterMember.getInstance(this);

        validation = new Validation();

        edtUserName = (EditText)findViewById(R.id.EdtUserName);
        edtEmail = (EditText)findViewById(R.id.EdtEmailId);
        edtNumber = (EditText)findViewById(R.id.EdtMobileNumber);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        ic_edtPic = (ImageView)findViewById(R.id.ic_edtPic);
        circleImageView = (CircleImageView) findViewById(R.id.circle_imageView);
        ccp = (CountryCodePicker)findViewById(R.id.ccp);

        cursorResult = registerMember.getDetails();

        while (cursorResult.moveToNext()) {

            String userName = cursorResult.getString(1);
            String emailId = cursorResult.getString(2);
            currentNumber = cursorResult.getString(3);
            byte[] profile = cursorResult.getBlob(4);

            Log.d("profileStatus",cursorResult.getCount()+" "+userName+"  "+emailId+" "+currentNumber+"  "+Arrays.toString(profile));

            edtUserName.setHint(userName);
            edtEmail.setHint(emailId);
            edtNumber.setHint(currentNumber.substring(3));
            circleImageView.setImageBitmap(dbBitmapUtility.getImage(profile));

        }
        cursorResult.close();

        ic_edtPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);

            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtUserName.getText().toString();
                String email = edtEmail.getText().toString();
                String number = edtNumber.getText().toString();

                if (name.length() == 0){
                    edtUserName.setError("Must put Name");
                } else if(!validation.isValidEmail(email)) {
                    edtEmail.setError("Invalid Email");
                } else if (number.length() != 10){
                    edtNumber.setError("Invalid Number");
                } else {
                    updateProfile(currentNumber,name,email,number);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:

                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    circleImageView.setImageBitmap(yourSelectedImage);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, Drawer.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_share).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.menu_submit).setVisible(false);

        return true;
    }

    public boolean updateProfile(final String oldNumber,final String name,
                                 final String email,final String mobile){

        CountryCode = ccp.getSelectedCountryCodeWithPlus();
        final String mobileNumber = CountryCode + mobile.trim();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final ProgressDialog loading = ProgressDialog.show(EditProfileStatus.this, "Updating!!","Please wait while we update your profile!!!", false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PROFILE_UPDATE_URL, new Response.Listener<String>() {
            @Override

            public void onResponse(String response) {

                Log.d("updateStatus",response);
                String res = "Successfully Updated";

                if (response.equals(res)){
                    loading.dismiss();

                    if (yourSelectedImage != null){

                        boolean status = registerMember.updatePic(name,email,mobileNumber,dbBitmapUtility.getBytes(yourSelectedImage));
                        if (status){

                            Log.d("updateStatus","updatedSuccessfully");
                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                        } else {
                            Log.d("updateStatus","fail");
                        }

                    } else {

                        BitmapDrawable drawable = (BitmapDrawable) circleImageView.getDrawable();
                        yourSelectedImage = drawable.getBitmap();

                        boolean status = registerMember.updatePic(name,email,mobileNumber,dbBitmapUtility.getBytes(yourSelectedImage));
                        if (status){

                            Log.d("updateStatus","updatedSuccessfully");
                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                        } else {
                            Log.d("updateStatus","fail");
                        }
                    }
                }
                else {

                    Log.d("updateStatus",response);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("updateStatus",error.toString());
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                loading.dismiss();

            }
        }) {

            @Override
            protected Map<String,String> getParams(){

                Map<String,String> Hashmap = new HashMap<String, String>();
                Hashmap.put("old_number",oldNumber);
                Hashmap.put(NAME,name);
                Hashmap.put(EMAIL,email);
                Hashmap.put(PHONENUMBER,mobileNumber);

                return Hashmap;
            }

        };
        requestQueue.add(stringRequest);
        return false;
    }
}
