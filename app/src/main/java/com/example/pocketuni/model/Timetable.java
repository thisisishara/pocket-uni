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

    String reminderDatesBefore;
    String reminderHoursBefore;
    String reminderMinutesBefore;
    String reminderRepeatPeriod;

}
