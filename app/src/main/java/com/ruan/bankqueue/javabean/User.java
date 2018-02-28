package com.ruan.bankqueue.javabean;

import cn.bmob.v3.BmobUser;

/**
 * @author by ruan on 2017/12/15.
 */

public class User extends BmobUser{
    private Integer  integral;

    public Integer getIntegral() {
    return integral;
}

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }
}
