package com.example.pocketuni.organizer.std;

import org.junit.Test;
import static org.junit.Assert.*;

public class ViewTimetableActivityTest {
    ViewTimetableActivity viewTimetableActivity = new ViewTimetableActivity();

    @Test
    public void isGreaterThanZero() {
        assertEquals (true, viewTimetableActivity.isGreaterThanZero(56));
        assertEquals (false, viewTimetableActivity.isGreaterThanZero(0));
        assertEquals (false, viewTimetableActivity.isGreaterThanZero(-1));
    }

    @Test
    public void getDayLongNameOfDate() {
        assertEquals("Monday", viewTimetableActivity.getDayLongNameOfDate(1));
        assertEquals("Tuesday", viewTimetableActivity.getDayLongNameOfDate(2));
        assertEquals("Wednesday", viewTimetableActivity.getDayLongNameOfDate(3));
        assertEquals("Thursday", viewTimetableActivity.getDayLongNameOfDate(4));
        assertEquals("Friday", viewTimetableActivity.getDayLongNameOfDate(5));
        assertEquals("Saturday", viewTimetableActivity.getDayLongNameOfDate(6));
        assertEquals("Sunday", viewTimetableActivity.getDayLongNameOfDate(7));
        assertEquals("N/A", viewTimetableActivity.getDayLongNameOfDate(0));
    }
}