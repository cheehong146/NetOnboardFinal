package com.netonboard.netonboard.Object;

public class Claim {

    private String d_submit;
    private String s_particular;
    private String f_total;

    public Claim(){}

    public Claim(String d_submit, String s_particular, String f_total) {
        this.d_submit = d_submit;
        this.s_particular = s_particular;
        this.f_total = f_total;
    }

    public String getD_submit() {
        return d_submit;
    }

    public void setD_submit(String d_submit) {
        this.d_submit = d_submit;
    }

    public String getS_particular() {
        return s_particular;
    }

    public void setS_particular(String s_particular) {
        this.s_particular = s_particular;
    }

    public String getF_total() {
        return f_total;
    }

    public void setF_total(String f_total) {
        this.f_total = f_total;
    }
}
