package com.ruan.bankqueue.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.ruan.bankqueue.fragment.BusinessFragment;
import com.ruan.bankqueue.fragment.MyFragment;
import com.ruan.bankqueue.fragment.SubscribeFragment;
import com.ruan.bankqueue.other.BaseConstants;
import com.ruan.bankqueue.other.ExitPressed;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * @author by ruan
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationBar.OnTabSelectedListener {
    BusinessFragment businessFragment;
    MyFragment myFragment;
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
        ButterKnife.bind(this);
        context = getApplicationContext();
//        创建AppVersion表
        // TODO: 2017/12/19 创建成功即清理此行
//        BmobUpdateAgent.initAppVersion();
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(this);
        initToolbar();
        initDrawerLayout();
        initBottomNavigationBar();

    }


    private void initToolbar(){
        toolbar.setTitle("");
        tvToolbar.setText("选择业务");
        setSupportActionBar(toolbar);
    }
    /**
     * 设置底部导航图片和图标，添加底部导航的点击事件
     */
    private void initBottomNavigationBar() {
        navigation.setMode(BottomNavigationBar.MODE_FIXED);
        navigation.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        navigation.addItem(new BottomNavigationItem(R.drawable.ic_business, getString(R.string.title_business)).setActiveColorResource(R.color.bottomBar))
                .addItem(new BottomNavigationItem(R.drawable.ic_subscribe, getString(R.string.title_subscribe)).setActiveColorResource(R.color.bottomBar))
                .addItem(new BottomNavigationItem(R.drawable.ic_my, getString(R.string.title_my)).setActiveColorResource(R.color.bottomBar))
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
        businessFragment = BusinessFragment.newInstance(getString(R.string.title_business));
        transaction.replace(R.id.content, businessFragment);
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
                businessFragment = BusinessFragment.newInstance(getString(R.string.title_business));
                tvToolbar.setText("选择业务");
                transaction.replace(R.id.content, businessFragment);
                break;
            case 1:
                subscribeFragment = SubscribeFragment.newInstance(getString(R.string.title_subscribe));
                tvToolbar.setText(getString(R.string.title_subscribe));
                transaction.replace(R.id.content, subscribeFragment);
                break;
            case 2:

                myFragment = MyFragment.newInstance(getString(R.string.title_my));
                tvToolbar.setText(getString(R.string.title_my));
                transaction.replace(R.id.content, myFragment);
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
            Toast.makeText(context, "点击了我的资料", Toast.LENGTH_SHORT).show();
            // Handle the camera action
        } else if (id == R.id.nav_exit) {
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            startActivity(intent);
            BmobUser.logOut();
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
