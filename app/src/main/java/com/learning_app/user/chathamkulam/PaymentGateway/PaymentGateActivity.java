package com.learning_app.user.chathamkulam.PaymentGateway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.learning_app.user.chathamkulam.PaymentGateway.utility.AvenuesParams;
import com.learning_app.user.chathamkulam.PaymentGateway.utility.ServiceUtility;
import com.learning_app.user.chathamkulam.R;

public class PaymentGateActivity extends AppCompatActivity {
	
	private EditText accessCode, merchantId, currency, amount, orderId, rsaKeyUrl, redirectUrl, cancelUrl;
	private TextView txtOrderId,txtSubjectName,txtAmount,txtCurrency;
    private LinearLayout layoutMain,layoutRequired;

    String subjectName;

	private void init(){
		accessCode = (EditText) findViewById(R.id.accessCode);
		merchantId = (EditText) findViewById(R.id.merchantId);
		orderId  = (EditText) findViewById(R.id.orderId);
		currency = (EditText) findViewById(R.id.currency);
		amount = (EditText) findViewById(R.id.amount);
		rsaKeyUrl = (EditText) findViewById(R.id.rsaUrl);
		redirectUrl = (EditText) findViewById(R.id.redirectUrl);
		cancelUrl = (EditText) findViewById(R.id.cancelUrl);

        txtOrderId = (TextView)findViewById(R.id.txtOrderId);
        txtSubjectName = (TextView)findViewById(R.id.txtSubjectName);
        txtAmount = (TextView)findViewById(R.id.txtAmount);
        txtCurrency = (TextView)findViewById(R.id.txtCurrency);

        layoutMain = (LinearLayout)findViewById(R.id.layoutMain);
        layoutMain.setVisibility(View.GONE);
        layoutRequired = (LinearLayout)findViewById(R.id.layoutRequired);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initial_screen);
		init();
		
		//generating order number
		Integer randomNum = ServiceUtility.randInt(0, 9999999);
		orderId.setText(randomNum.toString());
        txtOrderId.setText(randomNum.toString());

        subjectName = getIntent().getStringExtra("key_subjectName");
        int amount = getIntent().getIntExtra("key_amount",0);
        txtSubjectName.setText(subjectName);
        txtAmount.setText(String.valueOf(amount));

	}

	public void onClick(View view) {
		//Mandatory parameters. Other parameters can be added if required.
		String vAccessCode = ServiceUtility.chkNull(accessCode.getText()).toString().trim();
		String vMerchantId = ServiceUtility.chkNull(merchantId.getText()).toString().trim();
		String vCurrency = ServiceUtility.chkNull(txtCurrency.getText()).toString().trim();
		String vAmount = ServiceUtility.chkNull(txtAmount.getText()).toString().trim();
		if(!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")){
			Intent intent = new Intent(this,WebViewActivity.class);
			intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(accessCode.getText()).toString().trim());
			intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchantId.getText()).toString().trim());
			intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(txtOrderId.getText()).toString().trim());
			intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(txtCurrency.getText()).toString().trim());
			intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(txtAmount.getText()).toString().trim());
			
			intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(redirectUrl.getText()).toString().trim());
			intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(cancelUrl.getText()).toString().trim());
			intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(rsaKeyUrl.getText()).toString().trim());

			startActivity(intent);
		}else{
			showToast("All parameters are mandatory.");
		}
	}
	
	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}
} 