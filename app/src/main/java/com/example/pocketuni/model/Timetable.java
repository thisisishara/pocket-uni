package com.example.pocketuni.model;

import java.util.List;

public class Timetable {
    String timetableId;
    String year;
    String semester;
    String course;
    String batch;
    String name;
    List<TimetableItem> timetableItems = null;

    String reminderDate;
    String reminderTime;
    String reminderRepeatPeriod;

    public Timetable(String id, String year, String semester, String course, String batch, String name, List<TimetableItem> timetableItems) {
        this.timetableId = id;
        this.year = year;
        this.semester = semester;
        this.course = course;
        this.batch = batch;
        this.name = name;
        this.timetableItems = timetableItems;
    }

    public Timetable(String year, String semester, String course, String batch, String name, List<TimetableItem> timetableItems) {
        this.year = year;
        this.semester = semester;
        this.course = course;
        this.batch = batch;
        this.name = name;
        this.timetableItems = timetableItems;
    }

    public Timetable(String year, String semester, String course, String batch, String name) {
        this.year = year;
        this.semester = semester;
        this.course = course;
        this.batch = batch;
        this.name = name;
    }

    public Timetable(String id, String year, String semester, String course, String batch, String name) {
        this.timetableId = id;
        this.year = year;
        this.semester = semester;
        this.course = course;
        this.batch = batch;
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public String getSemester() {
        return semester;
    }

    public String getCourse() {
        return course;
    }

    public String getBatch() {
        return batch;
    }

    public String getName() {
        return name;
    }

    public List<TimetableItem> getTimetableItems() {
        return timetableItems;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimetableItems(List<TimetableItem> timetableItems) {
        this.timetableItems = timetableItems;
    }
}
