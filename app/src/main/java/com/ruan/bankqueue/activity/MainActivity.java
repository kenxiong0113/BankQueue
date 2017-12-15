package com.ruan.bankqueue.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ruan.bankqueue.R;
import com.ruan.bankqueue.fragment.BusinessFragment;
import com.ruan.bankqueue.fragment.MyFragment;
import com.ruan.bankqueue.fragment.SubscribeFragment;

import java.util.ArrayList;

/**
 * @author by ruan
 */
public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener{
    BottomNavigationBar navigation;
    BusinessFragment businessFragment;
    MyFragment myFragment;
    SubscribeFragment subscribeFragment;
    private ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initBottomNavigationBar();

    }

    /**
     * 设置底部导航图片和图标，添加底部导航的点击事件
     */
    private void initBottomNavigationBar() {
        navigation = (BottomNavigationBar) findViewById(R.id.navigation);
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

    /**
     * 设置默认fragment
     */
    private void setDefaultFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        businessFragment = BusinessFragment.newInstance(getString(R.string.title_business));
        transaction.replace(R.id.content, businessFragment);
        transaction.commit();

    }

    /**
     * 底部点击事件
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
                setTitle(getString(R.string.title_business));
                transaction.replace(R.id.content, businessFragment);
                break;
            case 1:
                subscribeFragment = SubscribeFragment.newInstance(getString(R.string.title_subscribe));
                setTitle(getString(R.string.title_subscribe));
                transaction.replace(R.id.content, subscribeFragment);
                break;
            case 2:
                myFragment = MyFragment.newInstance(getString(R.string.title_my));
                setTitle(getString(R.string.title_my));
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

}
