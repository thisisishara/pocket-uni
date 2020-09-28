package com.example.pocketuni.model;

import java.util.Date;

public class NoticeItem {
    String noticeId;
    Date noticeDate;
    String noticeTitle;
    String noticeContent;
    String adminId;
    String year;
    String semester;
    String adminEmail;
    String adminName;
    String adminDp;

    public NoticeItem() {
    }

    public NoticeItem(String noticeId, Date noticeDate, String noticeTitle, String noticeContent, String adminId, String year, String semester, String adminEmail, String adminName, String adminDp) {
        this.noticeId = noticeId;
        this.noticeDate = noticeDate;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.adminId = adminId;
        this.year = year;
        this.semester = semester;
        this.adminEmail = adminEmail;
        this.adminName = adminName;
        this.adminDp = adminDp;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public Date getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(Date noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminDp() {
        return adminDp;
    }

    public void setAdminDp(String adminDp) {
        this.adminDp = adminDp;
    }
}
