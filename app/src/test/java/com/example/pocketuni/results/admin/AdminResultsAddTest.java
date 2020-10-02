package com.example.pocketuni.results.admin;

import com.google.android.gms.common.internal.Asserts;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AdminResultsAddTest {
    private AdminResultsAdd adminResultsAdd;

    @Before
    public void setUp() throws Exception {
        adminResultsAdd = new AdminResultsAdd();
    }

    @Test
    public void getYearAndSemesterTest() {
        String result1 = adminResultsAdd.getYearAndSemester("Introduction to Programming");
        Assert.assertEquals("Year One Semester One", result1);

        String result2 = adminResultsAdd.getYearAndSemester("Introduction to Computer Systems");
        Assert.assertEquals("Year One Semester One", result2);

        String result3 = adminResultsAdd.getYearAndSemester("Mathematics for Computing");
        Assert.assertEquals("Year One Semester One", result3);

        String result4 = adminResultsAdd.getYearAndSemester("Communication Skills");
        Assert.assertEquals("Year One Semester One", result4);

        String result5 = adminResultsAdd.getYearAndSemester("Object Oriented Concepts");
        Assert.assertEquals("Year One Semester Two", result5);

        String result6 = adminResultsAdd.getYearAndSemester("Software Process Modeling");
        Assert.assertEquals("Year One Semester Two", result6);

        String result7 = adminResultsAdd.getYearAndSemester("English for Academic Purposes");
        Assert.assertEquals("Year One Semester Two", result7);

        String result8 = adminResultsAdd.getYearAndSemester("Information Systems & Data Modeling");
        Assert.assertEquals("Year One Semester Two", result8);

        String result9 = adminResultsAdd.getYearAndSemester("Internet and Web Technologies");
        Assert.assertEquals("Year One Semester Two", result9);

        String result10 = adminResultsAdd.getYearAndSemester("Software Engineering");
        Assert.assertEquals("Year Two Semester One", result10);

        String result11 = adminResultsAdd.getYearAndSemester("Object Oriented Programming");
        Assert.assertEquals("Year Two Semester One", result11);

        String result12 = adminResultsAdd.getYearAndSemester("Database Management Systems");
        Assert.assertEquals("Year Two Semester One", result12);

        String result13 = adminResultsAdd.getYearAndSemester("Computer Networks");
        Assert.assertEquals("Year Two Semester One", result13);

        String result14 = adminResultsAdd.getYearAndSemester("Operating Systems and System Administration");
        Assert.assertEquals("Year Two Semester One", result14);
    }
}
