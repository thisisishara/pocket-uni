package com.example.pocketuni.model;

public class User {
    private String academic_year;
    private String batch;
    private String course;
    private String dp;
    private String email;
    private boolean isRemindersOn = false;
    private String name;
    private int remainderMinutes = -1;
    private String semester;
    private String userId;
    private String userType;

    public User(){
    }

    //constructor for students
    public User(String academic_year, String batch, String course, String dp, String email, boolean isRemindersOn, String name, int remainderMinutes, String semester, String userId, String userType) {
        this.academic_year = academic_year;
        this.batch = batch;
        this.course = course;
        this.dp = dp;
        this.email = email;
        this.isRemindersOn = isRemindersOn;
        this.name = name;
        this.remainderMinutes = remainderMinutes;
        this.semester = semester;
        this.userId = userId;
        this.userType = userType;
    }

    //constructor for admin
    public User(String dp, String email, String name, String userId, String userType) {
        this.dp = dp;
        this.email = email;
        this.name = name;
        this.userId = userId;
        this.userType = userType;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isRemindersOn() {
        return isRemindersOn;
    }

    public void setRemindersOn(boolean remindersOn) {
        isRemindersOn = remindersOn;
    }

    public int getRemainderMinutes() {
        return remainderMinutes;
    }

    public void setRemainderMinutes(int remainderMinutes) {
        this.remainderMinutes = remainderMinutes;
    }
}
