package com.askme.comfort.chat;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Amit on 10/30/2016.
 */

public class Chat_Instance_Service extends FirebaseInstanceIdService {
    private static final String TAG = "Chat_Instance_Service";
    private static final String ENGAGE_TOPIC = "engaged";

    @Override
    public void onTokenRefresh() {
        String Token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token : " + Token);
        FirebaseMessaging.getInstance().subscribeToTopic(ENGAGE_TOPIC);
    }
}
