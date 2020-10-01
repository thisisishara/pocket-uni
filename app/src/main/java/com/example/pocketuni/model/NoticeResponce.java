package com.example.pocketuni.model;

public class NoticeResponce {
    private String userId;
    private String noticeId;

    public NoticeResponce() {
    }

    public NoticeResponce(String userId) {
        this.userId = userId;
    }

    public NoticeResponce(String userId, String noticeId) {
        this.userId = userId;
        this.noticeId = noticeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }
}
