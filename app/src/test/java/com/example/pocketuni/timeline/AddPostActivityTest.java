package com.example.pocketuni.timeline;

import org.junit.Test;

import static org.junit.Assert.*;

public class AddPostActivityTest {
    AddPostActivity addPostActivity = new AddPostActivity();

    @Test
    public void getAudience() {
        assertEquals("0", addPostActivity.getAudience("General"));
        assertEquals("1", addPostActivity.getAudience("1st Year"));
        assertEquals("2", addPostActivity.getAudience("2nd Year"));
        assertEquals("3", addPostActivity.getAudience("3rd Year"));
        assertEquals("4", addPostActivity.getAudience("4th Year"));
    }
}