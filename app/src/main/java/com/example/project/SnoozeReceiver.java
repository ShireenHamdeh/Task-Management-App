package com.example.project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Calendar;
import android.util.Log;

public class SnoozeReceiver extends BroadcastReceiver {
    private static final String MY_CHANNEL_ID = "TaskReminderChannel";
    int notificationId;

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra("taskTitle");
        notificationId = taskTitle.hashCode();

        // Show the primary notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle("Task Reminder")
                .setContentText("Reminder for: " + taskTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
        Log.d("ReminderBroadcast", "Notification shown for task: " + taskTitle);
    }
}