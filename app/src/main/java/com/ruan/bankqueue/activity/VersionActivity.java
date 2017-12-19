package com.ruan.bankqueue.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ruan.bankqueue.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author by ken
 */
public class VersionActivity extends BaseActivity {

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_version_up)
    TextView tvVersionUp;
    @BindView(R.id.tv_function_introduction)
    TextView tvFunctionIntroduction;
    @BindView(R.id.tv_feedback)
    TextView tvFeedback;
    @BindView(R.id.tv_terms_of_service)
    TextView tvTermsOfService;
    Context context;

    @Override
    protected int getContentView() {
        return R.layout.activity_version;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        context = getApplicationContext();
        initToolbar();
    }

    void initToolbar() {
        setTitle("版本信息");
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
    @OnClick({R.id.tv_version_up})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_version_up:

                break;
            default:
                break;
        }
    }


}
