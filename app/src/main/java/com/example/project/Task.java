package com.example.project;

import java.io.Serializable;

public class Task implements Serializable {
    private String title;
    private String description;
    private boolean isCompleted;
    private String priority;
    private String dueDate;
    private String dueTime;
    private String userEmail;
    private String reminderDate;
    private String reminderTime;
    private int snoozedTime;

    public Task() {
    }

    public Task(String title, String description, boolean isCompleted, String priority, String dueDate, String dueTime, String userEmail, String reminderDate, String reminderTime, int snoozedTime) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.priority = priority;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.userEmail = userEmail;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
        this.snoozedTime = snoozedTime;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public int getSnoozedTime() {
        return snoozedTime;
    }

    public void setSnoozedTime(int snoozedTime) {
        this.snoozedTime = snoozedTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isCompleted=" + isCompleted +
                ", priority='" + priority + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", dueTime='" + dueTime + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", reminderDate='" + reminderDate + '\'' +
                ", reminderTime='" + reminderTime + '\'' +
                ", snoozedTime=" + snoozedTime +
                '}';
    }
}
