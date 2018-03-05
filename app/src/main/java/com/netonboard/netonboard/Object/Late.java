package com.netonboard.netonboard.Object;

import java.sql.Date;

/**
 * Created by Netonboard on 26/2/2018.
 */

public class Late {
    private String date;
    private String minute;

    public Late(String date, String minute) {
        this.date = date;
        this.minute = minute;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }
}
