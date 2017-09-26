package com.learning_app.user.chathamkulam.PaymentGateway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.learning_app.user.chathamkulam.PaymentGateway.utility.AvenuesParams;
import com.learning_app.user.chathamkulam.PaymentGateway.utility.ServiceUtility;
import com.learning_app.user.chathamkulam.R;

import static com.learning_app.user.chathamkulam.PaymentGateway.Constants.ACCESS_CODE;
import static com.learning_app.user.chathamkulam.PaymentGateway.Constants.CANCEL_API;
import static com.learning_app.user.chathamkulam.PaymentGateway.Constants.CURRENCY;
import static com.learning_app.user.chathamkulam.PaymentGateway.Constants.MERCHANT_ID;
import static com.learning_app.user.chathamkulam.PaymentGateway.Constants.REDIRECT_API;
import static com.learning_app.user.chathamkulam.PaymentGateway.Constants.RSA_KEY_API;

public class PaymentGateActivity extends AppCompatActivity {

    String subjectName;
    private TextView txtOrderId, txtSubjectName, txtAmount, txtCurrency;

    private void init() {

        txtOrderId = (TextView) findViewById(R.id.txtOrderId);
        txtSubjectName = (TextView) findViewById(R.id.txtSubjectName);
        txtAmount = (TextView) findViewById(R.id.txtAmount);
        txtCurrency = (TextView) findViewById(R.id.txtCurrency);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);
        init();

        //generating order number
        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        txtOrderId.setText(randomNum.toString());

        subjectName = getIntent().getStringExtra("key_subjectName");
        double amount = getIntent().getDoubleExtra("key_amount", 0.00);
        txtSubjectName.setText(subjectName);
        txtAmount.setText(String.valueOf(amount));

    }

    public void onClick(View view) {
        //Mandatory parameters. Other parameters can be added if required.
        String vAccessCode = String.valueOf(ServiceUtility.chkNull(ACCESS_CODE.trim()));
        String vMerchantId = String.valueOf(ServiceUtility.chkNull(MERCHANT_ID.trim()));
        String vCurrency = String.valueOf(ServiceUtility.chkNull(CURRENCY.trim()));
        String vAmount = ServiceUtility.chkNull(txtAmount.getText()).toString().trim();
        String vOrderId = ServiceUtility.chkNull(txtOrderId.getText()).toString().trim();
        String vRedirectApi = String.valueOf(ServiceUtility.chkNull(REDIRECT_API.trim()));
        String vCancelApi = String.valueOf(ServiceUtility.chkNull(CANCEL_API.trim()));
        String vRsaKeyApi = String.valueOf(ServiceUtility.chkNull(RSA_KEY_API.trim()));

        if (!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")) {

            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(AvenuesParams.KEY_ACCESS_CODE, vAccessCode);
            intent.putExtra(AvenuesParams.KEY_MERCHANT_ID, vMerchantId);
            intent.putExtra(AvenuesParams.KEY_ORDER_ID, vOrderId);
            intent.putExtra(AvenuesParams.KEY_CURRENCY, vCurrency);
            intent.putExtra(AvenuesParams.KEY_AMOUNT, vAmount);
            intent.putExtra(AvenuesParams.KEY_REDIRECT_URL, vRedirectApi);
            intent.putExtra(AvenuesParams.KEY_CANCEL_URL, vCancelApi);
            intent.putExtra(AvenuesParams.KEY_RSA_KEY_URL, vRsaKeyApi);
            startActivity(intent);

        } else {
            showToast("All parameters are mandatory.");
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }
} 