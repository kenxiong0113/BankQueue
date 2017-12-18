package com.ruan.bankqueue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.javabean.User;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.util.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author by ruan on 2017/12/15.
 */

public class SetPasswordActivity extends BaseActivity {
    @BindView(R.id.tv_password)
    TextView tvPassword;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_r_password)
    TextView tvRPassword;
    @BindView(R.id.et_r_password)
    EditText etRPassword;
    @BindView(R.id.btn_ok)
    Button btnOk;
    String phone, code;
    String password, rPassword;
    @BindView(R.id.progress)
    ProgressBar progress;

    @Override
    protected int getContentView() {
        return R.layout.activity_set_password;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initToolbar();
        phone = (String) getIntent().getSerializableExtra("phone");
        code = (String) getIntent().getSerializableExtra("code");
    }

    private void initToolbar(){
        setTitle("设置密码");
        setTopLeftButton(R.drawable.ic_return, new OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_ok})
    public void onClick(View view) {
        switch (R.id.btn_ok) {
            case R.id.btn_ok:
                progress.setVisibility(View.VISIBLE);
                password = etPassword.getText().toString();
                rPassword = etRPassword.getText().toString();
                if (password.length() >= BaseConstants.PASSWORD_LENGTH) {
                    if (password.equals(rPassword)) {
                        User user = new User();
                        //设置手机号码（必填）
                        user.setMobilePhoneNumber(phone);
                        //设置用户名，如果没有传用户名，则默认为手机号码
                        user.setUsername(phone);
                        //设置额外信息：此处为年龄
                        user.setPassword(password);
                        user.setMobilePhoneNumberVerified(true);
                        user.signOrLogin(code, new SaveListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                if (e == null) {
                                    Intent intent = new Intent(getApplication(),LoginActivity.class);
                                    intent.putExtra("username",phone);
                                    intent.putExtra("password",password);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(SetPasswordActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    LogUtil.e("smile", "" + user.getUsername() + "-" + user.getObjectId());
                                } else {
                                    finish();
                                    Toast.makeText(SetPasswordActivity.this, "失败:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "密码长度不能少于6位", Toast.LENGTH_SHORT).show();
                }
                progress.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

}
