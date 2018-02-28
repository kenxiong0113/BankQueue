package com.ruan.bankqueue.javabean;

import com.iflytek.cloud.thirdparty.S;

import cn.bmob.v3.BmobObject;

/**
 * @author  by ruan on 2018/1/28.
 */

public class FeedBack extends BmobObject {
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String phone;
    private String message;
}
