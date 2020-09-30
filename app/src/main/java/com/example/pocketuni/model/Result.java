package com.example.pocketuni.model;

public class Result {
    String regNum, module, caMarks, grades, period, year;

    public Result() {
    }

    public Result(String regNum, String module, String caMarks, String grades, String period, String year) {
        this.regNum = regNum;
        this.module = module;
        this.caMarks = caMarks;
        this.grades = grades;
        this.period = period;
        this.year = year;
    }

    public Result(String module) {
        this.module = module;
    }

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getCaMarks() {
        return caMarks;
    }

    public void setCaMarks(String caMarks) {
        this.caMarks = caMarks;
    }

    public String getGrades() {
        return grades;
    }

    public void setGrades(String grades) {
        this.grades = grades;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
