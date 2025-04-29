package com.example.project;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskDetailsActivity extends AppCompatActivity {

    private DataBaseHelper dataBaseHelper;
    private Task task;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        //get the task from database
        dataBaseHelper = new DataBaseHelper(this);
        task = (Task) getIntent().getSerializableExtra("task");

        TaskCondition(task);

        //Initialize all componetnts
        TextView Title = findViewById(R.id.Title);
        TextView Description = findViewById(R.id.Description);
        TextView DueDate = findViewById(R.id.DueDate);
        TextView Priority = findViewById(R.id.Priority);
        TextView DueTime = findViewById(R.id.DueTime);
        TextView IsCompleted = findViewById(R.id.IsCompleted);
        Button editButton = findViewById(R.id.EditTask);
        Button shareByEmailButton = findViewById(R.id.ShareTask);
        Button markTaskAsCompleteButton = findViewById(R.id.MarkAsComplete);
        Button deleteButton = findViewById(R.id.DeleteTask);


        if (task != null) {
            putTaskDataIntoFeilds(task, Title, Description, DueDate, Priority, DueTime, IsCompleted);
        } else {
            Toast.makeText(this, "Task details are not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        editButton.setOnClickListener(v -> {
            //go to edit task page
            Intent intent = new Intent(TaskDetailsActivity.this, EditTaskActivity.class);
            intent.putExtra("task", task);
            startActivityForResult(intent, 100);
        });

        deleteButton.setOnClickListener(v -> {
            if (task != null) {
                //delete the task from the database
                boolean isDeleted = dataBaseHelper.deleteTask(dataBaseHelper.getTaskId(task.getTitle(), task.getDueDate(), task.getDueTime()));
               //check if its deleted or not
                if (isDeleted) {
                    Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                    //go to appropriate intent
                    setResult(RESULT_OK, intent);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "deleting task went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        markTaskAsCompleteButton.setOnClickListener(v -> {
            //if the task is not completed make it as completed
            if (task != null && !task.isCompleted()) {
                task.setCompleted(true);
                //update the state of the task in the database
                boolean TaskisUpdated = dataBaseHelper.markTaskAsComplete(dataBaseHelper.getTaskId(task.getTitle(), task.getDueDate(), task.getDueTime()));

                if (TaskisUpdated) {
                    //inform the user that the action went well
                    IsCompleted.setText("Completed");
                    Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show();
                   //if all tasks are completed
                    if (CheckCompletionOfTheTask()) {
                        Toast.makeText(this, "Congratulations! All tasks for today are completed!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to mark task as completed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Task is already completed", Toast.LENGTH_SHORT).show();
            }
            //go to appropriate intent based on the task condition
            setResult(RESULT_OK, intent);
            startActivity(intent);
            finish();
        });

        shareByEmailButton.setOnClickListener(v -> {
            if (task != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Task: " + task.getTitle() + "\nDescription: " + task.getDescription());
                startActivity(Intent.createChooser(shareIntent, "Share Task via"));
            }
        });
    }

    public void TaskCondition(Task task) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = dateFormat.format(new Date());

        if (task.getDueDate().equals(todayDate)) {
            intent = new Intent(this,HomeActivity.class);
        }
        else if (task.isCompleted()) {
            intent = new Intent(this,CompletedTasksActivity.class);
        }
        else{
            intent = new Intent(this,AllTasksActivity.class);
        }
    }

    //this is to check the accuracy of the function , and go to the new intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if everything went well
        if (requestCode == 100 && resultCode == RESULT_OK) {
//retrieve a Task object passed as a Serializable extra through an Intent
            task = (Task) data.getSerializableExtra("updatedTask");
            if (task != null) {

                putTaskDataIntoFeilds(task, findViewById(R.id.Title), findViewById(R.id.Description), findViewById(R.id.DueDate), findViewById(R.id.Priority), findViewById(R.id.DueTime), findViewById(R.id.IsCompleted));
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
        }
    }

    private void putTaskDataIntoFeilds(Task task, TextView taskTitle, TextView taskDescription, TextView taskDueDate, TextView taskPriority, TextView taskDueTime, TextView taskIsCompleted) {
        taskTitle.setText(task.getTitle());
        taskDescription.setText(task.getDescription());
        taskDueDate.setText(task.getDueDate());
        taskPriority.setText(task.getPriority());
        taskDueTime.setText(task.getDueTime());
        taskIsCompleted.setText(task.isCompleted() ? "Completed" : "Not Completed");
    }

    private boolean CheckCompletionOfTheTask() {
        List<Task> Tasks = dataBaseHelper.getTodaysTasks(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        for (Task task : Tasks) {
            if (!task.isCompleted()) {
                return false;
            }
        }
        return true;
    }
}
