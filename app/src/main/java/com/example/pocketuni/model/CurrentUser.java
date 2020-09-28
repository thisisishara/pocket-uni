package com.example.pocketuni.model;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

public class CurrentUser {
    private static String name, email, userId, userType, dp = "default";
    private static  String year, semester, batch;
    private static  String course;
    private static  Image profilePicture;
    private static boolean isRemindersOn = false;
    private static int remainderMinutes = -1;
    private static Timetable timetable;
    private static List<TimetableItem> timetableItems = new ArrayList<TimetableItem>();

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        CurrentUser.name = name;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        CurrentUser.email = email;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        CurrentUser.userId = userId;
    }

    public static String getYear() {
        return year;
    }

    public static void setYear(String year) {
        CurrentUser.year = year;
    }

    public static String getSemester() {
        return semester;
    }

    public static void setSemester(String semester) {
        CurrentUser.semester = semester;
    }

    public static String getBatch() {
        return batch;
    }

    public static void setBatch(String batch) {
        CurrentUser.batch = batch;
    }

    public static String getCourse() {
        return course;
    }

    public static void setCourse(String course) {
        CurrentUser.course = course;
    }

    public static Image getProfilePicture() {
        return profilePicture;
    }

    public static void setProfilePicture(Image profilePicture) {
        CurrentUser.profilePicture = profilePicture;
    }

    public static String getUserType() {
        return userType;
    }

    public static void setUserType(String userType) {
        CurrentUser.userType = userType;
    }

    public static String getDp() {
        return dp;
    }

    public static void setDp(String dp) {
        CurrentUser.dp = dp;
    }

    public static Timetable getTimetable() {
        return timetable;
    }

    public static void setTimetable(Timetable timetable) {
        CurrentUser.timetable = timetable;
    }

    public static List<TimetableItem> getTimetableItems() {
        return timetableItems;
    }

    public static void setTimetableItems(List<TimetableItem> timetableItems) {
        CurrentUser.timetableItems = timetableItems;
    }

    public static boolean isIsRemindersOn() {
        return CurrentUser.isRemindersOn;
    }

    public static void setIsRemindersOn(boolean isRemindersOn) {
        CurrentUser.isRemindersOn = isRemindersOn;
    }

    public static int getRemainderMinutes() {
        return remainderMinutes;
    }

    public static void setRemainderMinutes(int remainderMinutes) {
        CurrentUser.remainderMinutes = remainderMinutes;
    }
}
