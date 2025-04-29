package com.example.project;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class NewTaskActivity extends AppCompatActivity {

    private static final String MY_CHANNEL_ID = "TaskReminderChannel";

    private EditText taskTitle, taskDescription;
    private Button dueDateButton, dueTimeButton, saveTaskButton, reminderDateButton, reminderTimeButton;
    private Spinner prioritySpinner, snoozeIntervalSpinner;
    private Switch completionStatusSwitch;

    private int selectedYear, selectedMonth, selectedDay;
    private int selectedHour, selectedMinute;
    private String userEmail;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        // initialize all component of the page
        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        dueDateButton = findViewById(R.id.dueDateButton);
        dueTimeButton = findViewById(R.id.dueTimeButton);
        saveTaskButton = findViewById(R.id.saveButton);
        reminderDateButton = findViewById(R.id.ReminderDateButton);
        reminderTimeButton = findViewById(R.id.ReminderTimeButton);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        snoozeIntervalSpinner = findViewById(R.id.snoozeIntervalSpinner);
        completionStatusSwitch = findViewById(R.id.completionStatusSwitch);
        userEmail = getIntent().getStringExtra("email");

        // Initialize Database Helper
        dbHelper = new DataBaseHelper(this);

        //initialize  spinner based on time for snoozing
        setSpinner(snoozeIntervalSpinner, R.array.snooze_intervals);
        //initialize priority spinner based on priority
        setSpinner(prioritySpinner, R.array.priority_options);

        // Initialize Calendar values
        initializeCalendar();

        //this is to simplify choosing  the date of the deadline
        dueDateButton.setOnClickListener(v -> showDatePickerDialog(dueDateButton));
        //this is to simplify choosing  the time of the deadline
        dueTimeButton.setOnClickListener(v -> showTimePickerDialog(dueTimeButton));
        //this is to simplify choosing  the date of the reminder
        reminderDateButton.setOnClickListener(v -> showDatePickerDialog(reminderDateButton));
        //this is to simplify choosing  the time of the reminder
        reminderTimeButton.setOnClickListener(v -> showTimePickerDialog(reminderTimeButton));

        saveTaskButton.setOnClickListener(v -> saveTask());

        // Create notification channel
        createNotificationChannel();
    }
    //set the adapter for priorityspinner
    private void setSpinner(Spinner spinner, int arrayResourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                arrayResourceId,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void initializeCalendar() {
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendar.get(Calendar.MINUTE);
    }

    private void showDatePickerDialog(Button button) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedYear = year;
                    selectedMonth = month;
                    selectedDay = dayOfMonth;
                    button.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
                }, selectedYear, selectedMonth, selectedDay);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(Button button) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minute;
                    button.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                }, selectedHour, selectedMinute, true);
        timePickerDialog.show();
    }
    //set the snooze notification between  1 minute and 30 minutes
    private int setSnoozeTime() {
        String selectedInterval = snoozeIntervalSpinner.getSelectedItem().toString();
        switch (selectedInterval) {
            case "1 minute":
                return 60 * 1000;
            case "5 minutes":
                return 5 * 60 * 1000;
            case "10 minutes":
                return 10 * 60 * 1000;
            case "15 minutes":
                return 15 * 60 * 1000;
            case "30 minutes":
                return 30 * 60 * 1000;
            default:
                return 0;
        }
    }

    private void saveTask() {
        String title = taskTitle.getText().toString().trim();
        String description = taskDescription.getText().toString().trim();
        String priority = prioritySpinner.getSelectedItem().toString();
        boolean isCompleted = completionStatusSwitch.isChecked();
        String dueDate = dueDateButton.getText().toString();
        String dueTime = dueTimeButton.getText().toString();
        String reminderDate = reminderDateButton.getText().toString();
        String reminderTime = reminderTimeButton.getText().toString();

        int snoozeInterval = setSnoozeTime();

        //make sure all data are not empty
        if (title.isEmpty() || description.isEmpty() || dueDate.equals("Select Date") || dueTime.equals("Select Time") ||
                reminderDate.equals("Select Date") || reminderTime.equals("Select Time")) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        //create the task and add it to the database
        Task task = new Task(title, description, isCompleted, priority, dueDate, dueTime, userEmail, reminderDate, reminderTime, snoozeInterval);
        boolean isInserted = dbHelper.addTask(task);
        //check if the addition went well
        if (isInserted) {
            scheduleNotification(task);
            Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Error saving task", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    MY_CHANNEL_ID,
                    "Task Reminder Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for Task Reminder Notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void scheduleNotification(Task task) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);

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
    }
}
