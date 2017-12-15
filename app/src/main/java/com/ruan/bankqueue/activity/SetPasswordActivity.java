package com.ruan.bankqueue.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.util.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    String phone,code;
    @Override
    protected int getContentView() {
        return R.layout.activity_set_password;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("设置密码");
        phone = (String)getIntent().getSerializableExtra("phone");
        code = (String)getIntent().getSerializableExtra("code");
        LogUtil.e("手机验证----",phone+code);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }


}
