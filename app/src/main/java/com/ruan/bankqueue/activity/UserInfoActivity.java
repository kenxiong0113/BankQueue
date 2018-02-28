package com.ruan.bankqueue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.javabean.User;
import com.ruan.bankqueue.javabean.UserIntegral;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author by ruan 2018/1/24
 *         个人资料
 */
public class UserInfoActivity extends BaseActivity {
    User user;
    String userName = null;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_fixPassword)
    TextView tvFixPassword;
    @BindView(R.id.tv_integral)
    TextView tvIntegral;

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
        user = BmobUser.getCurrentUser(User.class);
        userName = user.getUsername();
        tvUsername.setText("用户名\t\t\t\t\t\t\t\t"+userName);
        BmobQuery<UserIntegral> query = new BmobQuery<UserIntegral>();
        query.addWhereEqualTo("phone",userName);
        query.findObjects(new FindListener<UserIntegral>() {
            @Override
            public void done(List<UserIntegral> list, BmobException e) {
                if (e == null){
                    Integer i = list.get(0).getIntegral();
                    tvIntegral.setText("积分\t\t\t\t\t\t\t\t\t\t"+String.valueOf(i));
                }else {
                    Toast.makeText(UserInfoActivity.this, e.getMessage() + e.getErrorCode(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("UserInfoActivity", e.getMessage() + e.getErrorCode());
                }
            }
        });
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

    private void initWidget() {
        tvFixPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FixPasswordActivity.class));
                finish();
            }
        });
    }
}
