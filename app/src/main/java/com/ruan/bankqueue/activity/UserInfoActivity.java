package com.ruan.bankqueue.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ruan.bankqueue.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

/**
 * @author by ruan 2018/1/24
 * 个人资料
 */
public class UserInfoActivity extends BaseActivity {
    BmobUser user;
    String userName = null;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_fixPassword)
    TextView tvFixPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initToolbar();
        initUserInfo();
        initWidget();
    }

    private void initUserInfo() {
            user = BmobUser.getCurrentUser();
            userName = user.getUsername();
            tvUsername.setText(userName);
    }

    private void initToolbar() {
        setTitle("我的资料");
        setTopLeftButton(R.drawable.ic_return, new OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    private void initWidget(){
        tvFixPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FixPasswordActivity.class));
                finish();
            }
        });
    }
}
