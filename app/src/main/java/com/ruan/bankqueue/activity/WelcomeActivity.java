package com.ruan.bankqueue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ruan.bankqueue.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;

/**
 * @author by ruan 2017-12-1
 */

public class WelcomeActivity extends AppCompatActivity {
    @BindView(R.id.button2)
    Button button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        Bmob.initialize(this,"1a64430728c9d575b7eb3117f2cf7e63");

    }

    @OnClick({R.id.button2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button2:
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;

        }

    }
}
