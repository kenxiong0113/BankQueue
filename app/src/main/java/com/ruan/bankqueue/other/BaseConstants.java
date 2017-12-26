package com.ruan.bankqueue.other;

/**
 * @author by ruan on 2017/12/15.
 * 基础常量类
 */

public class BaseConstants {
    public static final int CODE_AMAP_SUCCESS = 1000;
    public static final long PHONE_NUMBER = 11;
    public static final long VERIFICATION_CODE_LENGTH = 6;
    public static final long PASSWORD_LENGTH = 6;
    public static final int MESSAGE_NETWORK_REQUEST_IMAGE = 0x0001;
    public static final int MESSAGE_USER_CACHE_EXISTS = 0x0002;
    public static final int MESSAGE_USER_CACHE_INEXISTENCE = 0x0003;
    public static final int MESSAGE_REQUEST_IMAGE_SUCCESS = 0x0004;
    public static final int MESSAGE_REQUEST_IMAGE_FAIL = 0x0005;
    public static final int VERSION_GET_SUCCESS_MESSAGE = 0x1000;
    public static final int VERSION_GET_FAIL_MESSAGE = 0x1001;
    public static final int POI_SEARCH_ATM = 0x0002;
    public static class Action{
        public static final String ACTION_REGISTER = "com.ruan.action.register";
    }

    public static class RunTime {
        public static  long EXIT_TIME = 0;
        public static final int TIME = 2000;
    }



}
