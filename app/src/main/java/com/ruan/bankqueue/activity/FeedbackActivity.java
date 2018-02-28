package com.ruan.bankqueue.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.javabean.FeedBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author by ruan 2018/1/28
 *         反馈界面
 */
public class FeedbackActivity extends BaseActivity {

    @BindView(R.id.et_feedback)
    EditText etFeedback;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initToolbar();
        setButtonListener();
    }

    private void initToolbar() {
        setTitle("反馈意见");
        setTopLeftButton(R.drawable.ic_return, new OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    private void setButtonListener(){
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etFeedback.getText().toString();
                if (message.length() < 10){
                    Toast.makeText(FeedbackActivity.this, "输入文字不能少于10个字",
                            Toast.LENGTH_SHORT).show();
                }else {
                    FeedBack back = new FeedBack();
                    back.setMessage(message);
                    back.setPhone(BmobUser.getCurrentUser().getUsername());
                    back.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null){
                                finish();
                                Toast.makeText(FeedbackActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(FeedbackActivity.this, e.getMessage() + e.getErrorCode(),
                                        Toast.LENGTH_SHORT).show();
                                Log.e("FeedbackActivity", e.getMessage() + e.getErrorCode());
                            }
                        }
                    });
                }

            }
        });
    }
}
