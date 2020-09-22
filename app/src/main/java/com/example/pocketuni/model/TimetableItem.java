package com.example.pocketuni.model;

import com.example.pocketuni.util.DayEnum;

import java.util.Date;

public class TimetableItem {
    int day;
    Date endingDateTime;
    String endingTime;
    String itemId; //code+day+time
    String lecturerInCharge;
    String location;
    Date startingDateTime;
    String startingTime;
    String subjectCode;
    String subjectName;

    public TimetableItem() {
    }

    public TimetableItem(int day, Date endingDateTime, String endingTime, String itemId, String lecturerInCharge, String location, Date startingDateTime, String startingTime, String subjectCode, String subjectName) {
        this.day = day;
        this.endingDateTime = endingDateTime;
        this.endingTime = endingTime;
        this.itemId = itemId;
        this.lecturerInCharge = lecturerInCharge;
        this.location = location;
        this.startingDateTime = startingDateTime;
        this.startingTime = startingTime;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getLecturerInCharge() {
        return lecturerInCharge;
    }

    public void setLecturerInCharge(String lecturerInCharge) {
        this.lecturerInCharge = lecturerInCharge;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartingDateTime() {
        return startingDateTime;
    }

    public void setStartingDateTime(Date startingDateTime) {
        this.startingDateTime = startingDateTime;
    }

    public Date getEndingDateTime() {
        return endingDateTime;
    }

    public void setEndingDateTime(Date endingDateTime) {
        this.endingDateTime = endingDateTime;
    }
}
