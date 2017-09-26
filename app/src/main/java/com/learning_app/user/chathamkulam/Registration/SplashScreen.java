package com.learning_app.user.chathamkulam.Registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.learning_app.user.chathamkulam.Fragments.Drawer;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;
import com.tonyodev.fetch.Fetch;

import java.io.File;

import static com.learning_app.user.chathamkulam.Sqlite.RegisterMember.DATABASE_NAME;

public class SplashScreen extends Activity {

    String stringDay;
    String stringMonth;
    String stringYear;
    String stringSubject;

    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Fetch.startService(this);
        Thread thread = new Thread() {

            @Override
            public void run() {

                try {

                    sleep(2000);

                    RegisterMember registerMember = RegisterMember.getInstance(getApplicationContext());

                    if (doesDatabaseExist(getApplicationContext(), DATABASE_NAME)) {

                        Cursor cursorResultEmail = registerMember.getDetails();

                        while (cursorResultEmail.moveToNext()) {

                            String userEmail = cursorResultEmail.getString(2);

                            if (registerMember.ifExists(userEmail)) {

                                startActivity(new Intent(getApplicationContext(), Drawer.class));
//                                deleteRecord(getApplicationContext());

                            } else {

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }
                    } else {

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }

                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

//    public void Validity(final Context myContext){
//
//        deleteCache(myContext);
//        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
//
//        final ProgressDialog loading = ProgressDialog.show(myContext, "Authenticating", "Please wait while we check your expiry date", false,false);
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST,VALIDITY, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                String[] separated = response.split("-");
//
//                stringSubject = separated[0];
//                stringDay = separated[1];
//                stringMonth = separated[2];
//                stringYear = separated[3];
//
//                String checkStatus = separated[4];
//
//                if (checkStatus.equals("0")){
//
//                    LovelyInfoDialog dialog = new LovelyInfoDialog(myContext);
//                    dialog.setTopColorRes(R.color.lightGreen);
//                    dialog.setIcon(R.drawable.ic_checked);
//
//                    //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
//                    dialog.setNotShowAgainOptionEnabled(0);
//                    dialog.setNotShowAgainOptionChecked(true);
//                    dialog.setTitle(R.string.info_title);
//                    dialog.setMessage(R.string.info_message);
//                    dialog.show();
//
//                } else {
//
//                    LovelyStandardDialog standardDialog =  new LovelyStandardDialog(myContext);
//                    standardDialog.setTopColorRes(R.color.lightRed);
//                    standardDialog.setButtonsColorRes(R.color.lightRed);
//                    standardDialog.setIcon(R.drawable.ic_expired);
//                    standardDialog.setTitle(R.string.expired_title);
//                    standardDialog.setMessage(R.string.expired_message);
//
//                    standardDialog.setPositiveButton("Yes", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            startActivity(new Intent(getApplicationContext(),PaymentGateWay.class));
//                        }
//                    });
//
//                    standardDialog.setNegativeButton("No", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                            startActivity(new Intent(getApplicationContext(),Drawer.class));
//                        }
//                    });
//
//                    standardDialog.show();
//
//                }
//
////                Toast.makeText(myContext,response, Toast.LENGTH_LONG).show();
//                loading.dismiss();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Log.v("Volley Error",error.getMessage());
//                loading.dismiss();
//
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams()  {
//                Map<String,String> Hashmap = new HashMap<String, String>();
//
//                RegisterMember registerMember = RegisterMember.getInstance(myContext);;
//                Cursor cursorResult = registerMember.getDetails();
//
//                while (cursorResult.moveToNext()) {
//                    String MobileNumber= cursorResult.getString(2);
//
//                    if (MobileNumber != null) {
//                        Hashmap.put(PHONENUMBER, MobileNumber);
//                        Log.v("Number Filed",MobileNumber);
//                    }else {
//
//                        Log.v("Number Filed",null);
//                    }
//                }
//                return Hashmap;
//            }
//        };
//        requestQueue.add(stringRequest);
//    }

}
