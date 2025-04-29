package com.example.project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GetTasks extends AppCompatActivity {


    private TaskAdapter taskAdapter;
    private DataBaseHelper dbHelper;
    private RecyclerView view;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_tasks);

        email = getIntent().getStringExtra("email");
        dbHelper = new DataBaseHelper(this);
        view = findViewById(R.id.recyclerView);
        view.setLayoutManager(new LinearLayoutManager(this));


        ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(this);
        connectionAsyncTask.execute("https://mocki.io/v1/05959841-09d8-4d49-a0e8-6cd0d4e75c13");

    }

    public void saveToDataBase(List<Task> tasks) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        for (Task task : tasks) {
            if(!dataBaseHelper.isTaskStored(task.getTitle(), task.getDueDate(), task.getDueTime())){
                task.setUserEmail(email);
                scheduleNotification(task);
                dataBaseHelper.addTask(task);
            }
        }
    }

    private void scheduleNotification(Task task) {
        try {
            String[] dateParts = task.getReminderDate().split("/"); // Assuming format is "dd/MM/yyyy"
            String[] timeParts = task.getReminderTime().split(":"); // Assuming format is "HH:mm"

            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Calendar months are 0-based
            int year = Integer.parseInt(dateParts[2]);
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, 0); // Include seconds as 0

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                Toast.makeText(this, "Reminder time is in the past!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, ReminderBroadcast.class);
            intent.putExtra("taskTitle", task.getTitle());
            intent.putExtra("snoozeInterval", task.getSnoozedTime());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    dbHelper.getTaskId(task.getTitle(), task.getDueDate(), task.getDueTime()),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Log.d("NewTaskActivity", "Notification scheduled for task: " + task.getTitle() + " at " + calendar.getTime().toString());
            }
        } catch (Exception e) {
            Log.e("NewTaskActivity", "Error parsing reminder date or time", e);
            Toast.makeText(this, "Invalid reminder date or time format!", Toast.LENGTH_SHORT).show();
        }
    }

    public void setTasksToRecyclerView(List<Task> tasks) {
        if (tasks != null && !tasks.isEmpty()) {
            taskAdapter = new TaskAdapter(tasks);
            view.setAdapter(taskAdapter);
        } else {
            Toast.makeText(this, "No tasks found!", Toast.LENGTH_SHORT).show();
        }
    }
}
