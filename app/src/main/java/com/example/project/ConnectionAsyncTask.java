package com.example.project;

import android.os.AsyncTask;
import java.util.List;

public class ConnectionAsyncTask extends AsyncTask<String, Void, List<Task>> {

    private final GetTasks taskActivity;

    public ConnectionAsyncTask(GetTasks activity) {
        this.taskActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Task> doInBackground(String... params) {
        String jsonData = HttpManager.getData(params[0]);
        return TaskJsonParser.getObjectFromJson(jsonData);
    }

    @Override
    protected void onPostExecute(List<Task> tasks) {
        super.onPostExecute(tasks);
        taskActivity.setTasksToRecyclerView(tasks);
        taskActivity.saveToDataBase(tasks);
    }
}
