package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Project.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PASSWORD = "password";

    private static final String TABLE_TASKS = "tasks";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String IS_COMPLETED = "isCompleted";
    private static final String PRIORITY = "priority";
    private static final String DUE_DATE = "dueDate";
    private static final String DUE_TIME = "dueTime";
    private static final String REMINDER_DATE = "reminderDate";
    private static final String REMINDER_TIME = "reminderTime";
    private static final String SNOOZE_TIME = "snoozedTime";
    private static final String USER_EMAIL = "userEmail";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + EMAIL + " TEXT PRIMARY KEY, "
                + FIRST_NAME + " TEXT, "
                + LAST_NAME + " TEXT, "
                + PASSWORD + " TEXT);";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " TEXT, "
                + DESCRIPTION + " TEXT, "
                + IS_COMPLETED + " INTEGER, "
                + PRIORITY + " TEXT, "
                + DUE_DATE + " DATE, "
                + DUE_TIME + " TEXT, "
                + REMINDER_DATE + " TEXT, "
                + REMINDER_TIME + " TEXT, "
                + SNOOZE_TIME + " INTEGER, "
                + USER_EMAIL + " TEXT);";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean addUser(String email, String firstName, String lastName, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EMAIL, email);
        values.put(FIRST_NAME, firstName);
        values.put(LAST_NAME, lastName);
        values.put(PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, task.getTitle());
        values.put(DESCRIPTION, task.getDescription());
        values.put(IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(PRIORITY, task.getPriority());
        values.put(DUE_DATE, task.getDueDate());
        values.put(DUE_TIME, task.getDueTime());
        values.put(REMINDER_DATE, task.getReminderDate());
        values.put(REMINDER_TIME, task.getReminderTime());
        values.put(SNOOZE_TIME, task.getSnoozedTime());
        values.put(USER_EMAIL, task.getUserEmail());

        long result = db.insert(TABLE_TASKS, null, values);
        db.close();
        return result != -1;
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Task task = new Task();
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(IS_COMPLETED)) == 1);
                task.setPriority(cursor.getString(cursor.getColumnIndexOrThrow(PRIORITY)));
                task.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow(DUE_DATE)));
                task.setDueTime(cursor.getString(cursor.getColumnIndexOrThrow(DUE_TIME)));
                task.setReminderDate(cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_DATE)));
                task.setReminderTime(cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_TIME)));
                task.setSnoozedTime(cursor.getColumnIndexOrThrow(SNOOZE_TIME));
                task.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(USER_EMAIL)));

                taskList.add(task);
            }
            cursor.close();
        }

        db.close();
        return taskList;
    }

    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_TASKS, ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
        return rowsDeleted > 0;
    }

    public boolean editTask(Task oldTask, Task newTask) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, newTask.getTitle());
        values.put(DESCRIPTION, newTask.getDescription());
        values.put(PRIORITY, newTask.getPriority());
        values.put(DUE_DATE, newTask.getDueDate());
        values.put(DUE_TIME, newTask.getDueTime());
        values.put(REMINDER_DATE, newTask.getReminderDate());
        values.put(REMINDER_TIME, newTask.getReminderTime());
        values.put(SNOOZE_TIME, newTask.getSnoozedTime());
        values.put(IS_COMPLETED, newTask.isCompleted() ? 1 : 0);

        int rowsUpdated = db.update(TABLE_TASKS, values,
                TITLE + " = ? AND " + DUE_DATE + " = ? AND " + DUE_TIME + " = ?",
                new String[]{oldTask.getTitle(), oldTask.getDueDate(), oldTask.getDueTime()});

        db.close();
        return rowsUpdated > 0;
    }

    public boolean markTaskAsComplete(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IS_COMPLETED, 1);

        int rowsUpdated = db.update(TABLE_TASKS, values, ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
        return rowsUpdated > 0;
    }

    public List<Task> searchTasks(String keyword, String startDate, String endDate) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM tasks WHERE 1=1");
        List<String> argsList = new ArrayList<>();

        if (!keyword.isEmpty()) {
            queryBuilder.append(" AND (title LIKE ? OR description LIKE ?)");
            argsList.add("%" + keyword + "%");
            argsList.add("%" + keyword + "%");
        }
        if (!startDate.isEmpty()) {
            queryBuilder.append(" AND dueDate >= ?");
            argsList.add(startDate);
        }
        if (!endDate.isEmpty()) {
            queryBuilder.append(" AND dueDate <= ?");
            argsList.add(endDate);
        }
        String[] args = argsList.toArray(new String[0]);
        Cursor cursor = db.rawQuery(queryBuilder.toString(), args);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(IS_COMPLETED)) == 1);
                task.setPriority(cursor.getString(cursor.getColumnIndexOrThrow(PRIORITY)));
                task.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow(DUE_DATE)));
                task.setDueTime(cursor.getString(cursor.getColumnIndexOrThrow(DUE_TIME)));
                task.setReminderDate(cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_DATE)));
                task.setReminderTime(cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_TIME)));
                task.setSnoozedTime(cursor.getColumnIndexOrThrow(SNOOZE_TIME));
                task.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(USER_EMAIL)));

                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

    public int getTaskId(String title, String dueDate, String dueTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        int taskId = -1; 
        
        String query = "SELECT " + ID + " FROM " + TABLE_TASKS +
                " WHERE " + TITLE + " = ? AND " + DUE_DATE + " = ? AND " + DUE_TIME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{title, dueDate, dueTime});

        if (cursor != null && cursor.moveToFirst()) {
            taskId = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            cursor.close();
        }

        db.close();
        return taskId; 
    }

    public List<Task> getCompletedTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS + " WHERE " + IS_COMPLETED + " = 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Task task = new Task();
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(IS_COMPLETED)) == 1);
                task.setPriority(cursor.getString(cursor.getColumnIndexOrThrow(PRIORITY)));
                task.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow(DUE_DATE)));
                task.setDueTime(cursor.getString(cursor.getColumnIndexOrThrow(DUE_TIME)));
                task.setReminderDate(cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_DATE)));
                task.setReminderTime(cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_TIME)));
                task.setSnoozedTime(cursor.getColumnIndexOrThrow(SNOOZE_TIME));
                task.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(USER_EMAIL)));

                taskList.add(task);
            }
            cursor.close();
        }

        db.close();
        return taskList;
    }

    public List<Task> getTodaysTasks(String todayDate) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TASKS + " WHERE " + DUE_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{todayDate});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Task task = new Task();
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(IS_COMPLETED)) == 1);
                task.setPriority(cursor.getString(cursor.getColumnIndexOrThrow(PRIORITY)));
                task.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow(DUE_DATE)));
                task.setDueTime(cursor.getString(cursor.getColumnIndexOrThrow(DUE_TIME)));
                task.setReminderDate(cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_DATE)));
                task.setReminderTime(cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_TIME)));
                task.setSnoozedTime(cursor.getColumnIndexOrThrow(SNOOZE_TIME));
                task.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(USER_EMAIL)));

                taskList.add(task);
            }
            cursor.close();
        }

        db.close();
        return taskList;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + FIRST_NAME + ", " + LAST_NAME + " FROM " + TABLE_USERS + " WHERE " + EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String userName = null;

        if (cursor != null && cursor.moveToFirst()) {
            int firstNameIndex = cursor.getColumnIndex(FIRST_NAME);
            int lastNameIndex = cursor.getColumnIndex(LAST_NAME);
            
            if (firstNameIndex >= 0 && lastNameIndex >= 0) {
                String firstName = cursor.getString(firstNameIndex);
                String lastName = cursor.getString(lastNameIndex);
                userName = firstName + " " + lastName; 
            } else {
                
                Log.e("Database", "Column names not found in the query result.");
            }

            cursor.close();
        } else {
            Log.e("Database", "No user found with email: " + email);
        }

        db.close();
        return userName;
    }
    public boolean isEmailUsed(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean emailExists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return emailExists;
    }

    public boolean updateEmail(String oldEmail, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EMAIL, newEmail);

        int rowsAffected = db.update(TABLE_USERS, values, EMAIL + " = ?", new String[]{oldEmail});
        db.close();
        return rowsAffected > 0;
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSWORD, newPassword);

        int rowsAffected = db.update(TABLE_USERS, values, EMAIL + " = ?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + EMAIL + " = ? AND " + PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    public boolean isTaskStored(String title, String dueDate, String dueTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE "
                + TITLE + " = ? AND "
                + DUE_DATE + " = ? AND "
                + DUE_TIME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{title, dueDate, dueTime});

        boolean exists = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                exists = cursor.getInt(0) > 0;
            }
            cursor.close();
        }
        db.close();
        return exists;
    }
}
