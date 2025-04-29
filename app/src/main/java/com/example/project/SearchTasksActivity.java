package com.example.project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SearchTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSearchTasks;
    private TextView startDateTextView, endDateTextView;
    private EditText searchTasks;
    private Button searchButton;
    private TaskAdapter taskAdapter;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tasks);

        //Intialize all components of the layout
        recyclerViewSearchTasks = findViewById(R.id.recyclerViewSearchTasks);
        startDateTextView = findViewById(R.id.start_date);
        endDateTextView = findViewById(R.id.end_date);
        searchTasks = findViewById(R.id.search_Tasks);
        searchButton = findViewById(R.id.searchButton);

        recyclerViewSearchTasks.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DataBaseHelper(this);

        //when i click on start date , show the circle date where i can choose the date easily
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePicker(startDateTextView);
            }
        });
//when i click on end date , show the circle date where i can choose the date easily
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(endDateTextView);
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the data from the textviews
                String startDate = startDateTextView.getText().toString();
                String endDate = endDateTextView.getText().toString();
                String TaskName = searchTasks.getText().toString();
                //searchtask by data name , startDate, endDate
                List<Task> ResultTasks = dbHelper.searchTasks(TaskName, startDate, endDate);
                //if there is no result
                if (ResultTasks.isEmpty()) {
                    Toast.makeText(SearchTasksActivity.this, "No tasks found", Toast.LENGTH_SHORT).show();
                }
                //show the result by taskapadter
                taskAdapter = new TaskAdapter(ResultTasks);
                // put the results in recycleview
                recyclerViewSearchTasks.setAdapter(taskAdapter);
            }
        });

    }


//this is to make choosing the date easy
    private void showDatePicker(final TextView dateTextView) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        dateTextView.setText(sdf.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

}