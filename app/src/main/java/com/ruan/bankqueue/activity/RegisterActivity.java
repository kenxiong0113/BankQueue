package com.ruan.bankqueue.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.util.LogUtil;
import com.ruan.bankqueue.util.PhoneNumber;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * @author by ruan 2017/12/15.
 */

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.tv_verification_code)
    TextView tvVerificationCode;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.btn_code)
    Button btnCode;
    @BindView(R.id.btn_next_step)
    Button btnNextStep;
    String phone,code;
    boolean isPhone = false;
    Context mContext;
    @Override
    protected int getContentView() {
        return R.layout.activity_register;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
       initToolbar();
        mContext = getApplicationContext();

    }

    private void initToolbar(){
        setTitle("手机验证注册");
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

    @OnClick({R.id.btn_code,R.id.btn_next_step})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_code:
                phone = etPhone.getText().toString();
                if (phone.length() == BaseConstants.PHONE_NUMBER) {
                    String strP3 = phone.substring(0, 3);
                    int p3 = Integer.valueOf(strP3);
                    PhoneNumber number = new PhoneNumber();
                    if (number.phoneNumber(p3)) {
                        //重新获取验证码倒计时
                        new CountDownTimer(60000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                btnCode.setEnabled(false);
                                btnCode.setText(String.format("重新获取(%d秒)", millisUntilFinished / 1000));
                            }
                            @Override
                            public void onFinish() {
                                btnCode.setEnabled(true);
                                btnCode.setText("发送验证码");
                            }
                        }.start();
                        BmobSMS.requestSMSCode(phone, "银行排队", new QueryListener<Integer>() {
                            @Override
                            public void done(Integer smsId, BmobException ex) {
                                if (ex == null) {
                                    //验证码发送成功
                                    //用于查询本次短信发送详情
                                    LogUtil.e("smile", "短信id：" + smsId);
                                    isPhone = true;
                                } else {
                                    btnCode.setEnabled(true);
                                    btnCode.setText("发送验证码");
                                    Toast.makeText(RegisterActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {

                        Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_next_step:
                    code = etCode.getText().toString();
                    if (isPhone && code.length() == BaseConstants.VERIFICATION_CODE_LENGTH){
                        Intent intent = new Intent(getApplication(),SetPasswordActivity.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("code",code);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(this, "请输入正确的手机号和验证码", Toast.LENGTH_SHORT).show();
                    }
                break;

            default:

                break;

        }
    }

}
