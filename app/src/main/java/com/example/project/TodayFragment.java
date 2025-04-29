package com.example.project;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TodayFragment extends Fragment {
    //RecyclerView used to display a large set of data in a scrollable list or grid format.
    // It is an advanced version of ListView and GridView,
    // providing more flexibility and better performance.
    private RecyclerView ViewTasks;
    //TaskAdapter is a class that bridges data (a list of tasks) and the UI components of a RecyclerView.
    // It tells the RecyclerView:
            //What data to display.
            //How to display it.
            //What happens when a user interacts with the items.
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private Spinner sortSpinner;
    private LinearLayout sortContainer;

    public TodayFragment() {
        // Required empty public constructor
    }

    public static TodayFragment newInstance() {
        return new TodayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        //RecycleView
        ViewTasks = view.findViewById(R.id.ViewTasks);
        ViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the task list and adapter
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        ViewTasks.setAdapter(taskAdapter);

        //initializes the spinner of how to list data
        sortContainer = view.findViewById(R.id.sortContainer);
        sortSpinner = view.findViewById(R.id.SpinnerByPriority);
        setupSortSpinner(sortSpinner);



        // Load today's tasks
        loadTodayTasks(view);

        return view;
    }

    private void loadTodayTasks(View view) {
        //the format of todaytasks layout
        // Get today's date in "dd/MM/yyyy" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = dateFormat.format(new Date());


        // get todays tasks of the database
        DataBaseHelper dbHelper = new DataBaseHelper(getContext());
        List<Task> todaysTasks = dbHelper.getTodaysTasks(todayDate);


        // Separate completed and incomplete tasks
        List<Task> incompleteTasks = new ArrayList<>();
        boolean allCompleted = true;
//loop through all todays tasks
        if (todaysTasks != null) {
            for (Task a : todaysTasks) {
                if (!a.isCompleted()) {
                    incompleteTasks.add(a);
                    allCompleted = false;
                }
            }
        }

        // Get views
        TextView todayTitle = view.findViewById(R.id.todayTitle);
        TextView emptyView = view.findViewById(R.id.empty_view);
        //this is when the tasks are completes
        ImageView congratulationFrame = view.findViewById(R.id.congratulationFrame);

        if (todaysTasks == null || todaysTasks.isEmpty()) {
            // No tasks for today // so hide wverything and show empty view
            todayTitle.setVisibility(View.GONE); // Hide the title
            ViewTasks.setVisibility(View.GONE); // Hide RecyclerView
            emptyView.setVisibility(View.VISIBLE); // Show empty view
            congratulationFrame.setVisibility(View.GONE); // Hide congratulation animation
            sortSpinner.setVisibility(View.GONE); // Hide Spinner
            sortContainer.setVisibility(View.GONE);
        } else if (allCompleted) {
            //if  All tasks of today are completed
            //now show taost that informs the user that the task is completed
            //show congratulation animation and hidre everythig else
            todayTitle.setVisibility(View.GONE); // Hide the title
            ViewTasks.setVisibility(View.GONE); // Hide RecyclerView
            emptyView.setVisibility(View.GONE); // Hide empty view
            congratulationFrame.setVisibility(View.VISIBLE); // Show congratulation animation
            sortSpinner.setVisibility(View.GONE); // Hide Spinner
            sortContainer.setVisibility(View.GONE);

            // Start the animation
            congratulationFrame.post(() -> {
                AnimationDrawable animation = (AnimationDrawable) congratulationFrame.getBackground();
                animation.start();
            });

            // Display Toast message
            Toast.makeText(getContext(), "Congratulations! You've completed all tasks for today!", Toast.LENGTH_LONG).show();
        }
        else {
            // Display incomplete tasks
            //show everything
            todayTitle.setVisibility(View.VISIBLE); // Show the title
            ViewTasks.setVisibility(View.VISIBLE); // Show RecyclerView
            emptyView.setVisibility(View.GONE); // Hide empty view
            congratulationFrame.setVisibility(View.GONE); // Hide congratulation animation
            sortSpinner.setVisibility(View.VISIBLE); // Show Spinner
            sortContainer.setVisibility(View.VISIBLE);

            // Update task list and refresh adapter
            taskList.clear();
            taskList.addAll(incompleteTasks);
            taskAdapter.notifyDataSetChanged();
        }
        //i used it to track any errors
        Log.d("TodayFragment", "Spinner Visibility: " + sortSpinner.getVisibility());

    }


    //sets up a Spinner to allow the user to sort tasks
    //let the user choose between sorting by date or prioity

    private void setupSortSpinner(Spinner sortSpinner) {
        // Create sorting options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.sort_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ////Attaches the adapter to the spinner.
        sortSpinner.setAdapter(adapter);

        // Set sorting functionality
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // set option 1 to be Default order
                        loadTodayTasks(getView());
                        break;
                    case 1: //set option2 to be  Sort by priority (High to Low)
                        sortTasksByPriority();
                        break;
                }
            }
            //Called when no item is selected
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    //sort the tasks based on priority (High > Medium > Low)
    private void sortTasksByPriority() {
        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return getPriorityValue(task2.getPriority()) - getPriorityValue(task1.getPriority());
            }
        });

        // Refresh adapter
        taskAdapter.notifyDataSetChanged();
    }

    private int getPriorityValue(String priority) {
        switch (priority.toLowerCase()) {
            case "high":
                return 3;
            case "medium":
                return 2;
            case "low":
                return 1;
            default:
                return 0; // Default for unknown priorities
        }
    }
}
