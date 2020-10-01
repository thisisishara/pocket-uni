package com.example.pocketuni.organizer.std;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.pocketuni.R;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    private String reminderChannelID = "PUTTRNCH";
    private Context context;
    private String TAG = "BROADCASTRCVR";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        //show notification
        Log.d(TAG,"Notifying...");
        createNotificationChannel("Timetable Reminders","Sends Reminder Notifications for your current Semester Timetable if you have turned on reminders.", reminderChannelID);

        int requestCode = intent.getExtras().getInt("requestCode");
        String title = intent.getExtras().getString("title");
        String body = intent.getExtras().getString("body");

        Log.i(TAG, "Current request code: "+ requestCode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent openActivity = new Intent(context, OrganizerActivity.class);
            openActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, openActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            //NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setContentIntent(openActivity);
            Notification notification = null;

            notification = new Notification.Builder(context, reminderChannelID)
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.notif_small)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.notif_big))
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(body))
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(requestCode, notification);
        }
    }

    private void createNotificationChannel(CharSequence channelName, String channelDescription, String channelId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = channelName;
            String description = channelDescription;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
            Log.i(TAG, "Notification channel ("+channelName +"["+channelId+"] has been created.");
        }
    }
}
