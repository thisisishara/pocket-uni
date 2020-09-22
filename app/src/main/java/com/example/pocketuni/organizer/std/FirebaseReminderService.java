package com.example.pocketuni.organizer.std;

import android.app.Service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseReminderService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().isEmpty()) {
            Map<String, String> data = remoteMessage.getData();
            //remoteMessage
            //FCM Notifications not implemented
            //Refer ReminderBroadcastReceiver for local notification managers.
        }
    }
}
