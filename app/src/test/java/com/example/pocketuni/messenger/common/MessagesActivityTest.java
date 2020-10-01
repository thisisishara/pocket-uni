package com.example.pocketuni.messenger.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessagesActivityTest {
    MessagesActivity messagesActivity = new MessagesActivity();

    @Test
    public void checkIsOnline() {
        String status = "online";
        String status2 = "offline";
        assertEquals(true, messagesActivity.checkIsOnline(status));
        assertEquals(false, messagesActivity.checkIsOnline(status2));
    }

    @Test
    public void isMessageEmpty() {
        String message1 = "";
        String message2 = null;
        String message3 = "Some Text";

        assertEquals(true, messagesActivity.isMessageEmpty(message1));
        assertEquals(true, messagesActivity.isMessageEmpty(message2));
        assertEquals(false, messagesActivity.isMessageEmpty(message3));
    }
}