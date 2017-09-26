package com.learning_app.user.chathamkulam.Feedback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.learning_app.user.chathamkulam.Fragments.Drawer;

import java.util.List;

public class SendMailTask extends AsyncTask {

	private ProgressDialog statusDialog;
	private Activity sendMailActivity;

	public SendMailTask(Activity activity) {
		sendMailActivity = activity;

	}

	protected void onPreExecute() {
		statusDialog = new ProgressDialog(sendMailActivity);
		statusDialog.setMessage("Getting ready...");
		statusDialog.setIndeterminate(false);
		statusDialog.setCancelable(false);
		statusDialog.show();
	}

	@Override
	protected Object doInBackground(Object... args) {
        String result="";
		try {

			Log.i("SendMailTask", "About to instantiate GMail...");
			publishProgress("Processing input....");
			GMail androidEmail = new GMail(args[0].toString(),
					args[1].toString(), (List) args[2], args[3].toString(),
					args[4].toString());
			publishProgress("Preparing mail message....");
			androidEmail.createEmailMessage();
			publishProgress("Sending email....");
			androidEmail.sendEmail();
			publishProgress("Email Sent.");
            result="Mail Sent";
			Log.i("SendMailTask", "Mail Sent.");

		} catch (Exception e) {
			publishProgress(e.getMessage());
			Log.e("SendMailTask", e.getMessage(), e);
		}
		return result;
	}

	@Override
	public void onProgressUpdate(Object... values) {
		statusDialog.setMessage(values[0].toString());

	}

	@Override
	public void onPostExecute(Object result) {
        if(result.equals("Mail Sent"))
        {
            statusDialog.dismiss();
            Log.i("SendMailTask", result.toString());
            sendMailActivity.startActivity(new Intent(sendMailActivity.getApplicationContext(), Drawer.class));

        }
        else
        {
            statusDialog.dismiss();
        }

	}

}
