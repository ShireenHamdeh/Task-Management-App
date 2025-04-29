package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// We used RecyclerView for managing and recycling views efficiently
public class AllTasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    //To diffrientiate between two task , we used the task title and the sate to represent each task in the view
    // to know if the item in the list is date or task
    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_TASK = 1;

//list  holds both date (as String) and tasks (as Task objects).
    // date is the header , and it has list of tasks for that date
// This allows mixing different types of data in the same list.
    private final List<Object> tasksView;

    //The constructor of the class
    //input :Map of grouped tasks as input:
    //Keys: Dates as string  ("2024-12-20").
    //Values: Lists of Task objects for that date
    public AllTasksAdapter(Map<String, List<Task> > groupedTasks) {
        tasksView = new ArrayList<>();
        //loop through the map
        for (Map.Entry<String, List<Task>> listask : groupedTasks.entrySet()) {
            tasksView.add(listask.getKey()); // add the date as the header of the items list
            tasksView.addAll(listask.getValue()); // Add the tasks for that date
        }
    }

    @Override
    //decide whether an item is a date or a task
    public int getItemViewType(int position) {
        return (tasksView.get(position) instanceof String) ? VIEW_TYPE_DATE : VIEW_TYPE_TASK;
    }

    @NonNull
    @Override
    //"inflating" refers to the process of converting an XML layout file (like item_date_header.xml or item_task.xml) into a corresponding View object that can be displayed on the screen.
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATE) {
            // Inflate date header view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new DateViewHolder(view);
        } else {
            // Inflate task view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view, parent.getContext()); // Return TaskViewHolder with context
        }
    }

   //"bind" means associating data with the views in a ViewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof DateViewHolder) {
            // Handle date headers
            String date = (String) tasksView.get(position);
            ((DateViewHolder) holder).dateTextView.setText(date);
        }

        else if (holder instanceof TaskViewHolder) {
            // Handle tasks
            Task task = (Task) tasksView.get(position);
            TaskViewHolder taskHolder = (TaskViewHolder) holder;

            // Bind task data to the views
            taskHolder.taskTitle.setText(task.getTitle());
            taskHolder.taskDueDate.setText(task.getDueDate());
            taskHolder.taskPriority.setText(task.getPriority());


            // If we click on the item , it must go to task details activity
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), TaskDetailsActivity.class);
                intent.putExtra("task", task); // Pass the task object to the new activity
                holder.itemView.getContext().startActivity(intent);
            });
        }
    }



    @Override
    public int getItemCount() {
        return tasksView.size();
    }


    //this is to view the date
    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }




    // TaskViewHolder class that handles task views
    //this is view the tasks (title , date,priority)
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskDueDate;
        TextView taskPriority;
        Context context;

        public TaskViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            taskPriority = itemView.findViewById(R.id.taskPriority);
        }
    }
}
