package com.example.pocketuni.model;

import com.example.pocketuni.util.DayEnum;

public class TimetableItem {
    String timetableName;
    String itemId; //code+time
    String subjectCode;
    String subjectName;
    String startingTime;
    String endingTime;
    DayEnum day;
    String lecturerInCharge;
    String location;

    public TimetableItem() {
    }

    public TimetableItem(String itemId, String subjectCode, String subjectName, String startingTime, String endingTime, DayEnum day, String lecturerInCharge, String location) {
        this.itemId = itemId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.day = day;
        this.lecturerInCharge = lecturerInCharge;
        this.location=location;
    }

    public TimetableItem(String timetableName, String itemId, String subjectCode, String subjectName, String startingTime, String endingTime, DayEnum day, String lecturerInCharge, String location) {
        this.timetableName = timetableName;
        this.itemId = itemId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.day = day;
        this.lecturerInCharge = lecturerInCharge;
        this.location=location;
    }

    public String getTimetableName() {
        return timetableName;
    }

    public void setTimetableName(String timetableName) {
        this.timetableName = timetableName;
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

    public DayEnum getDay() {
        return day;
    }

    public void setDay(DayEnum day) {
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
}
