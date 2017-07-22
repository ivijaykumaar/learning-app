package com.learning_app.user.chathamkulam;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by User on 7/7/2017.
 */

public class GcmIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {

        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        startService(intent);

        //send token to app server
    }
}
