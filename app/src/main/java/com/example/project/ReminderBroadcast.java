package com.example.project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.project.R;

import java.util.Calendar;

public class ReminderBroadcast extends BroadcastReceiver {
    private static final String MY_CHANNEL_ID = "TaskReminderChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String taskTitle = intent.getStringExtra("taskTitle");
        int snoozeRemaining = intent.getIntExtra("snoozeRemaining", 1);
        int snoozeInterval = intent.getIntExtra("snoozeInterval", 5 * 60 * 1000);

        int notificationId = taskTitle.hashCode();

        if ("ACTION_SNOOZE".equals(action)) {

            Calendar snoozeTime = Calendar.getInstance();
            snoozeTime.add(Calendar.MILLISECOND, snoozeInterval);

            Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
            snoozeIntent.putExtra("taskTitle", taskTitle);
            snoozeIntent.putExtra("snoozeRemaining", snoozeRemaining - 1);
            snoozeIntent.putExtra("snoozeInterval", snoozeInterval);
            snoozeIntent.setAction("ACTION_SNOOZE");

            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        snoozeTime.getTimeInMillis(),
                        snoozePendingIntent
                );
            }

            Log.d("ReminderBroadcast", "Snoozed for " + snoozeInterval / 1000 / 60 + " minutes.");
            Toast.makeText(context, "Snoozed for " + snoozeInterval / 1000 / 60 + " minutes.", Toast.LENGTH_SHORT).show();

            NotificationManagerCompat.from(context).cancel(notificationId);
        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MY_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_reminder)
                    .setContentTitle("Task Reminder")
                    .setContentText("Reminder for: " + taskTitle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_snooze, "Snooze", createSnoozeAction(context, taskTitle, snoozeRemaining, snoozeInterval));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, builder.build());
            Log.d("ReminderBroadcast", "Notification shown for task: " + taskTitle);
        }
    }


    private PendingIntent createSnoozeAction(Context context, String taskTitle, int snoozeRemaining, int snoozeInterval) {
        // Create Intent for snooze action
        Intent snoozeIntent = new Intent(context, ReminderBroadcast.class);
        snoozeIntent.putExtra("taskTitle", taskTitle);
        snoozeIntent.putExtra("snoozeRemaining", snoozeRemaining);
        snoozeIntent.putExtra("snoozeInterval", snoozeInterval);
        snoozeIntent.setAction("ACTION_SNOOZE");


        int snoozeId = taskTitle.hashCode();
        return PendingIntent.getBroadcast(
                context,
                snoozeId,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
