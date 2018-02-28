package com.ruan.bankqueue.application;

import android.app.Application;
import com.ruan.bankqueue.javabean.User;
import cn.bmob.v3.BmobUser;

/**
 * @author  by ruan on 2018/1/7.
 * 全局类
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    public static String bankName = null;
    public static Integer integral = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        getInstance();
    }
    public static MyApplication getInstance(){
        if (instance == null){
            instance = new MyApplication();
        }
        return instance;
    }
}
