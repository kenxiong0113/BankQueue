package com.ruan.bankqueue.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ruan.bankqueue.R;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.util.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.update.AppVersion;
import cn.bmob.v3.update.BmobUpdateAgent;


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
    String versionId = null;
    String ver;
    @BindView(R.id.progress)
    ProgressBar progress;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //网络获取版本号成功
                case BaseConstants.VERSION_GET_SUCCESS_MESSAGE:
                    if (versionId.equals(ver)) {
                        Toast.makeText(context, "当前版本已经是最新版本", Toast.LENGTH_SHORT).show();
                    } else {
                        BmobUpdateAgent.forceUpdate(context);
                    }
                    progress.setVisibility(View.GONE);
                    break;
                //网络获取版本号失败
                case BaseConstants.VERSION_GET_FAIL_MESSAGE:
                    if (versionId == null){
                        Toast.makeText(context, "请检查当前网络环境", Toast.LENGTH_SHORT).show();
                    }
                    progress.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return R.layout.activity_version;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        context = getApplicationContext();
        progress.setVisibility(View.VISIBLE);
        initToolbar();
        acquireVersionNumber();

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

    /**
     * 获取本地缓存版本号
     */
    private void getsTheLocalCacheVersionNumber() {
        SharedPreferences preferences = getSharedPreferences("version", MODE_PRIVATE);
        ver = preferences.getString("number", "");
        tvVersion.setText(ver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick({R.id.tv_version_up,R.id.tv_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_version_up:
                progress.setVisibility(View.VISIBLE);
                getsTheLocalCacheVersionNumber();
                acquireVersionNumber();
                break;
            case R.id.tv_feedback:
                    startActivity(new Intent(this,FeedbackActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 查询获取版本信息
     */
    private void acquireVersionNumber() {
        final Message message = new Message();
        BmobQuery<AppVersion> query = new BmobQuery<AppVersion>();
        //查询playerName叫“比目”的数据
        query.addWhereEqualTo("query", "ok");
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(1);
        // 根据score字段降序显示数据
        query.order("-createdAt");
        //执行查询方法
        query.findObjects(new FindListener<AppVersion>() {
            @Override
            public void done(List<AppVersion> object, BmobException e) {
                if (e == null) {
                    for (AppVersion version : object) {
                        //获得最新版本号的信息
                        versionId = version.getVersion();
                        tvVersion.setText(versionId);
                        message.what = BaseConstants.VERSION_GET_SUCCESS_MESSAGE;
                        handler.sendMessage(message);
                        SharedPreferences.Editor preferences = getSharedPreferences("version", MODE_PRIVATE).edit();
                        preferences.putString("number", versionId);
                        preferences.apply();

                    }
                } else {
                    getsTheLocalCacheVersionNumber();
                    message.what = BaseConstants.VERSION_GET_FAIL_MESSAGE;
                    handler.sendMessage(message);
                   }

            }
        });
    }
}
