package com.example.pocketuni.model;

import java.util.Date;

public class Reminder {
    private int day;
    private String endingTime;
    private Date reminderDateTime;
    private String  reminderItemId;
    private String startingTime;
    private String subjectCode;
    private String subjectName;
    private String  userID;

    public Reminder() {
    }

    public Reminder(int day, String endingTime, Date reminderDateTime, String reminderItemId, String startingTime, String subjectCode, String subjectName, String userID) {
        this.day = day;
        this.endingTime = endingTime;
        this.reminderDateTime = reminderDateTime;
        this.reminderItemId = reminderItemId;
        this.startingTime = startingTime;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.userID = userID;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public Date getReminderDateTime() {
        return reminderDateTime;
    }

    public void setReminderDateTime(Date reminderDateTime) {
        this.reminderDateTime = reminderDateTime;
    }

    public String getReminderItemId() {
        return reminderItemId;
    }

    public void setReminderItemId(String reminderItemId) {
        this.reminderItemId = reminderItemId;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
