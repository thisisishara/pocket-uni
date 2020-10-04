package com.example.pocketuni.results.admin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AdminResultsDisplayTest {
    private AdminResultsDisplay adminResultsDisplay;

    @Before
    public void setUp() throws Exception {
        adminResultsDisplay = new AdminResultsDisplay();
    }

    @Test
    public void getSpinnerModuleTest() {
        int result1 = adminResultsDisplay.getSpinnerModule("Communication Skills");
        Assert.assertEquals(0,result1);

        int result2 = adminResultsDisplay.getSpinnerModule("Computer Networks");
        Assert.assertEquals(1,result2);

        int result3 = adminResultsDisplay.getSpinnerModule("Database Management Systems");
        Assert.assertEquals(2,result3);

        int result4 = adminResultsDisplay.getSpinnerModule("English for Academic Purposes");
        Assert.assertEquals(3,result4);

        int result5 = adminResultsDisplay.getSpinnerModule("Information Systems and Data Modeling");
        Assert.assertEquals(4,result5);

        int result6 = adminResultsDisplay.getSpinnerModule("Internet and Web Technologies");
        Assert.assertEquals(5,result6);

        int result7 = adminResultsDisplay.getSpinnerModule("Introduction to Computer Systems");
        Assert.assertEquals(6,result7);

        int result8 = adminResultsDisplay.getSpinnerModule("Introduction to Programming");
        Assert.assertEquals(7,result8);

        int result9 = adminResultsDisplay.getSpinnerModule("Mathematics for Computing");
        Assert.assertEquals(8,result9);

        int result10 = adminResultsDisplay.getSpinnerModule("Object Oriented Concepts");
        Assert.assertEquals(9,result10);

        int result11 = adminResultsDisplay.getSpinnerModule("Object Oriented Programming");
        Assert.assertEquals(10,result11);

        int result12 = adminResultsDisplay.getSpinnerModule("Operating Systems and System Administration");
        Assert.assertEquals(11,result12);

        int result13 = adminResultsDisplay.getSpinnerModule("Software Engineering");
        Assert.assertEquals(12,result13);

        int result14 = adminResultsDisplay.getSpinnerModule("Software Process Modeling");
        Assert.assertEquals(13,result14);
    }

    @Test
    public void getSpinnerPeriodTest() {
        int result1 = adminResultsDisplay.getSpinnerPeriod("Jan-Jun");
        Assert.assertEquals(0,result1);

        int result2 = adminResultsDisplay.getSpinnerPeriod("Jun-Dec");
        Assert.assertEquals(1,result2);
    }

    @Test
    public void getSpinnerYearTest() {
        int result1 = adminResultsDisplay.getSpinnerYear("1999");
        Assert.assertEquals(0,result1);

        int result2 = adminResultsDisplay.getSpinnerYear("2000");
        Assert.assertEquals(1,result2);

        int result3 = adminResultsDisplay.getSpinnerYear("2001");
        Assert.assertEquals(2,result3);

        int result4 = adminResultsDisplay.getSpinnerYear("2002");
        Assert.assertEquals(3,result4);

        int result5 = adminResultsDisplay.getSpinnerYear("2003");
        Assert.assertEquals(4,result5);

        int result6 = adminResultsDisplay.getSpinnerYear("2004");
        Assert.assertEquals(5,result6);

        int result7 = adminResultsDisplay.getSpinnerYear("2005");
        Assert.assertEquals(6,result7);

        int result8 = adminResultsDisplay.getSpinnerYear("2006");
        Assert.assertEquals(7,result8);

        int result9 = adminResultsDisplay.getSpinnerYear("2007");
        Assert.assertEquals(8,result9);

        int result10 = adminResultsDisplay.getSpinnerYear("2008");
        Assert.assertEquals(9,result10);

        int result11 = adminResultsDisplay.getSpinnerYear("2009");
        Assert.assertEquals(10,result11);

        int result12 = adminResultsDisplay.getSpinnerYear("2010");
        Assert.assertEquals(11,result12);

        int result13 = adminResultsDisplay.getSpinnerYear("2011");
        Assert.assertEquals(12,result13);

        int result14 = adminResultsDisplay.getSpinnerYear("2012");
        Assert.assertEquals(13,result14);

        int result15 = adminResultsDisplay.getSpinnerYear("2013");
        Assert.assertEquals(14,result15);

        int result16 = adminResultsDisplay.getSpinnerYear("2014");
        Assert.assertEquals(15,result16);

        int result17 = adminResultsDisplay.getSpinnerYear("2015");
        Assert.assertEquals(16,result17);

        int result18 = adminResultsDisplay.getSpinnerYear("2016");
        Assert.assertEquals(17,result18);

        int result19 = adminResultsDisplay.getSpinnerYear("2017");
        Assert.assertEquals(18,result19);

        int result20 = adminResultsDisplay.getSpinnerYear("2018");
        Assert.assertEquals(19,result20);

        int result21 = adminResultsDisplay.getSpinnerYear("2019");
        Assert.assertEquals(20,result21);

        int result22 = adminResultsDisplay.getSpinnerYear("2020");
        Assert.assertEquals(21,result22);
    }

    @Test
    public void getSpinnerGradeTest() {
        int result1 = adminResultsDisplay.getSpinnerModule("A+");
        Assert.assertEquals(0,result1);

        int result2 = adminResultsDisplay.getSpinnerModule("A");
        Assert.assertEquals(0,result2);

        int result3 = adminResultsDisplay.getSpinnerModule("A-");
        Assert.assertEquals(0,result3);

        int result4 = adminResultsDisplay.getSpinnerModule("B+");
        Assert.assertEquals(0,result4);

        int result5 = adminResultsDisplay.getSpinnerModule("B");
        Assert.assertEquals(0,result5);

        int result6 = adminResultsDisplay.getSpinnerModule("B-");
        Assert.assertEquals(0,result6);

        int result7 = adminResultsDisplay.getSpinnerModule("C+");
        Assert.assertEquals(0,result7);

        int result8 = adminResultsDisplay.getSpinnerModule("C");
        Assert.assertEquals(0,result8);

        int result9 = adminResultsDisplay.getSpinnerModule("C-");
        Assert.assertEquals(0,result9);

        int result10 = adminResultsDisplay.getSpinnerModule("D+");
        Assert.assertEquals(0,result10);

        int result11 = adminResultsDisplay.getSpinnerModule("D");
        Assert.assertEquals(0,result11);

        int result12 = adminResultsDisplay.getSpinnerModule("E");
        Assert.assertEquals(0,result12);


    }
}
