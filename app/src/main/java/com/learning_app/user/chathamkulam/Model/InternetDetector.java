package com.learning_app.user.chathamkulam.Model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by User on 4/24/2017.
 */

public class InternetDetector {
    private Context mcontext;

    public InternetDetector(Context context) {
        this.mcontext = context;
    }

    public boolean checkMobileInternetConn() {
        ConnectivityManager connectivity = (ConnectivityManager) mcontext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo mobInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mobInfo != null || wifiInfo != null) {
                if (mobInfo.isConnected() || wifiInfo.isConnected() ) {
                    return true;
                }
            }
        }
        return false;
    }
}