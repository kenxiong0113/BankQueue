package com.ruan.bankqueue.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.javabean.WelcomePicture;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.other.ExitPressed;
import com.ruan.bankqueue.other.GetPicThread;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author by ruan 2017-12-1
 */

public class WelcomeActivity extends AppCompatActivity {
    @BindView(R.id.btn_jump)
    Button btnJump;
    Context context;
    BmobUser bmobUser;
    BmobFile file;
    @BindView(R.id.img_picture)
    ImageView imgPicture;
    Message message = new Message();
    @BindView(R.id.progress)
    ProgressBar progress;
    CountDownTimer count;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BaseConstants.MESSAGE_NETWORK_REQUEST_IMAGE:
                    Bitmap bm = (Bitmap) msg.obj;
                    imgPicture.setImageBitmap(bm);
                    message.what = BaseConstants.MESSAGE_REQUEST_IMAGE_SUCCESS;
                    handler.sendMessage(message);
                    break;
                case BaseConstants.MESSAGE_REQUEST_IMAGE_SUCCESS:
                    progress.setVisibility(View.GONE);
                    jump();
                    break;
                case BaseConstants.MESSAGE_REQUEST_IMAGE_FAIL:
                    progress.setVisibility(View.GONE);
                    checkTheUserCache();
                    Toast.makeText(context, "加载图片失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置状态栏颜色
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.transparency),true);
        ButterKnife.bind(this);
        Bmob.initialize(this, "1a64430728c9d575b7eb3117f2cf7e63");
        context = getApplicationContext();
        bmobUser = BmobUser.getCurrentUser();
        progress.setVisibility(View.VISIBLE);
        initStartThePictures();
        btnJump.setVisibility(View.GONE);
    }

    @OnClick({R.id.btn_jump})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_jump:
                //点击跳过按钮，取消正在执行的倒计时线程
                count.cancel();
               checkTheUserCache();
                break;
            default:
                break;
        }
    }

    /**
     * 检查用户缓存
     */
    private void checkTheUserCache() {
        if (bmobUser != null) {
            // 允许用户使用应用
            startActivity(new Intent(getApplication(), MainActivity.class));
            finish();
        } else {
            //缓存用户对象为空时， 可打开用户登录界面…
            startActivity(new Intent(getApplication(), LoginActivity.class));
            finish();
        }
    }

    /**
     * 获取启动页图片
     */
    private void initStartThePictures() {
        BmobQuery bmobQuery = new BmobQuery();
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(new FindListener<WelcomePicture>() {
            @Override
            public void done(List<WelcomePicture> object, BmobException e) {
                if (e == null) {
                    for (WelcomePicture picture : object) {
                        BmobFile bmobfile = picture.getPicture();
                        String url = bmobfile.getUrl();
                        file = new BmobFile(new File(url));
                        if (file != null) {
                            GetPicThread gpt = new GetPicThread(url, handler);
                            Thread t = new Thread(gpt);
                            t.start();
                            //调用bmobfile.download方法
                        } else {
                            message.what = BaseConstants.MESSAGE_REQUEST_IMAGE_FAIL;
                            handler.sendMessage(message);
                        }
                    }
                } else {
                    checkTheUserCache();
                    Toast.makeText(context, "加载广告页失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 按两次返回退出程序
     */
    @Override
    public void onBackPressed() {
        ExitPressed pressed = new ExitPressed();
        boolean isPressed = pressed.exitPressed(context);
        if (isPressed) {
            finish();
        }
    }

    private void jump(){
        btnJump.setVisibility(View.VISIBLE);
         count = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnJump.setText(String.format("跳过(%ds)", millisUntilFinished / 1000));
            }
            @Override
            public void onFinish() {
               checkTheUserCache();
            }
        }.start();
    }
}
