package com.ruan.bankqueue.other;

import android.content.Context;
import android.os.Process;
import android.widget.Toast;

/**
 * 按两次退出程序
 * @author by ruan on 2017/12/16.
 */

public class ExitPressed {
    public boolean exitPressed(Context context){
        boolean isExit = false;
        if (System.currentTimeMillis() - BaseConstants.RunTime.EXIT_TIME > BaseConstants.RunTime.TIME) {
            Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            BaseConstants.RunTime.EXIT_TIME = System.currentTimeMillis();
            isExit = false;
        } else {
//            System.exit(0);
//            Process.killProcess(Process.myPid());
            isExit = true;
        }
        return isExit;
    }

}
