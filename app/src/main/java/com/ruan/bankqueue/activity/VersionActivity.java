package com.ruan.bankqueue.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ruan.bankqueue.R;
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
    String versionId;


    @Override
    protected int getContentView() {
        return R.layout.activity_version;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        context = getApplicationContext();
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

    private void initDate(){
        SharedPreferences preferences = getSharedPreferences("version",MODE_PRIVATE);
        String ver=  preferences.getString("number","");
        tvVersion.setText(ver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick({R.id.tv_version_up})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_version_up:
                BmobUpdateAgent.forceUpdate(context);
                break;
            default:
                break;
        }
    }
    /**
     * 查询获取版本信息
     */
   private void acquireVersionNumber(){
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
               if(e == null){
                   for (AppVersion version : object) {
                       //获得最新版本号的信息
                       versionId = version.getVersion();
                       tvVersion.setText(versionId);
                       SharedPreferences.Editor preferences = getSharedPreferences("version",MODE_PRIVATE).edit();
                       preferences.putString("number", versionId);
                       preferences.apply();
                   }
               }else{
                   initDate();
                   LogUtil.e("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                   Toast.makeText(context, e.getMessage() + "," + e.getErrorCode(), Toast.LENGTH_SHORT).show();
               }
           }
       });
   }
}
