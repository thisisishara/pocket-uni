package com.example.pocketuni.model;

import java.util.Date;

public class Message {
    private String messageBody = "N/A";
    private String senderId = "N/A";
    private String receiverId = "N/A";
    private Date sentDate;
    private Boolean deliveredStatus = false;
    private Boolean readStatus = false;

    public Message(){

    }

    public Message(String messageBody, String senderId, String receiverId) {
        this.messageBody = messageBody;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Message(String messageBody, String senderId, String receiverId, Date sentDate, Boolean deliveredStatus, Boolean readStatus) {
        this.messageBody = messageBody;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.sentDate = sentDate;
        this.deliveredStatus = deliveredStatus;
        this.readStatus = readStatus;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Boolean getDeliveredStatus() {
        return deliveredStatus;
    }

    public void setDeliveredStatus(Boolean deliveredStatus) {
        this.deliveredStatus = deliveredStatus;
    }

    public Boolean getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
    }
}