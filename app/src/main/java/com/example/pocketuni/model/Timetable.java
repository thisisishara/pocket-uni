package com.example.pocketuni.model;

import java.util.Date;
import java.util.List;

public class Timetable {
    String timetable_name;
    String timetable_year;
    String timetable_semester;
    String timetable_course;
    String timetable_batch;
    List<TimetableItem> timetableItems = null;
    Boolean timetable_reminders = false;
    int timetable_hours = 0;
    int timetable_minutes = 0;

    public Timetable(){

    }

    public Timetable(String timetable_name, String timetable_year, String timetable_semester, String timetable_course, String timetable_batch, Boolean timetable_reminders, int timetable_hours, int timetable_minutes) {
        this.timetable_name = timetable_name;
        this.timetable_year = timetable_year;
        this.timetable_semester = timetable_semester;
        this.timetable_course = timetable_course;
        this.timetable_batch = timetable_batch;
        this.timetable_reminders = timetable_reminders;
        this.timetable_hours = timetable_hours;
        this.timetable_minutes = timetable_minutes;
    }

    public Timetable(String timetable_name, String timetable_year, String timetable_semester, String timetable_course, String timetable_batch, List<TimetableItem> timetableItems, Boolean timetable_reminders, int timetable_hours, int timetable_minutes) {
        this.timetable_name = timetable_name;
        this.timetable_year = timetable_year;
        this.timetable_semester = timetable_semester;
        this.timetable_course = timetable_course;
        this.timetable_batch = timetable_batch;
        this.timetableItems = timetableItems;
        this.timetable_reminders = timetable_reminders;
        this.timetable_hours = timetable_hours;
        this.timetable_minutes = timetable_minutes;
    }

    public String getTimetable_name() {
        return timetable_name;
    }

    public void setTimetable_name(String timetable_name) {
        this.timetable_name = timetable_name;
    }

    public String getTimetable_year() {
        return timetable_year;
    }

    public void setTimetable_year(String timetable_year) {
        this.timetable_year = timetable_year;
    }

    public String getTimetable_semester() {
        return timetable_semester;
    }

    public void setTimetable_semester(String timetable_semester) {
        this.timetable_semester = timetable_semester;
    }

    public String getTimetable_course() {
        return timetable_course;
    }

    public void setTimetable_course(String timetable_course) {
        this.timetable_course = timetable_course;
    }

    public String getTimetable_batch() {
        return timetable_batch;
    }

    public void setTimetable_batch(String timetable_batch) {
        this.timetable_batch = timetable_batch;
    }

    public List<TimetableItem> getTimetableItems() {
        return timetableItems;
    }

    public void setTimetableItems(List<TimetableItem> timetableItems) {
        this.timetableItems = timetableItems;
    }

    public Boolean getTimetable_reminders() {
        return timetable_reminders;
    }

    public void setTimetable_reminders(Boolean timetable_reminders) {
        this.timetable_reminders = timetable_reminders;
    }

    public int getTimetable_hours() {
        return timetable_hours;
    }

    public void setTimetable_hours(int timetable_hours) {
        this.timetable_hours = timetable_hours;
    }

    public int getTimetable_minutes() {
        return timetable_minutes;
    }

    public void setTimetable_minutes(int timetable_minutes) {
        this.timetable_minutes = timetable_minutes;
    }
}
