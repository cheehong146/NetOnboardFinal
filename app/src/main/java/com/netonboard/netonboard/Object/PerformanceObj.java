package com.netonboard.netonboard.Object;

/**
 * Created by Netonboard on 6/3/2018.
 */

public class PerformanceObj {
    private String year;
    private float score;
    private float attendance;

    public PerformanceObj(String year, float score, float attendance) {
        this.year = year;
        this.score = score;
        this.attendance = attendance;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getAttendance() {
        return attendance;
    }

    public void setAttendance(float attendance) {
        this.attendance = attendance;
    }
}
