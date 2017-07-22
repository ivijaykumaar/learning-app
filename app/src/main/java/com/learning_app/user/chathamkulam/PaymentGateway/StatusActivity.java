package com.learning_app.user.chathamkulam.PaymentGateway;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.CheckingCards;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.learning_app.user.chathamkulam.Model.TestUrls.TEST_PAYMENT;

public class StatusActivity extends Activity {

	CheckingCards checkingCards;
	StringBuilder stringSemester;
	StringBuilder stringSubject;
	StringBuilder stringSubjectNumber;
	StringBuilder stringSubjectId;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_status);

		checkingCards = new CheckingCards(this);

		Intent mainIntent = getIntent();
		TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
		final String status = mainIntent.getStringExtra("transStatus");
		txtStatus.setText(status);

		if (status.equals("Transaction Successful!")){

			Cursor cursor = checkingCards.getCheckData();
			if (cursor.getCount() != 0){

				stringSemester = new StringBuilder();
				stringSubject = new StringBuilder();
				stringSubjectNumber = new StringBuilder();
				stringSubjectId = new StringBuilder();

				ArrayList<String> amountList = new ArrayList<String>();

				while (cursor.moveToNext()) {

					String semester = cursor.getString(5);
					String subject = cursor.getString(6);
					String subjectId = cursor.getString(7);
					String subjectNumber = cursor.getString(8);
					String amount = cursor.getString(9);

					Log.d("statusData",semester+" "+subject+" "+subjectId+" "+subjectNumber+" "+amount);

					stringSemester.append(semester).append(", ");
					stringSubject.append(subject).append(", ");
					stringSubjectId.append(subjectId).append(", ");
					stringSubjectNumber.append(subjectNumber).append(", ");

					amountList.add(amount+1);
				}
				cursor.close();

				int totalAmount = 0;
				for (int i = 0; i < amountList.size(); i++) {
					totalAmount += Integer.parseInt(amountList.get(i));
				}

				String subjectName = stringSubject.substring(0, stringSubject.length() - 2);
				Log.d("concatValue",subjectName+"   "+totalAmount);

				RequestQueue requestQueue = Volley.newRequestQueue(this);

				final ProgressDialog loading = ProgressDialog.show(StatusActivity.this,
						"Checking", "Please wait your detail will be check", false,false);
				StringRequest stringRequest = new StringRequest(Request.Method.POST,TEST_PAYMENT,new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						loading.dismiss();
						Log.d("Response",response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						loading.dismiss();
						Log.d("Response",error.getMessage());

					}
				}){

					@Override
					protected Map<String,String> getParams(){

						Map<String,String> Hashmap = new HashMap<String, String>();
						String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
						Hashmap.put("date",date);
						Hashmap.put("sem_no", String.valueOf(stringSemester));
						Hashmap.put("subject_id",String.valueOf(stringSubjectId));
						Hashmap.put("sub_no",String.valueOf(stringSubjectNumber));
						Hashmap.put("status",status);

						Log.d("mappingValue",Hashmap.toString());

						return Hashmap;
					}
				};
				requestQueue.add(stringRequest);

			}
		}
	}

	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}
} 