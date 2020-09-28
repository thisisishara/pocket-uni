package com.example.pocketuni.model;

public class ChatList {
    private String chatId;

    public ChatList(String chatId) {
        this.chatId = chatId;
    }

    public ChatList (){

    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
