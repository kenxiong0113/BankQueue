package com.ruan.bankqueue.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.other.ExitPressed;
import com.ruan.bankqueue.util.LogUtil;
import com.ruan.bankqueue.util.PhoneNumber;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author by ruan
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_forget)
    TextView tvForget;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    String username, password;
    Context context;
    @BindView(R.id.progress)
    ProgressBar progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        context = getApplicationContext();
        toolbar.setVisibility(View.GONE);

    }

    @OnClick({R.id.tv_register, R.id.btn_login,R.id.tv_forget})

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                Intent intent = new Intent(getApplication(), RegisterActivity.class);
                intent.putExtra("title","手机验证注册");
                startActivity(intent);
                break;
            case R.id.btn_login:
                username = etPhone.getText().toString();
                password = etPassword.getText().toString();
                if (username.length() == BaseConstants.PHONE_NUMBER &&
                        password.length() >= BaseConstants.PASSWORD_LENGTH) {
                    String str = username.substring(0, 3);
                    PhoneNumber number = new PhoneNumber();
                    int pn = Integer.valueOf(str);
                    //判断手机号前3位是否正确
                    if (number.phoneNumber(pn)) {
                        progress.setVisibility(View.VISIBLE);
                        BmobUser bu2 = new BmobUser();
                        bu2.setUsername(username);
                        bu2.setPassword(password);
                        bu2.login(new SaveListener<BmobUser>() {
                            @Override
                            public void done(BmobUser bmobUser, BmobException e) {
                                if (e == null) {
//
                                    Intent intent = new Intent(getApplication(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                                    //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                                } else {

                                    LogUtil.e("LoginActivity", "e.getErrorCode():" + e.getErrorCode());
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progress.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        Toast.makeText(this, "请输入正确的手机号密码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "请输入正确的手机号密码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_forget:
                Intent intent1 = new Intent(this,RegisterActivity.class);
                intent1.putExtra("title","手机验证");
                startActivity(intent1);
                break;
            default:
                break;
        }
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
}
