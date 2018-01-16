package com.ruan.bankqueue.javabean;

import cn.bmob.v3.BmobObject;

/**
 * @author by ruan on 2018/1/13.
 * 客户排队表
 */

public class Queue extends BmobObject {
    private Integer lineCode;
    private String bankName;
    private User user;
    private User lineUpRecord;
    private boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public User getLineUpRecord() {
        return lineUpRecord;
    }

    public void setLineUpRecord(User lineUpRecord) {
        this.lineUpRecord = lineUpRecord;
    }




    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getLineCode() {
        return lineCode;
    }

    public void setLineCode(Integer lineCode) {
        this.lineCode = lineCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
