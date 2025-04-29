package com.example.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

public class DismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra("taskId", -1);
        if (taskId != -1) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(taskId);
            Toast.makeText(context, "Task reminder dismissed", Toast.LENGTH_SHORT).show();
        }
    }
}
