package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CompletedTasksActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    private TextView emptyCompleted;
    private RecyclerView completedTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        dataBaseHelper = new DataBaseHelper(this);
        List<Task> Tasks = dataBaseHelper.getCompletedTasks();


        emptyCompleted = findViewById(R.id.emptyCompletedView);
        completedTasks = findViewById(R.id.CompletedTasks);


        if (Tasks.isEmpty()) {
            emptyCompleted.setVisibility(View.VISIBLE);
        } else {
            emptyCompleted.setVisibility(View.GONE);
            Map<String, List<Task>> groupedTasks = groupByDate(Tasks);
            AllTasksAdapter adapter = new AllTasksAdapter(groupedTasks);
            completedTasks.setLayoutManager(new LinearLayoutManager(this));
            completedTasks.setAdapter(adapter);
        }
    }

    private Map<String, List<Task>> groupByDate(List<Task> tasks) {
        Map<String, List<Task>> groupedTasksByDate = new TreeMap<>();
        for (Task task : tasks) {
            String date = task.getDueDate();
            if (!groupedTasksByDate.containsKey(date)) {
                groupedTasksByDate.put(date, new ArrayList<>());
            }
            groupedTasksByDate.get(date).add(task);
        }
        return groupedTasksByDate;
    }
}
