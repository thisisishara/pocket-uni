package com.example.pocketuni.messenger.common;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class MessageViewerAdapterTest {
    MessageViewerAdapter messageViewerAdapter = new MessageViewerAdapter();

    @Test
    public void getFormattedDateString() {
        Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        c1.set(Calendar.YEAR, 2015);
        c1.set(Calendar.MONTH, 11);
        c1.set(Calendar.DATE, 06);
        c1.set(Calendar.HOUR, 17);
        c1.set(Calendar.MINUTE, 3);
        c1.set(Calendar.SECOND, 0);

        Date date1 = c1.getTime();
        System.out.println(date1);

        assertEquals("06.12.2015 @ 05:03 PM", messageViewerAdapter.getFormattedDateString(date1));
    }
}