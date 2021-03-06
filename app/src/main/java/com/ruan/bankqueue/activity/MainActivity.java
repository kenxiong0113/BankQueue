package com.ruan.bankqueue.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.fragment.AtmFragment;
import com.ruan.bankqueue.fragment.QueueFragment;
import com.ruan.bankqueue.fragment.SubscribeFragment;
import com.ruan.bankqueue.other.ExitPressed;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;

/**
 * @author by ruan
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationBar.OnTabSelectedListener {
    AtmFragment atmFragment;
    QueueFragment queueFragment;
    SubscribeFragment subscribeFragment;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_toolbar)
    LinearLayout layoutToolbar;
    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar navigation;
    private ArrayList<Fragment> fragments;
    private Context context;
    SimpleDraweeView sdvHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryStatus));
        }
        ButterKnife.bind(this);
//        创建AppVersion表
        // TODO: 2017/12/19 创建成功即清理此行
//        BmobUpdateAgent.initAppVersion();
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(this);
        initToolbar();
        initDrawerLayout();
        initBottomNavigationBar();
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                // TODO Auto-generated method stub
                //根据updateStatus来判断更新是否成功
                Log.e("MainActivity", "updateStatus:" + updateStatus);
                Log.e("MainActivity", "updateInfo:" + updateInfo);
            }
        });

    }
    private void initToolbar(){
        toolbar.setTitle("");
        tvToolbar.setText("附近ATM");
        setSupportActionBar(toolbar);
    }
    /**
     * 设置底部导航图片和图标，添加底部导航的点击事件
     */
    private void initBottomNavigationBar() {
        navigation.setMode(BottomNavigationBar.MODE_FIXED);
        navigation.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        navigation.addItem(new BottomNavigationItem(R.drawable.ic_atm_bar, getString(R.string.title_atm)).setActiveColorResource(R.color.bottomBar))
                .addItem(new BottomNavigationItem(R.drawable.ic_queue, getString(R.string.title_queue)).setActiveColorResource(R.color.bottomBar))
                .addItem(new BottomNavigationItem(R.drawable.ic_subscribe, getString(R.string.title_subscribe)).setActiveColorResource(R.color.bottomBar))
                .setFirstSelectedPosition(0)
                .initialise();
        setDefaultFragment();
        navigation.setTabSelectedListener(this);
    }

    private void initDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        View drawView = navView.inflateHeaderView(R.layout.nav_header_main);
        sdvHead = (SimpleDraweeView) drawView.findViewById(R.id.sdv_head);
        navView.setNavigationItemSelectedListener(this);
        sdvHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "点击头像", Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     * 设置默认fragment
     */
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        atmFragment = AtmFragment.newInstance(getString(R.string.title_atm));
        transaction.replace(R.id.content, atmFragment);
        transaction.commit();
    }

    /**
     * 底部点击事件
     *
     * @param position
     */
    @Override
    public void onTabSelected(int position) {
        FragmentManager fm = getSupportFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                atmFragment = AtmFragment.newInstance(getString(R.string.title_atm));
                tvToolbar.setText("附近ATM");
                transaction.replace(R.id.content, atmFragment);

                break;
            case 1:
                queueFragment = QueueFragment.newInstance(getString(R.string.title_queue));
                tvToolbar.setText(getString(R.string.title_queue));
                transaction.replace(R.id.content, queueFragment);
                break;
            case 2:
                subscribeFragment = SubscribeFragment.newInstance(getString(R.string.title_subscribe));
                tvToolbar.setText(getString(R.string.title_subscribe));
                transaction.replace(R.id.content, subscribeFragment);
                break;
            default:
                break;
        }
        // 事务提交
        transaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(int position) {
    }

    /**
     * 按两次返回退出程序
     */
    @Override
    public void onBackPressed() {
        ExitPressed pressed = new ExitPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        //    2秒内按两次back退出应用程序
        else if (pressed.exitPressed(context)){
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_my_info) {
            startActivity(new Intent(context,UserInfoActivity.class));
        } else if (id == R.id.nav_version){
            startActivity(new Intent(context,VersionActivity.class));
        } else if (id == R.id.nav_my_queue){
            SharedPreferences preferences = getSharedPreferences("Bank",MODE_PRIVATE);
            if (preferences.getString("bankName","").equals("")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请注意！");
                builder.setMessage("请先到排队界面选择银行排队取号");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", null);
                builder.show();
            }else {
                Intent intent = new Intent(context,QueueActivity.class);
                intent.putExtra("bankName",preferences.getString("bankName",""));
                startActivity(intent);
            }

        }else if (id == R.id.nav_my_yy){
           startActivity(new Intent(this,MySubscribeActivity.class));

        }else if (id == R.id.nav_exit) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            BmobUser.logOut();
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
