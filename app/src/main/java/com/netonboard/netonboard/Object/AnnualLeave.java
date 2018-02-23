package com.netonboard.netonboard.Object;

public class AnnualLeave {
    private String s_date_apply;
    private String s_leave_remark;
    public AnnualLeave(){}

    public AnnualLeave(String s_date_apply, String s_leave_remark) {
        this.s_date_apply = s_date_apply;
        this.s_leave_remark = s_leave_remark;
    }

    public String getS_date_apply() {
        return s_date_apply;
    }

    public void setS_date_apply(String s_date_apply) {
        this.s_date_apply = s_date_apply;
    }

    public String getS_leave_remark() {
        return s_leave_remark;
    }

    public void setS_leave_remark(String s_leave_remark) {
        this.s_leave_remark = s_leave_remark;
    }
}
