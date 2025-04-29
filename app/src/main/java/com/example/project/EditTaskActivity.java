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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity {

    private EditText taskTitle, taskDescription;
    private Button dueDateButton, dueTimeButton, saveButton, cancelButton, editReminderDate, editReminderTime;
    private Spinner prioritySpinner, snoozeIntervalSpinner;
    private Switch completionStatusSwitch;

    private int selectedYear, selectedMonth, selectedDay;
    private int selectedHour, selectedMinute;
    private static final String MY_CHANNEL_ID = "TaskReminderChannel";
    private DataBaseHelper dbHelper;
    private Task task;
    private Task updatedTask;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // initialize all components
        taskTitle = findViewById(R.id.editTaskTitle);
        taskDescription = findViewById(R.id.editTaskDescription);
        dueDateButton = findViewById(R.id.editDueDateButton);
        dueTimeButton = findViewById(R.id.editDueTimeButton);
        saveButton = findViewById(R.id.saveEditButton);
        cancelButton = findViewById(R.id.cancelEditButton);
        prioritySpinner = findViewById(R.id.editPrioritySpinner);
        completionStatusSwitch = findViewById(R.id.editCompletionStatusSwitch);
        editReminderDate = findViewById(R.id.editReminderDateButton);
        editReminderTime = findViewById(R.id.editReminderTimeButton);
        snoozeIntervalSpinner = findViewById(R.id.snoozeIntervalSpinner);

        // Initialize Database Helper
        dbHelper = new DataBaseHelper(this);
        //this is the spinner for the snooze time option
        setPrioritySpinner(snoozeIntervalSpinner, R.array.snooze_intervals);

        //initialize priority spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.priority_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        // Initialize calendar values for the clock
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendar.get(Calendar.MINUTE);

        // Get the task object passed from the previous activity
        //it might come from all tasks , or todays activity or completed tasks
        task = (Task) getIntent().getSerializableExtra("task");
        if (task != null) {
            putSavedDataToTheFields(task, adapter);
        } else {
            Toast.makeText(this, "Error: There is no tasks", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

       //this is to simplify choosing  the date of the deadline
        dueDateButton.setOnClickListener(v -> showDatePickerDialog(dueDateButton));
        //this is to simplify choosing  the time of the deadline
        dueTimeButton.setOnClickListener(v -> showTimePickerDialog(dueTimeButton));
        //this is to simplify choosing  the date of the reminder
        editReminderDate.setOnClickListener(v -> showDatePickerDialog(editReminderDate));
        //this is to simplify choosing  the time of the reminder
        editReminderTime.setOnClickListener(v -> showTimePickerDialog(editReminderTime));
        //save all task deatials
        saveButton.setOnClickListener(v -> saveTaskAfterEditing());
        //this is to terminate the function
        cancelButton.setOnClickListener(v -> finish());

        createNotificationChannel();
    }


    //set the adapter for priorityspinner
    private void setPrioritySpinner(Spinner spinner, int arrayResourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                arrayResourceId,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //attach the apapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void putSavedDataToTheFields(Task task, ArrayAdapter<CharSequence> adapter) {
        taskTitle.setText(task.getTitle());
        taskDescription.setText(task.getDescription());
        completionStatusSwitch.setChecked(task.isCompleted());

        // map betwwrn pririty and number
        int priorityPosition = adapter.getPosition(task.getPriority());
        if (priorityPosition >= 0) {
            prioritySpinner.setSelection(priorityPosition);
        }

        // Set the date and time for the deadline of the task
        dueDateButton.setText(task.getDueDate());
        dueTimeButton.setText(task.getDueTime());

        //setting the time and date for the notification
        //and there must be a data for these feilds
        if (task.getReminderDate() != null && !task.getReminderDate().isEmpty()) {
            editReminderDate.setText(task.getReminderDate());
        } else {
            editReminderDate.setText("Select Date");
        }

        if (task.getReminderTime() != null && !task.getReminderTime().isEmpty()) {
            editReminderTime.setText(task.getReminderTime());
        } else {
            editReminderTime.setText("Select Time");
        }
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

    private void saveTaskAfterEditing() {
        String title = taskTitle.getText().toString().trim();
        String description = taskDescription.getText().toString().trim();
        String priority = prioritySpinner.getSelectedItem().toString();
        boolean isCompleted = completionStatusSwitch.isChecked();
        String dueDate = dueDateButton.getText().toString();
        String dueTime = dueTimeButton.getText().toString();
        String reminderDate = editReminderDate.getText().toString();
        String reminderTime = editReminderTime.getText().toString();
        int snoozeTime = setSnoozeTime();

        //toast to fill all data
        if (title.isEmpty() || description.isEmpty() || dueDate.equals("Select Date") || dueTime.equals("Select Time")) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        //create new task
        updatedTask = new Task(title, description, isCompleted, priority, dueDate, dueTime, task.getUserEmail(), reminderDate, reminderTime, snoozeTime);
        //update the changed data of the task
        boolean isUpdated = dbHelper.editTask(task, updatedTask);

        if (isUpdated) {
            scheduleNotification(updatedTask);
            Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show();
            TaskCondition(updatedTask);
            //move the intent to its appropriate place
            intent.putExtra("updatedTask", updatedTask);
            setResult(RESULT_OK, intent);
            finish();

        } else {
            Toast.makeText(this, "Error updating task", Toast.LENGTH_SHORT).show();
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

    private void TaskCondition(Task task) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = dateFormat.format(new Date());
        //if the task date is today, move it to hoeactivity(todays tasks)
        if (task.getDueDate().equals(todayDate)) {
            intent = new Intent(this, HomeActivity.class);
            //if the task date is today, move it to completed task page(todays tasks)
        } else if (task.isCompleted()) {
            intent = new Intent(this, CompletedTasksActivity.class);
        } else {
            //move it to all tasks activity
            intent = new Intent(this, AllTasksActivity.class);
        }
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
//PendingIntent allows another application (or a different part of the Android system, like the notification manager or alarm manager) to perform an action on your app's behalf, often at a later time.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                //data to show
                dbHelper.getTaskId(task.getTitle(), task.getDueDate(), task.getDueTime()),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        // use the alarm of the phone
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("NewTaskActivity", "Notification scheduled for task: " + task.getTitle() + " at " + calendar.getTime().toString());
        }
    }
}
