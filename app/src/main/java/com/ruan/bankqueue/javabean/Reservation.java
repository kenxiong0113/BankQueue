package com.ruan.bankqueue.javabean;

import cn.bmob.v3.BmobObject;

/**
 * @author by ruan on 2018/1/28.
 */

public class Reservation extends BmobObject {
    private boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    private User userId;
    private String time;
    private String phone;
    private String bankName;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Reservation(String time,String bankName){
        this.time = time;
        this.bankName = bankName;
    }
    public Reservation(){
    }
}
