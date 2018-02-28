package com.ruan.bankqueue.javabean;

import cn.bmob.v3.BmobObject;

/**
 * @author by ruan on 2018/1/27.
 * 用户积分
 */

public class UserIntegral extends BmobObject {
    private User userId;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String phone;

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    private Integer integral;
}
