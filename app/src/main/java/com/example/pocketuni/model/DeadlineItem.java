package com.example.pocketuni.model;

public class DeadlineItem {
    String deadlineid;
    String subjectcode;
    String subjectName;
    String assignmentName;
    String deadlineDate;
    String submissionInstructions;
    String lecturerInCharge;
    String contactInfo;
    String year;
    String semester;

    String reminderDate;
    String reminderTime;
    String reminderRepeatPeriod;

    public DeadlineItem(String deadlineid, String subjectcode, String subjectName, String assignmentName, String deadlineDate, String submissionInstructions, String lecturerInCharge, String contactInfo, String reminderDate, String reminderTime, String reminderRepeatPeriod) {
        this.deadlineid = deadlineid;
        this.subjectcode = subjectcode;
        this.subjectName = subjectName;
        this.assignmentName = assignmentName;
        this.deadlineDate = deadlineDate;
        this.submissionInstructions = submissionInstructions;
        this.lecturerInCharge = lecturerInCharge;
        this.contactInfo = contactInfo;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
        this.reminderRepeatPeriod = reminderRepeatPeriod;
    }

    public String getDeadlineid() {
        return deadlineid;
    }

    public void setDeadlineid(String deadlineid) {
        this.deadlineid = deadlineid;
    }

    public String getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(String subjectcode) {
        this.subjectcode = subjectcode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getSubmissionInstructions() {
        return submissionInstructions;
    }

    public void setSubmissionInstructions(String submissionInstructions) {
        this.submissionInstructions = submissionInstructions;
    }

    public String getLecturerInCharge() {
        return lecturerInCharge;
    }

    public void setLecturerInCharge(String lecturerInCharge) {
        this.lecturerInCharge = lecturerInCharge;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
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

    public String getReminderRepeatPeriod() {
        return reminderRepeatPeriod;
    }

    public void setReminderRepeatPeriod(String reminderRepeatPeriod) {
        this.reminderRepeatPeriod = reminderRepeatPeriod;
    }
}
