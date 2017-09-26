package com.learning_app.user.chathamkulam.PaymentGateway;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.learning_app.user.chathamkulam.PaymentGateway.utility.AvenuesParams;
import com.learning_app.user.chathamkulam.PaymentGateway.utility.Constants;
import com.learning_app.user.chathamkulam.PaymentGateway.utility.RSAUtility;
import com.learning_app.user.chathamkulam.PaymentGateway.utility.ServiceHandler;
import com.learning_app.user.chathamkulam.PaymentGateway.utility.ServiceUtility;
import com.learning_app.user.chathamkulam.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.learning_app.user.chathamkulam.PaymentGateway.Constants.ACCESS_CODE;
import static com.learning_app.user.chathamkulam.PaymentGateway.Constants.RSA_KEY_API;

public class WebViewActivity extends Activity {
    Intent mainIntent;
    String html, encVal;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_webview);
        mainIntent = getIntent();

        // Calling async task to get display content
        new RenderView().execute();
    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class RenderView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            dialog = new ProgressDialog(WebViewActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(AvenuesParams.KEY_ACCESS_CODE, ACCESS_CODE));
            params.add(new BasicNameValuePair(AvenuesParams.KEY_ORDER_ID, mainIntent.getStringExtra(AvenuesParams.KEY_ORDER_ID)));

            String vResponse = sh.makeServiceCall(RSA_KEY_API, ServiceHandler.POST, params);

            Log.d("Response", params.toString()+" Res "+ vResponse);

            if (!ServiceUtility.chkNull(vResponse).equals("")
                    && !ServiceUtility.chkNull(vResponse).toString().contains("ERROR")) {
                StringBuilder vEncVal = new StringBuilder("");
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.KEY_AMOUNT, mainIntent.getStringExtra(AvenuesParams.KEY_AMOUNT)));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.KEY_CURRENCY, mainIntent.getStringExtra(AvenuesParams.KEY_CURRENCY)));
                encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), vResponse);
            }

            return null;
        }

        @SuppressLint("AddJavascriptInterface")
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (dialog.isShowing())
                dialog.dismiss();

            @SuppressWarnings("unused")
            class MyJavaScriptInterface {
                @JavascriptInterface
                public void processHTML(String html) {
                    Log.d("##Result","processHTML");
                    // process the html as needed by the app
                    String status = null;
                    if (html.contains("Failure")) {
                        status = "Transaction Declined!";
                    } else if (html.contains("Success")) {
                        status = "Transaction Successful!";
                    } else if (html.contains("Aborted")) {
                        status = "Transaction Cancelled!";
                    } else {
                        status = "Status Not Known!";
                    }
                    //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                    intent.putExtra("transStatus", status);
                    startActivity(intent);
                }
            }

            final WebView webview = (WebView) findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(webview, url);
                    System.out.print("URL"+url);
                    if (url.contains("/ccavResponseHandler.php")) {
                        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                }
            });

			/* An instance of this class will be registered as a JavaScript interface */
            StringBuilder params = new StringBuilder();
            params.append(ServiceUtility.addToPostParams(AvenuesParams.KEY_ACCESS_CODE, mainIntent.getStringExtra(AvenuesParams.KEY_ACCESS_CODE)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.KEY_MERCHANT_ID, mainIntent.getStringExtra(AvenuesParams.KEY_MERCHANT_ID)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.KEY_ORDER_ID, mainIntent.getStringExtra(AvenuesParams.KEY_ORDER_ID)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.KEY_REDIRECT_URL, mainIntent.getStringExtra(AvenuesParams.KEY_REDIRECT_URL)));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.KEY_CANCEL_URL, mainIntent.getStringExtra(AvenuesParams.KEY_CANCEL_URL)));

            try {
                Log.d("valuesTest", encVal);
                params.append(ServiceUtility.addToPostParams(AvenuesParams.KEY_ENC_VAL, URLEncoder.encode(encVal, "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String vPostParams = params.substring(0, params.length() - 1);
            Log.d("vPostParams", vPostParams);
            try {
                webview.postUrl(Constants.TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
            } catch (Exception e) {
                showToast("Exception occured while opening webview.");
            }
        }
    }
} 