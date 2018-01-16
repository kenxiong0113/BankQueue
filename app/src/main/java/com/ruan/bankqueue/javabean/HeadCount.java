package com.ruan.bankqueue.javabean;

import cn.bmob.v3.BmobObject;

/**
 * @author  by ruan on 2018/1/13.
 */

public class HeadCount extends BmobObject {
    private String bankName;
    private Integer count;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
