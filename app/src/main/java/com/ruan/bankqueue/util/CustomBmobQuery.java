package com.ruan.bankqueue.util;

import com.iflytek.cloud.thirdparty.T;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/1/17.
 */

public class CustomBmobQuery {
    private String vrg;


    private void query(){
        BmobQuery<T> query = new BmobQuery<T>();
        query.addWhereEqualTo(vrg,vrg);
        query.findObjects(new FindListener<T>() {
            @Override
            public void done(List<T> list, BmobException e) {
                
            }
        });

    }

}

