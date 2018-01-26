package com.ruan.bankqueue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ruan.bankqueue.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author by ruan 2018/1/24
 *         修改密码
 */
public class FixPasswordActivity extends BaseActivity {

    @BindView(R.id.et_old)
    EditText etOld;
    @BindView(R.id.et_new)
    EditText etNew;
    @BindView(R.id.et_new1)
    EditText etNew1;
    @BindView(R.id.btn_fix)
    Button btnFix;
    String oldPassword,newPassword,new1Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_fix_password;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initToolbar();
        initWidget();
    }

    private void initToolbar() {
        setTitle("修改密码");
        setTopLeftButton(R.drawable.ic_return, new OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    private void initWidget(){
        btnFix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPassword = etOld.getText().toString();
                newPassword = etNew.getText().toString();
                new1Password = etNew1.getText().toString();
                if (newPassword.equals(new1Password)){
                    BmobUser.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(FixPasswordActivity.this, "密码修改成功",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                finish();
                            }else{
                                Toast.makeText(FixPasswordActivity.this, "修改失败:" + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                Log.e("FixPasswordActivity", e.getMessage() + e.getErrorCode());
                            }
                        }

                    });
                }else {
                    Toast.makeText(FixPasswordActivity.this, "两次密码输入不一致",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
