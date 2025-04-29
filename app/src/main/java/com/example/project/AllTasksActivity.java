package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AllTasksActivity extends AppCompatActivity {

    private RecyclerView AllTasksView;
    private TextView emptyView;
    private Spinner SpinnerByPriority;

   //groupedTasks :Map
    //Keys: Dates as string  ("2024-12-20").
    //Values: Lists of Task objects for that date

    private Map<String, List<Task>> groupedTasks = new TreeMap<>();
    private AllTasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        //this is the view where we will show the tasks
        AllTasksView = findViewById(R.id.AllTasksView);
        emptyView = findViewById(R.id.emptyView);
        //here we choose the priority of the tasks to be viewed as
        SpinnerByPriority = findViewById(R.id.SpinnerByPriority);
            //get tasks from database and group them by date
        List<Task> AllTasks = getTasksFromDtabase();
        groupedTasks = groupTasksByDate(AllTasks);


        if (groupedTasks.isEmpty()) {
            EmptyView_NoTasks();
        } else {

            setupTaskAdapter();
            setupSortSpinner(SpinnerByPriority);
        }
    }

    //functions

    private List<Task> getTasksFromDtabase() {
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        return dbHelper.getAllTasks();
    }

//group the tasks by date and return a map of it
    private Map<String, List<Task>> groupTasksByDate(List<Task> tasks) {
        Map<String, List<Task>> grouped = new TreeMap<>();
        //for each task in the tasks
        for (Task task : tasks) {
            //get the date
            String date = task.getDueDate();
            //explanation: If the key date exists, computeIfAbsent returns the existing list.
            //If the key date does not exist, it creates a new list, associates it with date, and returns it.
            grouped.computeIfAbsent(date, k -> new ArrayList<>()).add(task);
        }
        return grouped;
    }

    private void sortTasksByPriority(Map<String, List<Task>> tasks) {
        //reversed():Reverses the natural sorting order, making the highest-priority tasks appear first.
        for (List<Task> taskGroup : tasks.values()) {
            Collections.sort(taskGroup, Comparator.comparingInt(this::getPriorityValue).reversed());
        }
    }

    private int getPriorityValue(Task task) {
        //get the priority of each task
        switch (task.getPriority().toLowerCase()) {
            case "high": return 3;
            case "medium": return 2;
            case "low": return 1;
            default: return 0;
        }
    }

    private void setupTaskAdapter() {
        //dont show the emptyView, because there is tasks
        emptyView.setVisibility(View.GONE);
        AllTasksView.setVisibility(View.VISIBLE);

        //call the class AllTasksAdapter to show the tasks

        //The AllTasksAdapter class is a custom adapter for a RecyclerView in Android.
        // Its main purpose is to display a list of tasks grouped by date,
        // where each group contains a date header followed by its associated tasks.
        adapter = new AllTasksAdapter(groupedTasks);
        AllTasksView.setLayoutManager(new LinearLayoutManager(this));
        AllTasksView.setAdapter(adapter);
    }

    //sets up a Spinner to allow the user to sort tasks
    //let the user choose between sorting by date or prioity
    private void setupSortSpinner(Spinner PrioritySpinner) {
        // Create and Set Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                //This array contains the sorting options
                R.array.sort_options,
                android.R.layout.simple_spinner_item
        );
        //Sets the layout for the spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Attaches the adapter to the spinner.
        PrioritySpinner.setAdapter(adapter);

        PrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //Listens for user selection events on the spinner
            //Called when a user selects an item
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //If the first option ("Sort by Date") is selected
                if (position == 0) {
                    groupedTasks = groupTasksByDate(getTasksFromDtabase());
                } else if (position == 1) {
                    //Sorts the grouped tasks by priority using sortTasksByPriority(groupedTasks).
                    sortTasksByPriority(groupedTasks);
                }
                updateRecyclerView();
            }

            @Override
            //Called when no item is selected
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateRecyclerView() {
        adapter = new AllTasksAdapter(groupedTasks);
        AllTasksView.setAdapter(adapter);
    }
    //when there are no tasks in the database

    private void EmptyView_NoTasks() {
        emptyView.setVisibility(View.VISIBLE);
        AllTasksView.setVisibility(View.GONE);
    }
}
