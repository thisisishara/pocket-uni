package com.example.pocketuni.results.std;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ResultsActivityTest {
    private ResultsActivity resultsActivity;

    @Before
    public void setUp() throws Exception {
        resultsActivity = new ResultsActivity();
    }

    @Test
    public void getGradePointTest() {
        double result1 = resultsActivity.getGradePoint("A+");
        Assert.assertEquals(4.0, result1, 0.001);

        double result2 = resultsActivity.getGradePoint("A");
        Assert.assertEquals(4.0, result2, 0.001);

        double result3 = resultsActivity.getGradePoint("A-");
        Assert.assertEquals(3.7, result3, 0.001);

        double result4 = resultsActivity.getGradePoint("B+");
        Assert.assertEquals(3.3, result4, 0.001);

        double result5 = resultsActivity.getGradePoint("B");
        Assert.assertEquals(3.0, result5, 0.001);

        double result6 = resultsActivity.getGradePoint("B-");
        Assert.assertEquals(2.7, result6, 0.001);

        double result7 = resultsActivity.getGradePoint("C+");
        Assert.assertEquals(2.3, result7, 0.001);

        double result8 = resultsActivity.getGradePoint("C");
        Assert.assertEquals(2.0, result8, 0.001);

        double result9 = resultsActivity.getGradePoint("C-");
        Assert.assertEquals(1.7, result9, 0.001);

        double result10 = resultsActivity.getGradePoint("D+");
        Assert.assertEquals(1.3, result10, 0.001);

        double result11 = resultsActivity.getGradePoint("D");
        Assert.assertEquals(1.0, result11, 0.001);

        double result12 = resultsActivity.getGradePoint("E");
        Assert.assertEquals(0.0, result12, 0.001);
    }
}
