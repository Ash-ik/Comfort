package com.askme.comfort.chat;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Amit on 10/29/2016.
 */

public class Chat_Messanging_Service extends FirebaseMessagingService {
    private static final String TAG = "Chat_Messanging_Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Message Service ID : " + remoteMessage.getMessageId());
        Log.d(TAG, "Notification Message : " + remoteMessage.getNotification());
        Log.d(TAG, "Data Message : " + remoteMessage.getData());
    }
}
