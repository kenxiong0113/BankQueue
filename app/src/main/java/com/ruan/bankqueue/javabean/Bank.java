package com.ruan.bankqueue.javabean;

import cn.bmob.v3.BmobObject;

/**
 * @author  by ruan on 2018/1/7.
 * 银行表
 * 1、银行名称
 * 2、银行窗口（动态变更） 默认9个
 */

public class Bank extends BmobObject{
    private String windows;
    private String bankName;
    private boolean state;
    private int img;
    private String employeeObjectId;

    public String getEmployeeObjectId() {
        return employeeObjectId;
    }

    public void setEmployeeObjectId(String employeeObjectId) {
        this.employeeObjectId = employeeObjectId;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getWindows() {
        return windows;
    }

    public void setWindows(String windows) {
        this.windows = windows;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Bank(String windows, int img){
        this.windows = windows;
        this.img = img;
    }

    public Bank(){

    }
}
